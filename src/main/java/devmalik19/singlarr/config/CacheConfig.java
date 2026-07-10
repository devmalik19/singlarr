package devmalik19.singlarr.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig
{
	@Bean
	public CacheManager cacheManager()
	{
		CaffeineCacheManager cacheManager = new CaffeineCacheManager(
			"ProwlarrSearchResult",
			"searchSongByTitle",
			"searchSongByTitleArtist",
			"searchSongByTitleAlbum",
			"searchSongByTitleArtistAlbum",
			"getImageUrlsForArtist",
			"getImageUrlsForAlbum"
		);
		cacheManager.setCaffeine(Caffeine.newBuilder()
			.expireAfterWrite(30, TimeUnit.MINUTES)
			.maximumSize(500)
		);
		return cacheManager;
	}
}
