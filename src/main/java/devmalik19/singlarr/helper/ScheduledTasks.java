package devmalik19.singlarr.helper;

import devmalik19.singlarr.constants.Keys;
import devmalik19.singlarr.constants.Settings;
import devmalik19.singlarr.service.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


@Component
public class ScheduledTasks
{
	static Logger logger = LoggerFactory.getLogger(ScheduledTasks.class);

	@Autowired
	SearchService searchService;

	@Scheduled(fixedRate = 60000, initialDelay = 120000)
	public void search()
	{
		logger.info("Starting scheduled search!");
		String schedule = Settings.store.get(Keys.SCHEDULE);
		if(schedule!=null)
			searchService.search();
		logger.info("Scheduled search ended!");
	}
}
