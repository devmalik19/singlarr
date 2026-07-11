package devmalik19.singlarr.service;

import devmalik19.singlarr.constants.Constants;
import devmalik19.singlarr.constants.FileTypes;
import devmalik19.singlarr.constants.FolderType;
import devmalik19.singlarr.data.dao.Item;
import devmalik19.singlarr.data.dao.Library;
import devmalik19.singlarr.data.dao.LibraryFilter;
import devmalik19.singlarr.repository.ItemRepository;
import devmalik19.singlarr.repository.LibraryFilterRepository;
import devmalik19.singlarr.repository.LibraryRepository;
import devmalik19.singlarr.service.metadata.MetaDataService;
import jakarta.persistence.EntityNotFoundException;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
public class LibraryService
{
	private static final Logger logger = LoggerFactory.getLogger(LibraryService.class);

	private final FileSystemService fileSystemService;
	private final MetaDataService metaDataService;
	private final LibraryRepository libraryRepository;
	private final ItemRepository itemRepository;
	private final LibraryFilterRepository libraryFilterRepository;

	public LibraryService(FileSystemService fileSystemService,
						  MetaDataService metaDataService,
						  LibraryRepository libraryRepository,
						  ItemRepository itemRepository,
						  LibraryFilterRepository libraryFilterRepository)
	{
		this.fileSystemService = fileSystemService;
		this.metaDataService = metaDataService;
		this.libraryRepository = libraryRepository;
		this.itemRepository = itemRepository;
		this.libraryFilterRepository = libraryFilterRepository;
	}

	public void dbCleanUp() throws Exception
	{
		logger.info("Database cleanup started!");
		List<Library> libraryList = libraryRepository.findAll();
		List<Library> toDelete = new ArrayList<>();
		for (Library library : libraryList)
		{
			if (!Files.exists(Path.of(library.getPath())))
				toDelete.add(library);
		}
		if (!toDelete.isEmpty())
		{
			libraryRepository.deleteAllInBatch(toDelete);
			logger.info("Deleted {} missing records.", toDelete.size());
		}
		logger.info("Database cleanup complete!");
	}

	public void scan() throws Exception
	{
		Map<Path, Library> savedDirectories = new HashMap<>();
		List<Path> filesList = fileSystemService.scanRoot(Constants.LIBRARY_PATH);
		Path rootPath = Paths.get(Constants.LIBRARY_PATH).toAbsolutePath().normalize();
		filesList.sort(Comparator.comparingInt(Path::getNameCount));
		List<PathMatcher> dbFilters = dbFilters();

		logger.info("Starting root scan with list of files/directories {}", filesList);

		filesList.forEach(file -> {

			boolean isIgnored = Constants.pathMatcherList.stream().anyMatch(matcher -> matcher.matches(file));
			boolean isDbIgnored = dbFilters.stream().anyMatch(matcher -> matcher.matches(file));
			if (isIgnored || isDbIgnored)
			{
				logger.info("Skipping for file {}", file);
				return;
			}

			Path path = file.toAbsolutePath().normalize();
			Path parentPath = path.getParent();

			if (parentPath != null && rootPath.equals(parentPath))
				parentPath = null;

			if (Files.isDirectory(file))
			{
				logger.info("Scanning directory {}", path);
				int depth = rootPath.relativize(path).getNameCount();

				Library library = libraryRepository.findByPath(path.toString()).orElse(new Library());
				library.setName(path.getFileName().toString());
				library.setPath(path.toString());

				switch (depth)
				{
					case 1:
						library.setType(FolderType.ARTIST);
						break;
					case 2:
						library.setType(FolderType.ALBUM);
						break;
				}

				if (parentPath != null && savedDirectories.containsKey(parentPath))
				{
					library.setLibrary(savedDirectories.get(parentPath));
				}

				library = libraryRepository.save(library);
				if (!library.isMetadataFetched())
				{
					try
					{
						metaDataService.getMetaForLibrary(library, file);
						library.setMetadataFetched(true);
						libraryRepository.save(library);
					}
					catch (Exception e)
					{
						logger.error("Metadata fetch failed for {}: {}", library.getPath(), e.getMessage());
					}
				}
				savedDirectories.put(path, library);
			}
			else
			{
				logger.info("Scanning files {} in directory {}", path, parentPath);
				String extension = StringUtils.getFilenameExtension(path.toString());
				if (!FileTypes.isMatch(extension))
					return;

				Item item = itemRepository.findByPath(path.toString()).orElse(new Item());
				item.setName(path.getFileName().toString());
				item.setPath(path.toString());
				item.setType(extension);

				if (parentPath != null && savedDirectories.containsKey(parentPath))
				{
					item.setLibrary(savedDirectories.get(parentPath));
				}

				itemRepository.save(item);
			}
		});
		logger.info("Root scan finish");
	}

	private List<PathMatcher> dbFilters()
	{
		List<LibraryFilter> dbFilters = libraryFilterRepository.findAll();
		FileSystem fileSystem = FileSystems.getDefault();
		return dbFilters.stream()
			.map(filter -> fileSystem.getPathMatcher("glob:" + filter.getPath() + "{,/**}"))
			.toList();
	}

	public List<Library> getAll()
	{
		return libraryRepository.findAll();
	}

	public List<Library> getByType(FolderType type)
	{
		return libraryRepository.findByType(type);
	}

	public Library findById(Integer id)
	{
		return libraryRepository.findById(id).orElse(new Library());
	}

	public void refreshMetadata(Integer id)
	{
		Library library = libraryRepository.findById(id)
			.orElseThrow(() -> new EntityNotFoundException("Library not found: " + id));
		library.setMetadataFetched(false);
		libraryRepository.save(library);
		try
		{
			metaDataService.getMetaForLibrary(library, Path.of(library.getPath()));
			library.setMetadataFetched(true);
			libraryRepository.save(library);
		}
		catch (Exception e)
		{
			logger.error("Metadata refresh failed for {}: {}", library.getPath(), e.getMessage());
			throw e;
		}
	}

	@Transactional
	public int resetAllMetadataFlags()
	{
		return libraryRepository.resetAllMetadataFlags();
	}
}
