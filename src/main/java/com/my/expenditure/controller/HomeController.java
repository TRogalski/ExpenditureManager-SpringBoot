package com.my.expenditure.controller;

import com.my.expenditure.entity.User;
import com.my.expenditure.repository.ExpenditureRepository;
import com.my.expenditure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.security.Principal;
import java.time.LocalDate;

@Controller
public class HomeController {

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String getHomeView(Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        String date = String.valueOf(LocalDate.now());
        model.addAttribute("expenditures", expenditureRepository.findAllByUserAndDate(user,date));
        return "main/home";
    }

}
