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

class QbittorrentServiceIT extends BaseIT
{
	@Autowired
	private QbittorrentService qbittorrentService;

	@Autowired
	private NetworkService networkService;

	@Test
	void checkConnection_shouldAuthenticate()
	{
		HashMap<String, ConnectionSettings> clients = networkService.getConnectionsSettingsForClients();
		ConnectionSettings settings = clients.get(NetworkService.QBITTORRENT);
		assertNotNull(settings, "qBittorrent settings should be configured");
		assertNotNull(settings.getUrl(), "qBittorrent URL should not be null");

		String response = qbittorrentService.checkConnection(settings);
		assertNotNull(response);
		assertTrue(response.contains("Ok"), "qBittorrent auth should return Ok. Got: " + response);
	}

	@Test
	void checkDownloads_withNoMatchingTorrent_shouldNotChangeStatus() throws Exception
	{
		Search search = new Search();
		DownloadState state = new DownloadState();
		state.setIdentifier("nonexistent-torrent-name-xyz");
		state.setService(NetworkService.QBITTORRENT);
		search.setData(state);
		search.setStatus(SearchStatus.DOWNLOADING);

		qbittorrentService.checkDownloads(search);

		assertEquals(SearchStatus.DOWNLOADING, search.getStatus(),
			"Status should remain DOWNLOADING when torrent not found");
	}
}
