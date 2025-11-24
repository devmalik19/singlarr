package devmalik19.singlarr.service.thirdparty;

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
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class QbittorrentService
{
    Logger logger = LoggerFactory.getLogger(QbittorrentService.class);

    @Autowired
    private HttpRequestService httpRequestService;

    @Autowired
    private SettingsRepository settingsRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private static ResponseEntity<String> AUTH_RESPONSE;

    public String checkConnection(ConnectionSettings connectionSettings)
    {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
        String body = String.format("username=%s&password=%s", connectionSettings.getUsername(), connectionSettings.getPassword());
        AUTH_RESPONSE = httpRequestService.doPostRequestRaw(String.format("%s/api/v2/auth/login", connectionSettings.getUrl()), body, headers);
        return AUTH_RESPONSE.getBody();
    }

    public void addTorrent(String url) throws Exception
    {
        Optional<Setting> settingsOpt = settingsRepository.findById(NetworkService.QBITTORRENT);
        if(settingsOpt.isPresent())
        {
            Setting settings = settingsOpt.get();
            ConnectionSettings connectionSettings = objectMapper.readValue(settings.getValue(), ConnectionSettings.class);

            if(AUTH_RESPONSE==null)
                checkConnection(connectionSettings);
            HttpHeaders responseHeaders = AUTH_RESPONSE.getHeaders();
            String cookies = responseHeaders.get("set-cookie").toString();
            String sid =  cookies.replaceAll(".*SID=(.+?);.*", "$1");

            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
            headers.put("Cookie", "SID="+sid);
            String body = String.format("urls=%s&category=%s", url, connectionSettings.getCategory());
            httpRequestService.doPostRequest(String.format("%s/api/v2/torrents/add", connectionSettings.getUrl()), body, headers);
        }
    }
}
