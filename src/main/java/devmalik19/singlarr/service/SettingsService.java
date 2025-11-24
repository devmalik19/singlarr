package devmalik19.singlarr.service;

import devmalik19.singlarr.constants.SettingsKeys;
import devmalik19.singlarr.data.dao.Setting;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import devmalik19.singlarr.repository.SettingsRepository;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class SettingsService
{
	Logger logger = LoggerFactory.getLogger(SettingsService.class);

    @Autowired
    SettingsRepository settingsRepository;

	@Autowired
	ProwlarrService prowlarrService;

    ObjectMapper objectMapper = new ObjectMapper();

    public <T> void save(String key, T value)
    {
        Setting setting = new Setting();
        setting.setKey(key);

        switch (key)
        {
            case SettingsKeys.PROWLARR:
                setting.setValue(objectMapper.writeValueAsString(value));
            break;
        }

        settingsRepository.save(setting);
    }

    public <T> T get(String key, Class<T> type)
    {
        Optional<Setting> settings = settingsRepository.findById(key);
        if(settings.isPresent())
        {
            switch(key)
            {
                case SettingsKeys.PROWLARR:
                    return objectMapper.readValue(settings.get().getValue(), type);
            }
        }
        return null;
    }

	public String check(String key, ConnectionSettings connectionSettings)
	{
		switch(key)
		{
			case SettingsKeys.PROWLARR:
				return prowlarrService.checkConnection(connectionSettings);
		}

		return null;
	}

	public void sync(String key)
	{
		switch(key)
		{
			case SettingsKeys.PROWLARR:
				prowlarrService.sync();
		}
	}
}
