package devmalik19.singlarr.controller;

import devmalik19.singlarr.service.IndexService;
import devmalik19.singlarr.service.SettingsService;
import devmalik19.singlarr.data.dto.ConnectionSettings;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

@Controller
public class SettingsController
{
    @Autowired
    SettingsService settingsService;

    @Autowired
    IndexService indexService;

	@GetMapping("settings")
	public String general(Model model) throws Exception
	{
		List<String> fixedTime = new ArrayList<>();
		LocalTime time = LocalTime.MIDNIGHT;
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");

		for (int i = 0; i < 48; i++)
		{
			fixedTime.add(time.format(formatter));
			time = time.plusMinutes(30);
		}
		model.addAttribute("fixedTime", fixedTime);

		List<String> interval = List.of("5 minutes", "10 minutes", "1 hour");
		model.addAttribute("interval", interval);

		List<String> services = List.of("qbt", "prowlarr", "slskd");
		model.addAttribute("services", services);

		return "settings/general";
	}

    @GetMapping("settings/indexes")
    public String indexes(Model model) throws Exception
    {
        model.addAttribute("settings", settingsService.getConnectionsSettingsForIndexes());
        model.addAttribute("indexes", indexService.findAll());
        return "settings/indexes";
    }

    @GetMapping("settings/clients")
    public String clients(Model model) throws Exception
    {
        model.addAttribute("settings", settingsService.getConnectionsSettingsForClients());
        return "settings/clients";
    }

	@GetMapping("settings/services")
	public String services(Model model) throws Exception
	{
		model.addAttribute("settings", settingsService.getConnectionsSettingsForServices());
		return "settings/services";
	}

	@PostMapping("/settings/save/{key}")
	public String save(
		@PathVariable String key,
		@RequestParam(value = "redirect", required = false) String redirect,
		@ModelAttribute ConnectionSettings connectionSettings, Model model)
	{
		settingsService.save(key, connectionSettings);
		if(StringUtils.hasText(redirect))
			return "redirect:/settings/"+redirect;
		else
			return "redirect:/settings";
	}

	@GetMapping("/settings/indexes/{id}")
	@ResponseBody
	public void update(@PathVariable int id, @RequestParam(value = "status") String status)
	{
		settingsService.update(id,status);
	}

    @PostMapping("/settings/indexes/check/{key}")
    @ResponseBody
    public String checkNetworkConnection(@PathVariable String key, @ModelAttribute ConnectionSettings connectionSettings)
    {
        return settingsService.checkNetworkConnection(key, connectionSettings);
    }

	@PostMapping("/settings/services/check/{key}")
	@ResponseBody
	public String checkPluginConnection(@PathVariable String key, @ModelAttribute ConnectionSettings connectionSettings)
	{
		return settingsService.checkPluginConnection(key, connectionSettings);
	}

	@GetMapping("/settings/sync")
	public String sync(Model model) throws Exception
	{
		settingsService.sync();
		model.addAttribute("indexes", indexService.findAll());
		return   "settings/indexes :: indexes";
	}
}
