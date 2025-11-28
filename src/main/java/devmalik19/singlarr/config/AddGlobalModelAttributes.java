package devmalik19.singlarr.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class AddGlobalModelAttributes
{
    @ModelAttribute
    public void addGlobalModelAttributes(Model model, HttpServletRequest request)
    {
        model.addAttribute("section", request.getRequestURI());
    }
}
