package devmalik19.singlarr.controller;

import devmalik19.singlarr.service.FileSystemService;
import devmalik19.singlarr.service.SlskdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HomeController
{
    @Autowired
    FileSystemService fileSystemService;

	@Autowired
	SlskdService slskdService;

    @GetMapping("/")
    public String home()
    {
        return "home";
    }

    @GetMapping("/test")
    @ResponseBody
    public void test() throws Exception
    {
		slskdService.search("TEST");
        //fileSystemService.getAllFilesList(Constants.DOWNLOAD_PATH);
    }
}
