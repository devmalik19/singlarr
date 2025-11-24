package devmalik19.singlarr.service.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import devmalik19.singlarr.constants.Settings;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import devmalik19.singlarr.data.dto.SearchResult;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NetworkService
{
	Logger logger = LoggerFactory.getLogger(NetworkService.class);

	public static final String PROWLARR = "prowlarr";
	public static final String QBITTORRENT = "qbt";
	public static final String SABNZBD = "sabnzbd";

	@Autowired
	private ProwlarrService prowlarrService;

	@Autowired
	private QbittorrentService qbittorrentService;

	@Autowired
	private SabnzbdService sabnzbdService;

	@Autowired
	private ObjectMapper objectMapper;


	public String check(String key, ConnectionSettings connectionSettings)
	{
		return switch (key) {
			case PROWLARR -> prowlarrService.checkConnection(connectionSettings);
			case QBITTORRENT -> qbittorrentService.checkConnection(connectionSettings);
			case SABNZBD -> sabnzbdService.checkConnection(connectionSettings);
			default -> null;
		};
	}

	public void sync() throws Exception
	{
		prowlarrService.sync();
	}

	public SearchResult[] search(String searchTerm) throws Exception
	{
		return prowlarrService.search(searchTerm);
	}

	public ConnectionSettings getConnectionsSettingsForIndexes()
	{
		String value = Settings.store.get(PROWLARR);
		try
		{
			return objectMapper.readValue(value, ConnectionSettings.class);
		}
		catch (Exception e)
		{
			logger.error(e.getLocalizedMessage());
		}
		return new ConnectionSettings();
	}

	public HashMap<String, ConnectionSettings> getConnectionsSettingsForClients()
	{
		HashMap<String, ConnectionSettings> connectionSettingsList = new HashMap<>();
		try
		{
			String value = Settings.store.get(QBITTORRENT);
			if(value!=null)
				connectionSettingsList.put(QBITTORRENT, objectMapper.readValue(value, ConnectionSettings.class));
			else
				connectionSettingsList.put(QBITTORRENT, new ConnectionSettings());

			value = Settings.store.get(SABNZBD);
			if(value!=null)
				connectionSettingsList.put(SABNZBD, objectMapper.readValue(value, ConnectionSettings.class));
			else
				connectionSettingsList.put(SABNZBD,  new ConnectionSettings());

		}
		catch (Exception e)
		{
			logger.error(e.getLocalizedMessage());
		}
		return connectionSettingsList;
	}
}
