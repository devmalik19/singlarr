package devmalik19.singlarr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class SinglarrApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(SinglarrApplication.class, args);
	}
}
