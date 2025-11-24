package devmalik19.singlarr.service.plugins;

import com.fasterxml.jackson.databind.ObjectMapper;
import devmalik19.singlarr.data.dao.Setting;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import devmalik19.singlarr.repository.SettingsRepository;
import devmalik19.singlarr.service.HttpRequestService;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SlskdService
{
    Logger logger = LoggerFactory.getLogger(SlskdService.class);

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public String checkConnection(ConnectionSettings connectionSettings)
    {
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Api-Key", connectionSettings.getApiKey());
        return httpRequestService.doGetRequest(String.format("%s/api/v0/session/enabled",connectionSettings.getUrl()));
    }

    public String search(String search) throws Exception
    {
        Optional<Setting> settings = settingsRepository.findById(PluginsService.SLSKD);
        if (settings.isPresent())
        {
            ConnectionSettings connectionSettings = objectMapper.readValue(settings.get().getValue(), ConnectionSettings.class);
            if(connectionSettings != null)
            {
				Map<String, String> headers = new HashMap<>();
				headers.put("X-Api-Key", connectionSettings.getApiKey());
				headers.put("Content-Type", "application/json");
				String json = "{\"SearchText\": \""+search+"\"}";
               return httpRequestService.doPostRequest(String.format("%s/api/v0/searches", connectionSettings.getUrl()), json, headers);
            }
        }
        return search;
    }
}
