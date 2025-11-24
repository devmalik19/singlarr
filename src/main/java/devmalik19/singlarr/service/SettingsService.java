package devmalik19.singlarr.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import devmalik19.singlarr.constants.Keys;
import devmalik19.singlarr.data.dao.Setting;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import devmalik19.singlarr.repository.SettingsRepository;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class
SettingsService
{
	Logger logger = LoggerFactory.getLogger(SettingsService.class);

    @Autowired
    SettingsRepository settingsRepository;

	@Autowired
	ProwlarrService prowlarrService;

    @Autowired
    QbittorrentService qbittorrentService;

    @Autowired
    SabnzbdService sabnzbdService;

    @Autowired
    SlskdService slskdService;

    ObjectMapper objectMapper = new ObjectMapper();

    public <T> void save(String key, T value)
    {
        Setting setting = new Setting();
        setting.setKey(key);

        try
        {
            switch (key)
            {
                case Keys.PROWLARR:
                    setting.setValue(objectMapper.writeValueAsString(value));
                    break;
                case Keys.QBITTORRENT:
                    setting.setValue(objectMapper.writeValueAsString(value));
                    break;
                case Keys.SABNZBD:
                    setting.setValue(objectMapper.writeValueAsString(value));
                    break;
                case Keys.SLSKD:
                    setting.setValue(objectMapper.writeValueAsString(value));
                    break;
            }
        }
        catch (Exception e)
        {
            logger.error(e.getLocalizedMessage());
        }
        settingsRepository.save(setting);
    }

    public <T> T get(String key, Class<T> type) throws Exception
    {
        Optional<Setting> settings = settingsRepository.findById(key);
        if(settings.isPresent())
        {
            try
            {
                switch(key)
                {
                    case Keys.PROWLARR:
                        return objectMapper.readValue(settings.get().getValue(), type);
                    case Keys.QBITTORRENT:
                        return objectMapper.readValue(settings.get().getValue(), type);
                    case Keys.SABNZBD:
                        return objectMapper.readValue(settings.get().getValue(), type);
                    case Keys.SLSKD:
                        return objectMapper.readValue(settings.get().getValue(), type);
                }
            }
            catch (Exception e)
            {
                logger.error(e.getLocalizedMessage());
            }
        }
        return type.getConstructor().newInstance();
    }

	public String check(String key, ConnectionSettings connectionSettings)
	{
        return switch (key) {
            case Keys.PROWLARR -> prowlarrService.checkConnection(connectionSettings);
            case Keys.QBITTORRENT -> qbittorrentService.checkConnection(connectionSettings);
            case Keys.SABNZBD -> sabnzbdService.checkConnection(connectionSettings);
            case Keys.SLSKD -> slskdService.checkConnection(connectionSettings);
            default -> null;
        };
    }

	public void sync(String key) throws Exception
	{
		switch(key)
		{
			case Keys.PROWLARR:
				prowlarrService.sync();
		}
	}
}
