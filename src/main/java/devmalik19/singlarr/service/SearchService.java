package devmalik19.singlarr.service;

import devmalik19.singlarr.constants.SearchStatus;
import devmalik19.singlarr.data.dao.Blocklist;
import devmalik19.singlarr.data.dao.Library;
import devmalik19.singlarr.data.dao.Search;
import devmalik19.singlarr.data.dto.DownloadRequest;
import devmalik19.singlarr.data.dto.MetadataResult;
import devmalik19.singlarr.data.dto.SearchResult;
import devmalik19.singlarr.helper.PaginationHelper;
import devmalik19.singlarr.helper.PriorityHelper;
import devmalik19.singlarr.repository.BlocklistRepository;
import devmalik19.singlarr.repository.LibraryRepository;
import devmalik19.singlarr.repository.SearchRepository;
import devmalik19.singlarr.service.plugins.PluginsService;
import devmalik19.singlarr.service.thirdparty.NetworkService;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchService
{
	private static final Logger logger = LoggerFactory.getLogger(SearchService.class);

	private final NetworkService networkService;
	private final PluginsService pluginsService;
	private final DownloadService downloadService;
	private final SearchRepository searchRepository;
	private final BlocklistRepository blocklistRepository;
	private final LibraryRepository libraryRepository;

	private static List<Entry<String, Integer>> sortedServices;

	public SearchService(NetworkService networkService,
						 PluginsService pluginsService,
						 DownloadService downloadService,
						 SearchRepository searchRepository,
						 BlocklistRepository blocklistRepository,
						 LibraryRepository libraryRepository)
	{
		this.networkService = networkService;
		this.pluginsService = pluginsService;
		this.downloadService = downloadService;
		this.searchRepository = searchRepository;
		this.blocklistRepository = blocklistRepository;
		this.libraryRepository = libraryRepository;
	}

	public void setPriorityOrder()
	{
		HashMap<String, Integer> priority = PriorityHelper.getPriority();

		sortedServices = priority.entrySet().stream()
			.filter(entry -> entry.getValue() != 0)
			.sorted(Entry.comparingByValue()).toList();
	}

	public Page<SearchResult> interactiveSearch(Integer id, Pageable pageable) throws Exception
	{
		Search search = getSearchById(id);
		List<SearchResult> searchResults = Arrays.asList(networkService.getSearchResults(search.getArtist() + " " + search.getTitle()));
		return PaginationHelper.prepareResults(searchResults, pageable);
	}

	public void addToDownloadClients(DownloadRequest downloadRequest) throws Exception
	{
		networkService.addToDownloadClients(downloadRequest);
	}

	public List<Search> getAllSearchHistory()
	{
		return searchRepository.findAll();
	}

	public void triggerSearch()
	{
		logger.info("Starting search engine!");
		List<Search> searchList = searchRepository.findByStatus(SearchStatus.NEW);
		searchList.forEach(this::searchEntry);
		logger.info("Search engine finish!");
	}

	@Async
	public void add(MetadataResult metadataResult)
	{
		logger.info("Adding  {} in search queue", metadataResult.getTitle());
		Search search = new Search();
		search.setTitle(metadataResult.getTitle());
		search.setArtist(metadataResult.getArtists());
		search.setAlbum(metadataResult.getAlbums());
		search.setYear(metadataResult.getYear());
		search.setStatus(SearchStatus.NEW);
		Optional<Library> optionalLibrary = libraryRepository.findById(metadataResult.getLibrary());
		optionalLibrary.ifPresent(search::setLibrary);
		search = searchRepository.save(search);
		searchEntry(search);
	}

	public void searchEntry(Search search)
	{
		logger.info("Searching for {} with priority order {}", search.getTitle(), sortedServices);
		search.setStatus(SearchStatus.SEARCHING);
		searchRepository.save(search);

		Set<String> blocklist = getBlocklistIdentifiers(search);

		boolean isSuccess;
		for (Entry<String, Integer> entry : sortedServices)
		{
			try
			{
				if (NetworkService.services.contains(entry.getKey()))
				{
					isSuccess = networkService.search(search, blocklist);
				}
				else
				{
					isSuccess = pluginsService.search(search, blocklist);
				}

				if (isSuccess)
					break;
			}
			catch (Exception e)
			{
				logger.debug(e.getLocalizedMessage());
			}
		}
		logger.info("Search for {} finish", search.getTitle());
	}

	private Set<String> getBlocklistIdentifiers(Search search)
	{
		return blocklistRepository.findBySearch(search).stream()
			.map(Blocklist::getIdentifier)
			.collect(Collectors.toSet());
	}

	public void reset()
	{
		searchRepository.update(SearchStatus.NOTFOUND, SearchStatus.NEW);
	}

	public void delete(int id)
	{
		Optional<Search> opt = searchRepository.findById(id);
		opt.ifPresent(searchRepository::delete);
	}

	public Search getSearchById(Integer id)
	{
		return searchRepository.findById(id).orElseThrow();
	}

	public void checkDownloads()
	{
		List<Search> searchList = searchRepository.findByStatus(SearchStatus.DOWNLOADING);
		searchList.forEach(this::processDownloadCheck);
	}

	@Transactional
	protected void processDownloadCheck(Search search)
	{
		try
		{
			SearchStatus previousStatus = search.getStatus();
			Set<String> blocklist = getBlocklistIdentifiers(search);
			networkService.checkDownloads(search);
			pluginsService.checkDownloads(search, blocklist);

			if (search.getStatus() != previousStatus)
			{
				if (search.getStatus() == SearchStatus.COMPLETED)
				{
					downloadService.process(search);
					searchRepository.save(search);
				}
				else if (search.getStatus() == SearchStatus.FAILED)
				{
					addToBlocklist(search);
					search.setStatus(SearchStatus.NEW);
					search.setData(null);
					searchRepository.save(search);
					logger.info("Download failed for search id={}, added to blocklist. Retrying.", search.getId());
					searchEntry(search);
				}
				else
				{
					searchRepository.save(search);
				}
			}
		}
		catch (Exception e)
		{
			logger.error("Download check failed for search id={}: {}", search.getId(), e.getMessage());
		}
	}

	private void addToBlocklist(Search search)
	{
		if (search.getData() != null && search.getData().getIdentifier() != null)
		{
			Blocklist entry = new Blocklist();
			entry.setSearch(search);
			entry.setIdentifier(search.getData().getIdentifier());
			entry.setService(search.getData().getService());
			blocklistRepository.save(entry);
			logger.info("Blocklisted '{}' for search id={}", search.getData().getIdentifier(), search.getId());
		}
	}
}
