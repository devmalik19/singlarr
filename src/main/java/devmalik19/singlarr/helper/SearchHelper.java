package devmalik19.singlarr.helper;

import devmalik19.singlarr.constants.SearchStatus;
import devmalik19.singlarr.data.dao.Search;
import devmalik19.singlarr.data.dto.DownloadState;
import devmalik19.singlarr.repository.SearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulates the progressive search pattern used by both NetworkService and PluginsService.
 * Tries multiple query variations (artist+title, album+title, artist+album+title)
 * and updates the search status accordingly.
 */
public class SearchHelper
{
	private static final Logger logger = LoggerFactory.getLogger(SearchHelper.class);

	@FunctionalInterface
	public interface QueryExecutor
	{
		DownloadState execute(String query) throws Exception;
	}

	/**
	 * Executes the progressive search pattern:
	 * 1. artist + title
	 * 2. album + title (if first fails)
	 * 3. artist + album + title (if second fails)
	 *
	 * Updates the search entity status and persists the result.
	 */
	public static boolean progressiveSearch(Search search, SearchRepository searchRepository, QueryExecutor executor) throws Exception
	{
		DownloadState downloadState = executor.execute(search.getArtist() + " " + search.getTitle());

		if (downloadState.isEmpty())
			downloadState = executor.execute(search.getAlbum() + " " + search.getTitle());

		if (downloadState.isEmpty())
			downloadState = executor.execute(search.getArtist() + " " + search.getAlbum() + " " + search.getTitle());

		boolean isSuccess = !downloadState.isEmpty();

		if (isSuccess)
			search.setData(downloadState);
		search.setStatus(isSuccess ? SearchStatus.DOWNLOADING : SearchStatus.NOTFOUND);
		searchRepository.save(search);

		return isSuccess;
	}
}
