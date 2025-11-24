package devmalik19.singlarr.helper;

import devmalik19.singlarr.constants.Constants;
import devmalik19.singlarr.service.ProwlarrService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class StartupTasks
{
	private final ProwlarrService prowlarrService;

	public StartupTasks(ProwlarrService prowlarrService)
	{
		this.prowlarrService = prowlarrService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void run() throws Exception
	{
		prowlarrService.sync();
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
}
