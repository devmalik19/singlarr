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

import java.nio.file.*;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class LibraryService
{
	Logger logger = LoggerFactory.getLogger(LibraryService.class);

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private MetaDataService metaDataService;

	@Autowired
	private LibraryRepository libraryRepository;

	@Autowired
	private ItemRepository itemRepository;

	@Autowired
	private LibraryFilterRepository libraryFilterRepository;

	public void scan() throws Exception
	{
		Map<Path, Library> savedDirectories = new HashMap<>();
		List<Path> filesList = fileSystemService.scanRoot(Constants.LIBRARY_PATH);
		Path rootPath = Paths.get(Constants.LIBRARY_PATH).toAbsolutePath().normalize();
		filesList.sort(Comparator.comparingInt(Path::getNameCount));
		List<PathMatcher> dbFilters = dbFilters();

		logger.info("Starting root scan with list of files/directories {}", filesList);

		filesList.forEach(file->{

			boolean isIgnored = Constants.pathMatcherList.stream().anyMatch(matcher -> matcher.matches(file));
			boolean isDbIgnored = dbFilters.stream().anyMatch(matcher -> matcher.matches(file));
			if (isIgnored || isDbIgnored)
			{
				logger.info("Skipping for file {}", file);
				return;
			}

			Path path = file.toAbsolutePath().normalize();
			Path parentPath = path.getParent();

			if(parentPath!=null && rootPath.equals(parentPath))
				parentPath=null;

			if(Files.isDirectory(file))
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
				metaDataService.getMetaForLibrary(library, file);
				savedDirectories.put(path, library);
			}
			else
			{
				logger.info("Scanning files {} in directory {}", path, parentPath);
				String extension = StringUtils.getFilenameExtension(path.toString());
				if(!FileTypes.isMatch(extension))
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
			.map(filter -> {
				return fileSystem.getPathMatcher("glob:" + filter + "{,/**}");
			})
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
}
