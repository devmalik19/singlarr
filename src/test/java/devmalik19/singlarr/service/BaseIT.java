package devmalik19.singlarr.service;

import devmalik19.singlarr.constants.Settings;
import devmalik19.singlarr.data.dao.Setting;
import devmalik19.singlarr.lifecycle.StartupTasks;
import devmalik19.singlarr.repository.SettingsRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

/**
 * Base integration test class that loads settings from the real database
 * into the in-memory Settings.store before tests run.
 */
@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public abstract class BaseIT
{
	@MockitoBean
	private StartupTasks startupTasks;

	@Autowired
	private SettingsRepository settingsRepository;

	@BeforeAll
	void loadSettings()
	{
		List<Setting> settingList = settingsRepository.findAll();
		settingList.forEach(setting -> Settings.store.put(setting.getKey(), setting.getValue()));
	}
}
