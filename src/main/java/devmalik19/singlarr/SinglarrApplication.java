package devmalik19.singlarr;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SinglarrApplication
{
	public static void main(String[] args)
	{
		SpringApplication.run(SinglarrApplication.class, args);
	}
}


/* TODO
	alpha release :
	1. how to easily switch DB with env variables
	2. how to integrate angular
	3. how to run project as docker
	4. publish to docker
 */