package com.smart.garage.controllers.mvc;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMVCController {

    @GetMapping()
    public String showHomepage() {
        return "index";
    }

}
