package devmalik19.singlarr.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer
{
	@Override
	public void addViewControllers(ViewControllerRegistry registry)
	{
		registry.addViewController("/").setViewName("home");
		registry.addViewController("/error").setViewName("error/error");
		registry.addViewController("indexes").setViewName("settings/indexes");
		registry.addViewController("clients").setViewName("settings/clients");
	}

}
