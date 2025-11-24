package devmalik19.singlarr.service.thirdparty;

import com.fasterxml.jackson.databind.ObjectMapper;
import devmalik19.singlarr.data.dao.Setting;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import devmalik19.singlarr.repository.SettingsRepository;
import devmalik19.singlarr.service.HttpRequestService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SabnzbdService
{
    Logger logger = LoggerFactory.getLogger(SabnzbdService.class);

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public String checkConnection(ConnectionSettings connectionSettings)
    {
        return httpRequestService.doGetRequest(String.format("%s/api?mode=queue&output=json&apikey=%s",connectionSettings.getUrl(),connectionSettings.getApiKey()));
    }

    public void addNzb(String url) throws Exception
    {
        Optional<Setting> settingsOpt = settingsRepository.findById(NetworkService.SABNZBD);
        if (settingsOpt.isPresent())
        {
            Setting settings = settingsOpt.get();
            ConnectionSettings connectionSettings = objectMapper.readValue(settings.getValue(), ConnectionSettings.class);
            httpRequestService.doGetRequest(String.format("%s/api?mode=addurl&output=json&apikey=%s&cat=%s&name=%s",connectionSettings.getUrl(),connectionSettings.getApiKey(),
				connectionSettings.getCategory(), url));
        }
    }
}
