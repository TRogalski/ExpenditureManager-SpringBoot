package com.my.expenditure.controller;

import com.my.expenditure.model.User;
import com.my.expenditure.repository.UserRepository;
import com.my.expenditure.service.expenditure.ExpenditureStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.sql.Date;
import java.time.LocalDate;

@Controller
public class HomeController {

    @Autowired
    private ExpenditureStatsService expenditureStatsService;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String getHomeView(Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        model.addAttribute("dashboardData", expenditureStatsService.getMainDashboardData(user, Date.valueOf(LocalDate.now())));
        return "main/home";
    }
}
