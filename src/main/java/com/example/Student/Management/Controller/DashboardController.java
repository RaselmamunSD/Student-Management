package com.example.Student.Management.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import java.security.Principal;

@Controller
public class DashboardController 
{
    @GetMapping("/dashboard")
    public String dashboard(Model model, Principal principal)
    {
        if(principal==null){
            return "redirect:/login";
        }
        
        model.addAttribute("username",principal.getName());
        return "dashboard";
    }
}
