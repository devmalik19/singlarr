package devmalik19.singlarr.service.metadata;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import devmalik19.singlarr.constants.Constants;
import devmalik19.singlarr.data.dto.MetadataResult;
import devmalik19.singlarr.service.HttpRequestService;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class MusicBrainzService
{
	Logger logger = LoggerFactory.getLogger(MusicBrainzService.class);

	@Autowired
	private HttpRequestService httpRequestService;

	@Autowired
	private ObjectMapper objectMapper;

	private static String MUSICBRAINZ_URL = "https://musicbrainz.org/ws/2/";
	private static String COVERTART_URL = "https://coverartarchive.org/release/" ;

	@Cacheable("searchSongByTitle")
	public List<MetadataResult> searchSongByTitle(String title, String year, int offset, int limit)
	{
		String query = String.format("recording:\"%s\"", title);
		query = appendYear(query, year);
		return getMetadataResults(query, offset, limit);
	}

	@Cacheable("searchSongByTitleArtist")
	public List<MetadataResult> searchSongByTitleArtist(String title, String artist, String year, int offset, int limit)
	{
		String query = String.format("recording:\"%s\" AND artist:\"%s\"", title, artist);
		query = appendYear(query, year);
		return getMetadataResults(query, offset, limit);
	}

	@Cacheable("searchSongByTitleAlbum")
	public List<MetadataResult> searchSongByTitleAlbum(String title, String album, String year, int offset, int limit)
	{
		String query = String.format("recording:\"%s\" AND release:\"%s\"", title, album);
		query = appendYear(query, year);
		return getMetadataResults(query, offset, limit);
	}

	@Cacheable("searchSongByTitleArtistAlbum")
	public List<MetadataResult> searchSongByTitleArtistAlbum(String title, String artist, String album,  String year, int offset, int limit)
	{
		String query = String.format("recording:\"%s\" AND artist:\"%s\" AND release:\"%s\"", title, artist, album);
		query = appendYear(query, year);
		return getMetadataResults(query, offset, limit);
	}

	private String appendYear(String query, String year)
	{
		if (StringUtils.hasText(year))
		{
			return query + " AND date:" + year.trim();
		}
		return query;
	}

	private List<MetadataResult> getMetadataResults(String query, int offset, int limit)
	{
		logger.info("Starting song search for {}", query);

		List<MetadataResult> metadataResults = new ArrayList<>();

		Map<String, String> headers = new HashMap<>();
		headers.put("User-Agent", Constants.USER_AGENT);

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MUSICBRAINZ_URL+"recording");
		uriBuilder.queryParam("query",  query);
		uriBuilder.queryParam("fmt", "json");
		uriBuilder.queryParam("offset", offset);
		uriBuilder.queryParam("limit", limit);

		URI uri = uriBuilder.build().toUri();
		String response = httpRequestService.doGetRequest(headers, uri);
		logger.debug("Response from musicBrainz {}", response);

		if (!StringUtils.hasText(response))
			return metadataResults;

		try
		{
			JsonNode jsonNode = objectMapper.readTree(response);
			if(jsonNode.path("count").asInt()>0)
			{
				JsonNode recordingsNode = jsonNode.path("recordings");
				MusicBrainzResults[] results = objectMapper.treeToValue(recordingsNode, MusicBrainzResults[].class);
				for(MusicBrainzResults result:results)
				{
					MetadataResult metadataResult = new MetadataResult();
					metadataResult.setTitle(result.title());
					metadataResult.setYear(result.getYear());
					metadataResult.setAlbums(result.getReleaseTitles());
					metadataResult.setArtists(result.getArtistNames());
					metadataResults.add(metadataResult);
				}
			}
		}
		catch (Exception e)
		{
			logger.error(e.getLocalizedMessage());
		}
		return metadataResults;
	}


	@JsonIgnoreProperties(ignoreUnknown = true)
	public record MusicBrainzResults(
		String id,
		String title,
		@JsonProperty("first-release-date") String year,
		@JsonProperty("artist-credit") List<ArtistCredit> artistCredits,
		List<Release> releases
	) {
		@JsonIgnoreProperties(ignoreUnknown = true)
		public record ArtistCredit(String name, Artist artist) {}

		@JsonIgnoreProperties(ignoreUnknown = true)
		public record Artist(String id, String name) {}

		@JsonIgnoreProperties(ignoreUnknown = true)
		public record Release(String id, String title) {}

		public String getArtistNames()
		{
			return artistCredits == null ? "" : artistCredits.stream().map(ArtistCredit::name).collect(Collectors.joining(", "));
		}

		public String getReleaseTitles()
		{
			return releases == null ? "" : releases.stream().map(Release::title).collect(Collectors.joining(", "));
		}

		public String getYear()
		{
			if (year != null && year.length() >= 4)
			{
				return year.substring(0, 4);
			}
			return "";
		}
	}

	@Cacheable("getImageUrlsForArtist")
	public String getImageUrlsForArtist(String mbid)
	{
		logger.info("Starting Image lookup for Artist MBID {}", mbid);

		Map<String, String> headers = new HashMap<>();
		headers.put("User-Agent", Constants.USER_AGENT);

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(MUSICBRAINZ_URL + "artist/" + mbid);
		uriBuilder.queryParam("fmt", "json");
		uriBuilder.queryParam("inc", "url-rels");

		URI uri = uriBuilder.build().encode().toUri();
		String response = httpRequestService.doGetRequest(headers, uri);
		logger.debug("Response from musicBrainz {}", response);

		if (!StringUtils.hasText(response))
			return "";

		return response;
	}

	@Cacheable("getImageUrlsForAlbum")
	public String getImageUrlsForAlbum(String mbid)
	{
		logger.info("Starting Image lookup for Album using MBID {}", mbid);

		Map<String, String> headers = new HashMap<>();
		headers.put("User-Agent", Constants.USER_AGENT);

		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(COVERTART_URL + mbid);
		URI uri = uriBuilder.build().encode().toUri();
		String response = httpRequestService.doGetRequest(headers, uri);
		logger.debug("Response from musicBrainz {}", response);

		if (!StringUtils.hasText(response))
			return "";

		return response;
	}
}
