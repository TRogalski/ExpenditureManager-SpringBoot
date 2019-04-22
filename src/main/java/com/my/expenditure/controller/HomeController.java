package com.my.expenditure.controller;

import com.my.expenditure.entity.User;
import com.my.expenditure.repository.ExpeditureRepository;
import com.my.expenditure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
public class HomeController {

    @Autowired
    private ExpeditureRepository expeditureRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping("/")
    public String getHomeView(Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        model.addAttribute("expenditures", expeditureRepository.findAllByUser(user));
        return "main/home";
    }


}
