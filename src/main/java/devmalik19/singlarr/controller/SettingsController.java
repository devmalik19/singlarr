package devmalik19.singlarr.controller;

import devmalik19.singlarr.constants.Keys;
import devmalik19.singlarr.service.IndexService;
import devmalik19.singlarr.service.SettingsService;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
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
    public String indexes(Model model) throws Exception
    {
        ConnectionSettings connectionSettings = settingsService.get(Keys.PROWLARR, ConnectionSettings.class);
        model.addAttribute("connectionSettings", connectionSettings);
        model.addAttribute("indexes", indexService.findAll());
        return "settings/indexes";
    }

    @GetMapping("settings/clients")
    public String clients(Model model) throws Exception
    {
        ConnectionSettings qbt = settingsService.get(Keys.QBITTORRENT, ConnectionSettings.class);
        ConnectionSettings sabnzbd = settingsService.get(Keys.SABNZBD, ConnectionSettings.class);

        model.addAttribute("qbt", qbt);
        model.addAttribute("sabnzbd", sabnzbd);
        return "settings/clients";
    }

	@GetMapping("settings/services")
	public String services(Model model) throws Exception
	{
		ConnectionSettings slskd = settingsService.get(Keys.SLSKD, ConnectionSettings.class);
		model.addAttribute("slskd", slskd);
		return "settings/services";
	}

	@PostMapping("/settings/save/{key}")
	public String save(@PathVariable String key, @ModelAttribute ConnectionSettings connectionSettings, Model model)
	{
		settingsService.save(key, connectionSettings);
        if(key.equals(Keys.PROWLARR))
		    return "redirect:/settings/indexes";
        else
            return "redirect:/settings/clients";
	}

    @PostMapping("/settings/check/{key}")
    @ResponseBody
    public String check(@PathVariable String key, @ModelAttribute ConnectionSettings connectionSettings)
    {
        return settingsService.check(key, connectionSettings);
    }

	@GetMapping("/settings/sync/{key}")
	public String sync(@PathVariable String key, Model model) throws Exception
	{
		settingsService.sync(key);
		model.addAttribute("indexes", indexService.findAll());
		return   "settings/indexes :: indexes";
	}
}
