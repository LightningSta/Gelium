package org.example.frontend.view;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "main";
    }

    @GetMapping("/registration")
    public String registration() {
        return "signup";
    }
    @GetMapping("/login")
    public String login() {
        return "signin";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/profile")
    public String profile() {
        return "profile";
    }

    @GetMapping("/intofs")
    public String intofs() {
        return "fs";
    }

    @GetMapping("/test")
    public String test() {
        return "fs";
    }
}
