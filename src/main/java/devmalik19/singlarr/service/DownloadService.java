package devmalik19.singlarr.service;

import devmalik19.singlarr.constants.Constants;
import devmalik19.singlarr.constants.FileTypes;
import devmalik19.singlarr.constants.Keys;
import devmalik19.singlarr.constants.Settings;
import devmalik19.singlarr.data.dao.Search;
import devmalik19.singlarr.data.dto.DownloadState;
import org.jaudiotagger.audio.AudioFile;
import org.jaudiotagger.audio.AudioFileIO;
import org.jaudiotagger.tag.FieldKey;
import org.jaudiotagger.tag.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Stream;

@Service
public class DownloadService
{
	private static final Logger logger = LoggerFactory.getLogger(DownloadService.class);

	/**
	 * Processes a completed download: finds the audio file in the download directory,
	 * writes metadata tags, and moves it to the target library folder.
	 */
	public void process(Search search)
	{
		try
		{
			DownloadState downloadState = search.getData();
			if (downloadState == null || downloadState.getDownloadPath() == null)
			{
				logger.warn("No download state/path for search id={}, skipping post-download.", search.getId());
				return;
			}

			Path downloadDir = resolveDownloadPath(downloadState.getDownloadPath());
			if (downloadDir == null || !Files.exists(downloadDir))
			{
				logger.warn("Download directory does not exist: {}", downloadDir);
				return;
			}

			Path audioFile = findAudioFile(downloadDir, search.getTitle());
			if (audioFile == null)
			{
				logger.warn("Could not locate audio file for '{}' in {}", search.getTitle(), downloadDir);
				return;
			}

			logger.info("Found audio file: {}", audioFile);

			// Write metadata tags if enabled
			if (isMetadataTaggingEnabled())
			{
				writeMetadataTags(audioFile, search);
			}

			// Move file to library folder
			Path targetPath = resolveTargetPath(search, audioFile);
			if (targetPath != null)
			{
				Files.createDirectories(targetPath.getParent());
				Files.move(audioFile, targetPath, StandardCopyOption.REPLACE_EXISTING);
				logger.info("Moved '{}' to '{}'", audioFile.getFileName(), targetPath);
			}
		}
		catch (Exception e)
		{
			logger.error("Post-download processing failed for search id={}: {}", search.getId(), e.getMessage(), e);
		}
	}

	/**
	 * Resolves the download path. The downloadPath from DownloadState is the category name.
	 * Files are expected under DOWNLOAD_PATH/category/ or directly in DOWNLOAD_PATH.
	 */
	private Path resolveDownloadPath(String category)
	{
		Path basePath = Paths.get(Constants.DOWNLOAD_PATH);
		if (StringUtils.hasText(category))
		{
			Path categoryPath = basePath.resolve(category);
			if (Files.exists(categoryPath))
				return categoryPath;
		}
		return basePath;
	}

	/**
	 * Searches for an audio file matching the search title in the download directory.
	 * Uses a fuzzy match (Levenshtein) similar to how the search matching works.
	 */
	private Path findAudioFile(Path downloadDir, String title) throws IOException
	{
		try (Stream<Path> files = Files.walk(downloadDir))
		{
			List<Path> audioFiles = files
				.filter(Files::isRegularFile)
				.filter(this::isAudioFile)
				.toList();

			// First try exact-ish match using filename similarity
			for (Path file : audioFiles)
			{
				String fileName = file.getFileName().toString();
				String nameWithoutExt = fileName.replaceAll("\\.\\w{3,5}$", "");
				if (devmalik19.singlarr.helper.FilesHelper.isMatch(title, nameWithoutExt))
				{
					return file;
				}
			}

			// If only one audio file exists in the directory, it's likely the right one
			if (audioFiles.size() == 1)
			{
				return audioFiles.get(0);
			}
		}
		return null;
	}

	private boolean isAudioFile(Path path)
	{
		String extension = StringUtils.getFilenameExtension(path.toString());
		return FileTypes.isMatch(extension);
	}

	/**
	 * Writes artist and album tags to the audio file using JAudioTagger.
	 */
	private void writeMetadataTags(Path audioFile, Search search)
	{
		try
		{
			AudioFile af = AudioFileIO.read(audioFile.toFile());
			Tag tag = af.getTagOrCreateDefault();

			if (StringUtils.hasText(search.getTitle()))
				tag.setField(FieldKey.TITLE, search.getTitle());

			if (StringUtils.hasText(search.getArtist()))
				tag.setField(FieldKey.ARTIST, search.getArtist());

			if (StringUtils.hasText(search.getAlbum()))
				tag.setField(FieldKey.ALBUM, search.getAlbum());

			if (StringUtils.hasText(search.getYear()))
				tag.setField(FieldKey.YEAR, search.getYear());

			af.commit();
			logger.info("Metadata tags written for '{}'", audioFile.getFileName());
		}
		catch (Exception e)
		{
			logger.error("Failed to write metadata tags for '{}': {}", audioFile.getFileName(), e.getMessage());
		}
	}

	/**
	 * Resolves the target path in the library where the file should be moved.
	 * Target: Library.path / filename
	 */
	private Path resolveTargetPath(Search search, Path audioFile)
	{
		if (search.getLibrary() == null || search.getLibrary().getPath() == null)
		{
			logger.warn("No library assigned for search id={}, cannot move file.", search.getId());
			return null;
		}

		Path libraryPath = Paths.get(search.getLibrary().getPath());
		return libraryPath.resolve(audioFile.getFileName());
	}

	private boolean isMetadataTaggingEnabled()
	{
		String value = Settings.store.get(Keys.METADATA_TAGGING);
		// Enabled by default if not explicitly set to "false"
		return value == null || !"false".equalsIgnoreCase(value);
	}
}
