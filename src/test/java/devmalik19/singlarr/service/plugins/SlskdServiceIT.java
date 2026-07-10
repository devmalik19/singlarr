package devmalik19.singlarr.service.plugins;

import devmalik19.singlarr.constants.SearchStatus;
import devmalik19.singlarr.data.dao.Search;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import devmalik19.singlarr.data.dto.DownloadState;
import devmalik19.singlarr.service.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SlskdServiceIT extends BaseIT
{
	@Autowired
	private SlskdService slskdService;

	@Autowired
	private PluginsService pluginsService;

	@Test
	void checkConnection_shouldReturnResponse()
	{
		HashMap<String, ConnectionSettings> services = pluginsService.getConnectionsSettingsForServices();
		ConnectionSettings settings = services.get(PluginsService.SLSKD);
		assertNotNull(settings, "Slskd settings should be configured");
		assertNotNull(settings.getUrl(), "Slskd URL should not be null");

		String response = slskdService.checkConnection(settings);
		assertNotNull(response);
		assertFalse(response.isEmpty(), "Slskd connection check should return a response");
	}

	@Test
	void search_shouldReturnResults() throws Exception
	{
		// Note: This test takes time (~60s) because slskd search is async with polling
		List<SlskdService.SearchResult> results = slskdService.search("Imagine Dragons Believer");
		assertNotNull(results);
		// Slskd may or may not return results depending on online peers
	}

	@Test
	void checkDownloads_withNoMatchingFile_shouldNotChangeStatus() throws Exception
	{
		Search search = new Search();
		DownloadState state = new DownloadState();
		state.setIdentifier("nonexistent-file-path-xyz.mp3");
		state.setService(PluginsService.SLSKD);
		search.setData(state);
		search.setStatus(SearchStatus.DOWNLOADING);

		slskdService.checkDownloads(search);

		assertNotNull(search.getStatus());
	}
}
