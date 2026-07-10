package devmalik19.singlarr.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
public class JacksonConfig
{
	@Bean
	@Primary
	public ObjectMapper objectMapper()
	{
		return new ObjectMapper();
	}
}
