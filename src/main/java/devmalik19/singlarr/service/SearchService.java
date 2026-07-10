package devmalik19.singlarr.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import devmalik19.singlarr.constants.Keys;
import devmalik19.singlarr.constants.SearchStatus;
import devmalik19.singlarr.constants.Settings;
import devmalik19.singlarr.data.dao.Library;
import devmalik19.singlarr.data.dao.Search;
import devmalik19.singlarr.data.dto.DownloadRequest;
import devmalik19.singlarr.data.dto.MetadataResult;
import devmalik19.singlarr.data.dto.SearchResult;
import devmalik19.singlarr.helper.PaginationHelper;
import devmalik19.singlarr.repository.LibraryRepository;
import devmalik19.singlarr.repository.SearchRepository;
import devmalik19.singlarr.service.plugins.PluginsService;
import devmalik19.singlarr.service.thirdparty.NetworkService;

import java.util.*;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class SearchService
{
	static Logger logger = LoggerFactory.getLogger(SearchService.class);

    @Autowired
    private NetworkService networkService;

	@Autowired
	private PluginsService pluginsService;

	@Autowired
	private SearchRepository searchRepository;

	@Autowired
	private LibraryRepository libraryRepository;

	@Autowired
	private ObjectMapper objectMapper;

	private static List<Entry<String, Integer>> sortedServices;

	public void setPriorityOrder() throws Exception
	{
		String value = Settings.store.get(Keys.PRIORITY);
		HashMap<String, Integer> priority = objectMapper.readValue(value, new TypeReference<HashMap<String, Integer>>() {});

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

	public void triggerSearch() throws Exception
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

		boolean isSuccess;
		for (Entry<String, Integer> entry : sortedServices)
		{
			try
			{
				if(NetworkService.services.contains(entry.getKey()))
				{
					isSuccess = networkService.search(search);
				}
				else
				{
					isSuccess = pluginsService.search(search);
				}

				if(isSuccess)
					break;
			}
			catch (Exception e)
			{
				logger.debug(e.getLocalizedMessage());
			}
		}
		logger.info("Search for {} finish", search.getTitle());
	}

	public void reset()
	{
		searchRepository.update(SearchStatus.NOTFOUND, SearchStatus.NEW);
	}

	public void delete(int id)
	{
		Optional<Search> opt = searchRepository.findById(id);
		opt.ifPresent(search -> searchRepository.delete(search));
	}

	public Search getSearchById(Integer id)
	{
		Optional<Search> opt = searchRepository.findById(id);
		return opt.get();
	}

	public void checkDownloads()
	{
		List<Search> searchList = searchRepository.findByStatus(SearchStatus.DOWNLOADING);
		searchList.forEach(search -> {
			try
			{
				networkService.checkDownloads(search);
				pluginsService.checkDownloads(search);
			}
			catch (Exception e)
			{
				logger.debug(e.getLocalizedMessage());
			}
		});
	}
}
