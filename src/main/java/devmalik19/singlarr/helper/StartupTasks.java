package devmalik19.singlarr.helper;

import devmalik19.singlarr.constants.Constants;
import devmalik19.singlarr.constants.Settings;
import devmalik19.singlarr.data.dao.Setting;
import devmalik19.singlarr.repository.SettingsRepository;
import devmalik19.singlarr.service.thirdparty.NetworkService;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class StartupTasks
{
	private final NetworkService networkService;
	private final SettingsRepository settingsRepository;

	public StartupTasks(NetworkService networkService, SettingsRepository settingsRepository)
	{
		this.networkService = networkService;
		this.settingsRepository = settingsRepository;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void run() throws Exception
	{
		networkService.sync();
	}

	@Value("${ENCRYPTION_KEY:}")
	public void setEncryptionKey(String key)
	{
		if(StringUtils.hasText(key))
		{
			if (key.length() < 32)
				key += "#".repeat(32 - key.length());
			else
				key = key.substring(0, 32);
		}
		Constants.ENCRYPTION_KEY = key;
	}

	@PostConstruct
	public void loadSettings()
	{
		List<Setting> settingList = settingsRepository.findAll();
		settingList.forEach(setting -> Settings.store.put(setting.getKey(),setting.getValue()));
	}
}
