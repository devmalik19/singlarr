package devmalik19.singlarr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController
{
    @GetMapping("indexes")
    public String indexes()
    {
        return "indexes";
    }

	@GetMapping("downloader")
	public String downloader()
	{
		return "downloader";
	}
}
