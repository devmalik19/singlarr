package devmalik19.singlarr.helper;

import devmalik19.singlarr.service.ProwlarrService;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class StartupTasks
{
	private final ProwlarrService prowlarrService;

	public StartupTasks(ProwlarrService prowlarrService)
	{
		this.prowlarrService = prowlarrService;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void run()
	{
		prowlarrService.sync();
	}
}
