package devmalik19.singlarr.service;

import devmalik19.singlarr.constants.Constants;
import devmalik19.singlarr.constants.FileTypes;
import devmalik19.singlarr.data.dao.Item;
import devmalik19.singlarr.data.dao.Library;
import devmalik19.singlarr.repository.ItemRepository;
import devmalik19.singlarr.repository.LibraryRepository;
import devmalik19.singlarr.service.metadata.MetaDataService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

	public void scan() throws Exception
	{
		Map<Path, Library> savedDirectories = new HashMap<>();
		List<Path> filesList = fileSystemService.scanRoot(Constants.LIBRARY_PATH);
		Path rootPath = Paths.get(Constants.LIBRARY_PATH).toAbsolutePath().normalize();
		filesList.sort(Comparator.comparingInt(Path::getNameCount));
		logger.info("Starting root scan with list of files/directories {}", filesList);

		filesList.forEach(file->{

			boolean isIgnored = Constants.pathMatcherList.stream().anyMatch(matcher -> matcher.matches(file));
			if (isIgnored)
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
				Library library = libraryRepository.findByPath(path.toString()).orElse(new Library());
				library.setName(path.getFileName().toString());
				library.setPath(path.toString());
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

				item = itemRepository.save(item);
				metaDataService.getMetaForItem(item);
			}
		});
		logger.info("Root scan finish");
	}

	public List<Library> getAll()
	{
		return libraryRepository.findAll();
	}

	public Library findById(String id)
	{
		return libraryRepository.findById(id).orElse(new Library());
	}
}
