package devmalik19.singlarr.helper;

import com.fasterxml.jackson.databind.ObjectMapper;
import devmalik19.singlarr.constants.Settings;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * Centralized helper for resolving ConnectionSettings from the Settings store.
 * Eliminates repeated objectMapper.readValue(Settings.store.get(key), ConnectionSettings.class)
 * scattered across multiple services.
 */
@Component
public class SettingsHelper
{
	private static final Logger logger = LoggerFactory.getLogger(SettingsHelper.class);

	private final ObjectMapper objectMapper;

	public SettingsHelper(ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}

	/**
	 * Retrieves and deserializes ConnectionSettings for the given service key.
	 * Returns null if the key is not found or the value is empty.
	 */
	public ConnectionSettings getConnectionSettings(String key)
	{
		String value = Settings.store.get(key);
		if (!StringUtils.hasText(value))
			return null;

		try
		{
			return objectMapper.readValue(value, ConnectionSettings.class);
		}
		catch (Exception e)
		{
			logger.error("Failed to parse ConnectionSettings for key '{}': {}", key, e.getMessage());
			return null;
		}
	}

	/**
	 * Retrieves ConnectionSettings, returning a default empty instance if not found.
	 */
	public ConnectionSettings getConnectionSettingsOrDefault(String key)
	{
		ConnectionSettings settings = getConnectionSettings(key);
		return settings != null ? settings : new ConnectionSettings();
	}
}
