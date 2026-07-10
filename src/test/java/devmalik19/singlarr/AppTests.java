package devmalik19.singlarr;

import devmalik19.singlarr.helper.StartupTasks;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest
class AppTests
{
	@MockitoBean
	private StartupTasks startupTasks;

	@Test
	void contextLoads()
	{
	}
}
