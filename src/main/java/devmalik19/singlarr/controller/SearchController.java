package devmalik19.singlarr.controller;

import devmalik19.singlarr.constants.Constants;
import devmalik19.singlarr.constants.SettingsKeys;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import devmalik19.singlarr.data.dto.SearchResult;
import devmalik19.singlarr.service.FileSystemService;
import devmalik19.singlarr.service.SettingsService;
import devmalik19.singlarr.service.ProwlarrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SearchController
{
    @Autowired
    FileSystemService fileSystemService;

    @Autowired
    SettingsService settingsService;

    @Autowired
    ProwlarrService prowlarrService;

    @GetMapping("/search")
    public String home()
    {
        return "search";
    }

    @GetMapping("/search/result")
    @ResponseBody
    public SearchResult[] result(@RequestParam(value = "search") String search)
    {
        ConnectionSettings prowlarrSettings = settingsService.get(SettingsKeys.PROWLARR, ConnectionSettings.class);
        return prowlarrService.search(search, prowlarrSettings);
    }

    @GetMapping("/scan")
    @ResponseBody
    public void scan()
    {
        fileSystemService.getAllFilesList(Constants.DOWNLOAD_PATH);
    }
}
