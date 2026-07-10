package devmalik19.singlarr.service.metadata;

import devmalik19.singlarr.data.dao.Library;
import devmalik19.singlarr.data.dto.MetadataResult;
import devmalik19.singlarr.helper.PaginationHelper;
import devmalik19.singlarr.repository.LibraryRepository;
import devmalik19.singlarr.service.FileSystemService;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MetaDataService
{
	@Autowired
	private MusicBrainzService musicBrainzService;

	@Autowired
	private FileSystemService fileSystemService;

	@Autowired
	private LibraryRepository libraryRepository;

	public void getMetaForLibrary(Library library, Path file)
	{
		Path imagePath = fileSystemService.findLibraryImage(file);
		if (imagePath!=null && Files.exists(imagePath))
		{
			String name = String.valueOf(library.getId());
			String extension = StringUtils.getFilenameExtension(imagePath.toString());
			String imageFileName = name+"."+extension;
			fileSystemService.copyImageToCache(imagePath, "library", imageFileName);
			library.setImage(imageFileName);
		}
		libraryRepository.save(library);
    }

	public Page<MetadataResult> search(String title, String artist, String album, String year, Pageable pageable)
	{
		List<MetadataResult> metadataResult;
		boolean hasArtist = StringUtils.hasText(artist);
		boolean hasAlbum = StringUtils.hasText(album);

		if (hasArtist && hasAlbum)
			metadataResult = musicBrainzService.searchSongByTitleArtistAlbum(title, artist, album, year, 0, 100);
		else if (hasArtist)
			metadataResult = musicBrainzService.searchSongByTitleArtist(title, artist, year, 0, 100);
		else if (hasAlbum)
			metadataResult = musicBrainzService.searchSongByTitleAlbum(title, album, year, 0, 100);
		else
			metadataResult = musicBrainzService.searchSongByTitle(title, year, 0, 100);

		return PaginationHelper.prepareResults(metadataResult, pageable);
	}
}
