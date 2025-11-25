package devmalik19.singlarr.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SettingController
{
    @GetMapping("settings")
    public String settings()
    {
        return "settings";
    }
}
