package devmalik19.singlarr.service.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import devmalik19.singlarr.constants.Settings;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import java.util.HashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PluginsService
{
	Logger logger = LoggerFactory.getLogger(PluginsService.class);
	public static final String SLSKD = "slskd";

	@Autowired
	private SlskdService slskdService;

	@Autowired
	private ObjectMapper objectMapper;

	public String check(String key, ConnectionSettings connectionSettings)
	{
		return switch (key) {
			case SLSKD -> slskdService.checkConnection(connectionSettings);
			default -> null;
		};
	}

	public void search(String query) throws Exception
	{
		slskdService.search(query);
	}

	public HashMap<String, ConnectionSettings> getConnectionsSettingsForServices()
	{
		HashMap<String, ConnectionSettings> connectionSettingsList = new HashMap<>();
		try
		{
			String value = Settings.store.get(SLSKD);
			if(value!=null)
				connectionSettingsList.put(SLSKD, objectMapper.readValue(value, ConnectionSettings.class));
			else
				connectionSettingsList.put(SLSKD, new ConnectionSettings());
		}
		catch (Exception e)
		{
			logger.error(e.getLocalizedMessage());
		}
		return connectionSettingsList;
	}
}
