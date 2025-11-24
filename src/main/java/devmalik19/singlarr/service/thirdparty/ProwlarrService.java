package devmalik19.singlarr.service.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import devmalik19.singlarr.constants.Constants;
import devmalik19.singlarr.data.dao.Index;
import devmalik19.singlarr.data.dao.Setting;
import devmalik19.singlarr.data.dto.SearchResult;
import devmalik19.singlarr.data.dto.Tag;
import devmalik19.singlarr.repository.IndexRepository;
import devmalik19.singlarr.data.dto.ConnectionSettings;

import devmalik19.singlarr.service.HttpRequestService;
import java.net.URI;
import java.util.*;

import devmalik19.singlarr.repository.SettingsRepository;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class ProwlarrService
{
	Logger logger = LoggerFactory.getLogger(ProwlarrService.class);

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
	private IndexRepository indexRepository;

	@Autowired
	private SettingsRepository settingsRepository;

	@Autowired
	private ObjectMapper objectMapper;

    public String checkConnection(ConnectionSettings connectionSettings)
    {
        return httpRequestService.doGetRequest(String.format("%s/ping", connectionSettings.getUrl()));
    }

    public void sync() throws Exception
	{
		logger.info("Starting indexes sync");

		Optional<Setting> settings = settingsRepository.findById(NetworkService.PROWLARR);
		if(settings.isPresent())
		{
			ConnectionSettings prowlarrSettings = objectMapper.readValue(settings.get().getValue(), ConnectionSettings.class);
			if(prowlarrSettings!=null)
			{
				String url = String.format("%s/api/v1/indexer", prowlarrSettings.getUrl());

				Map<String, String> headers = new HashMap<>();
				headers.put("Content-Type", "application/json; charset= utf-8");
				headers.put("X-Api-Key", prowlarrSettings.getApiKey());

				String response = httpRequestService.doGetRequest(url, headers);
				if(StringUtils.hasText(response))
				{
					logger.debug("Response from prowlarr : {}", response);

					indexRepository.deleteAll();

					Map<Integer, String> prowlarrTags = tags(prowlarrSettings);
					Index[] indexes = objectMapper.readValue(response, Index[].class);
					String category = prowlarrSettings.getCategory();
					Arrays.stream(indexes)
							.filter(Index::isEnable)
							.filter(index -> {
								if (!StringUtils.hasText(category)) return true;

								if (index.getTags() == null || index.getTags().length == 0) return false;

								return Arrays.stream(index.getTags())
											.filter(prowlarrTags::containsKey)
											.anyMatch(indexTag-> prowlarrTags.get(indexTag).contains(category));

							})
							.forEach(index -> indexRepository.save(index));
				}
			}
		}
		logger.info("Indexes sync finish!");
    }

	@Cacheable("ProwlarrSearchResult")
    public SearchResult[] search(String searchTerm) throws Exception
	{
		Optional<Setting> settings = settingsRepository.findById(NetworkService.PROWLARR);
		if(settings.isPresent())
		{
			ConnectionSettings prowlarrSettings = objectMapper.readValue(settings.get().getValue(), ConnectionSettings.class);

			List<Index> indexes = indexRepository.findAll();

			List<String> indexerIds = indexes.stream()
					.filter(Index::isEnable)
					.map(index -> String.valueOf(index.getId()))
					.toList();

			String url = String.format("%s/api/v1/search/", prowlarrSettings.getUrl());
			UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromUriString(url);
			uriBuilder.queryParam("query", searchTerm);
			uriBuilder.queryParam("type", "search");
			uriBuilder.queryParam("categories", Constants.SEARCH_CATEGORY);
			uriBuilder.queryParam("indexerIds", indexerIds);
			uriBuilder.queryParam("limit", Constants.QUERY_LIMIT);
			uriBuilder.queryParam("offset", 0);

			URI uri = uriBuilder.build().toUri();

			Map<String, String> headers = new HashMap<>();
			headers.put("Content-Type", "application/json; charset= utf-8");
			headers.put("X-Api-Key", prowlarrSettings.getApiKey());

			String response = httpRequestService.doGetRequest(
					url, headers, uri);

			logger.debug("Search response {}", response);

			return objectMapper.readValue(response, SearchResult[].class);
		}

		return new SearchResult[]{};
    }

	public Map<Integer, String> tags(ConnectionSettings prowlarrSettings) throws Exception
	{
		logger.info("Fetching Tags");

		Map<Integer, String> tags = new HashMap<>();

		String url = String.format("%s/api/v1/tag", prowlarrSettings.getUrl());

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; charset= utf-8");
		headers.put("X-Api-Key", prowlarrSettings.getApiKey());

		String response = httpRequestService.doGetRequest(url, headers);
		if(StringUtils.hasText(response))
		{
			logger.debug("Response from prowlarr : {}", response);
			Tag[] items = objectMapper.readValue(response, Tag[].class);
			tags = Arrays.stream(items)
					.collect(Collectors.toMap(
							Tag::getId,
							Tag::getLabel
					));
		}
		logger.info("Tags fetch!");
		return tags;
	}
}

