package devmalik19.singlarr.controller;

import devmalik19.singlarr.constants.SettingsKeys;
import devmalik19.singlarr.service.IndexService;
import devmalik19.singlarr.service.SettingsService;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SettingsController
{
    @Autowired
    SettingsService settingsService;

    @Autowired
    IndexService indexService;

    @GetMapping("settings/indexes")
    public String indexes(Model model)
    {
        ConnectionSettings prowlarrSettings = settingsService.get(SettingsKeys.PROWLARR,
                ConnectionSettings.class);

        if(prowlarrSettings==null)
            prowlarrSettings = new ConnectionSettings();
        model.addAttribute("prowlarrSettings", prowlarrSettings);
        model.addAttribute("indexes", indexService.findAll());
        return "settings/indexes";
    }

    @GetMapping("settings/clients")
    public String clients()
    {
        return "settings/clients";
    }

	@PostMapping("/settings/save/prowlarr")
	public String save(@ModelAttribute ConnectionSettings prowlarrSettings, Model model)
	{
		settingsService.save(SettingsKeys.PROWLARR, prowlarrSettings);
		model.addAttribute("prowlarrSettings", prowlarrSettings);
		return "settings/indexes";
	}

    @PostMapping("/settings/check/prowlarr")
    @ResponseBody
    public String check(@ModelAttribute ConnectionSettings prowlarrSettings)
    {
        return settingsService.check(SettingsKeys.PROWLARR, prowlarrSettings);
    }

	@GetMapping("/settings/sync/prowlarr")
	public String sync(Model model)
	{
		settingsService.sync(SettingsKeys.PROWLARR);
		model.addAttribute("indexes", indexService.findAll());
		return   "settings/indexes :: indexes";
	}
}
