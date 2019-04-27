package com.my.expenditure.controller;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.Tag;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.ExpeditureRepository;
import com.my.expenditure.repository.TagRepository;
import com.my.expenditure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/expenditure")
public class ExpenditureController {

    @Autowired
    private ExpeditureRepository expeditureRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/add/{date}", method = RequestMethod.GET)
    public String getAddExpenditureView(@PathVariable String date, Model model) {
        Expenditure expenditure = new Expenditure();
        expenditure.setDate(date);
        model.addAttribute("expenditure", expenditure);
        return "expenditure/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addExpenditure(@ModelAttribute Expenditure expenditure, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        expenditure.setUser(user);
        expenditure.setCreated(String.valueOf(LocalDate.now()));
        expeditureRepository.save(expenditure);
        return "redirect:/";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    private String getListView(Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        model.addAttribute("expenditures", expeditureRepository.findAllByUser(user));
        return "expenditure/list";
    }


    @RequestMapping(value = "/date/{date}", method = RequestMethod.GET)
    @ResponseBody
    private List<Expenditure> getJson(@PathVariable String date, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return expeditureRepository.findAllByUserAndDate(user, date);
    }

    @RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
    private String removeExpenditure(@PathVariable Long id) {
        Expenditure expenditure = expeditureRepository.getOne(id);
        expeditureRepository.delete(expenditure);

        return "redirect:/";
    }

    @ModelAttribute("tags")
    public List<Tag> tags(Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return tagRepository.findAllByUser(user);
    }
}
