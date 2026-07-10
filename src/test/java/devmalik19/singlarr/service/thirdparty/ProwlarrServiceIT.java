package devmalik19.singlarr.service.thirdparty;

import devmalik19.singlarr.data.dto.ConnectionSettings;
import devmalik19.singlarr.data.dto.SearchResult;
import devmalik19.singlarr.service.BaseIT;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class ProwlarrServiceIT extends BaseIT
{
	@Autowired
	private ProwlarrService prowlarrService;

	@Autowired
	private NetworkService networkService;

	@Test
	void checkConnection_shouldReturnResponse()
	{
		ConnectionSettings settings = networkService.getConnectionsSettingsForIndexes();
		assertNotNull(settings.getUrl(), "Prowlarr URL should be configured");

		String response = prowlarrService.checkConnection(settings);
		assertNotNull(response);
		assertFalse(response.isEmpty(), "Prowlarr ping should return a response");
	}

	@Test
	void search_shouldReturnResults() throws Exception
	{
		SearchResult[] results = prowlarrService.search("Imagine Dragons Believer");
		assertNotNull(results);
		assertTrue(results.length > 0, "Search should return at least one result");

		SearchResult first = results[0];
		assertNotNull(first.getTitle());
		assertNotNull(first.getProtocol());
	}

	@Test
	void search_withNoResults_shouldReturnEmptyArray() throws Exception
	{
		SearchResult[] results = prowlarrService.search("xyznonexistentsong12345abc");
		assertNotNull(results);
	}

	@Test
	void sync_shouldNotThrow()
	{
		assertDoesNotThrow(() -> prowlarrService.sync());
	}
}
