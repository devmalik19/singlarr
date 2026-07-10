package devmalik19.singlarr.service.thirdparty;

import devmalik19.singlarr.constants.SearchStatus;
import devmalik19.singlarr.data.dao.Search;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import devmalik19.singlarr.data.dto.DownloadState;
import devmalik19.singlarr.service.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class SabnzbdServiceIT extends BaseIT
{
	@Autowired
	private SabnzbdService sabnzbdService;

	@Autowired
	private NetworkService networkService;

	@Test
	void checkConnection_shouldReturnQueueResponse()
	{
		HashMap<String, ConnectionSettings> clients = networkService.getConnectionsSettingsForClients();
		ConnectionSettings settings = clients.get(NetworkService.SABNZBD);
		assertNotNull(settings, "SABnzbd settings should be configured");
		assertNotNull(settings.getUrl(), "SABnzbd URL should not be null");

		String response = sabnzbdService.checkConnection(settings);
		assertNotNull(response);
		assertFalse(response.isEmpty(), "SABnzbd queue response should not be empty");
		assertTrue(response.contains("queue"), "Response should contain queue data. Got: " + response);
	}

	@Test
	void checkDownloads_withNoMatchingNzb_shouldNotChangeStatus() throws Exception
	{
		Search search = new Search();
		DownloadState state = new DownloadState();
		state.setIdentifier("nonexistent-nzb-name-xyz.nzb");
		state.setService(NetworkService.SABNZBD);
		search.setData(state);
		search.setStatus(SearchStatus.DOWNLOADING);

		sabnzbdService.checkDownloads(search);

		assertEquals(SearchStatus.DOWNLOADING, search.getStatus(),
			"Status should remain DOWNLOADING when nzb not found in history");
	}
}
