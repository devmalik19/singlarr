package devmalik19.singlarr.service;

import devmalik19.singlarr.constants.Constants;
import devmalik19.singlarr.constants.SettingsKeys;
import devmalik19.singlarr.data.dao.Index;
import devmalik19.singlarr.data.dao.Setting;
import devmalik19.singlarr.data.dto.SearchResult;
import devmalik19.singlarr.data.dto.Tag;
import devmalik19.singlarr.repository.IndexRepository;
import devmalik19.singlarr.data.dto.ConnectionSettings;

import java.util.*;

import devmalik19.singlarr.repository.SettingsRepository;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import tools.jackson.databind.ObjectMapper;

@Service
public class ProwlarrService
{
	Logger logger = LoggerFactory.getLogger(ProwlarrService.class);

    @Autowired
    HttpRequestService httpRequestService;

    @Autowired
	IndexRepository indexRepository;

	@Autowired
	SettingsRepository settingsRepository;

    ObjectMapper objectMapper = new ObjectMapper();

    public String checkConnection(ConnectionSettings prowlarrSettings)
    {
        return httpRequestService.doGetRequest(String.format("%s/ping", prowlarrSettings.getUrl()));
    }

    public void sync()
    {
		logger.info("Starting indexes sync");

		Optional<Setting> settings = settingsRepository.findById(SettingsKeys.PROWLARR);
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
					String[] tags = prowlarrSettings.getTags();
					boolean isTagFilterEmpty = (tags == null || tags.length == 0);
					Arrays.stream(indexes)
							.filter(Index::isEnable)
							.filter(index -> {
								if (isTagFilterEmpty) return true;

								if (index.getTags() == null || index.getTags().length == 0) return false;

								return Arrays.stream(tags).anyMatch(tag -> {
									return Arrays.stream(index.getTags())
											.filter(prowlarrTags::containsKey)
											.anyMatch(indexTag-> prowlarrTags.get(indexTag).contains(tag));
                                });

							})
							.forEach(index -> indexRepository.save(index));
				}
			}
		}
		logger.info("Indexes sync finish!");
    }

    public SearchResult[] search(String searchTerm, ConnectionSettings prowlarrSettings)
    {
        List<Index> indexes = indexRepository.findAll();

		Map<String, String> params = new HashMap<>();
		params.put("query", searchTerm);
		params.put("type", "search");
		params.put("categories", Constants.SEARCH_CATEGORY);

		indexes.forEach(index -> {
			params.put("indexerIds", String.valueOf(index.getId()));
		});

		Map<String, String> headers = new HashMap<>();
		headers.put("Content-Type", "application/json; charset= utf-8");
		headers.put("X-Api-Key", prowlarrSettings.getApiKey());

		String response = httpRequestService.doGetRequest(
				String.format("%s/api/v1/search/", prowlarrSettings.getUrl()), headers, params);

		logger.debug("Search response {}", response);

		return objectMapper.readValue(response, SearchResult[].class);
    }

	public Map<Integer, String> tags(ConnectionSettings prowlarrSettings)
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
			logger.info("Response from prowlarr : {}", response);
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

