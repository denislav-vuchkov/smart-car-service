package com.smart.garage.controllers.mvc;

import com.smart.garage.services.contracts.StatisticsService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class HomeMVCController {

    private final StatisticsService statisticsService;

    public HomeMVCController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping()
    public String showHomepage() {
        return "index";
    }

    @GetMapping("about-us")
    public String showAboutUs() {
        return "about-us";
    }


    @ModelAttribute("visitsCount")
    public long getNumberOfVisits() {
        return statisticsService.getNumberOfVisits();
    }

    @ModelAttribute("vehiclesCount")
    public long getNumberOfVehicles() {
        return statisticsService.getNumberOfVehicles();
    }

    @ModelAttribute("customersCount")
    public long getNumberOfCustomers() {
        return statisticsService.getNumberOfCustomers();
    }

    @ModelAttribute("servicesCount")
    public long getNumberOfServices() {
        return statisticsService.getNumberOfServices();
    }
}
