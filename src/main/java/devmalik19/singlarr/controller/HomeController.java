package devmalik19.singlarr.controller;

import devmalik19.singlarr.service.FileSystemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController
{
    @Autowired
    FileSystemService fileSystemService;

    @GetMapping("/")
    public String home()
    {
        return "home";
    }
}
