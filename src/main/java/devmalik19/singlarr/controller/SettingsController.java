package devmalik19.singlarr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingsController
{
    @GetMapping("settings/indexes")
    public String indexes()
    {
        return "settings/indexes";
    }

    @GetMapping("settings/clients")
    public String clients()
    {
        return "settings/clients";
    }
}
