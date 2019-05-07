package com.my.expenditure.controller;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.Tag;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.ExpenditureRepository;
import com.my.expenditure.repository.TagRepository;
import com.my.expenditure.repository.UserRepository;
import com.my.expenditure.service.expenditure.ExpenditureStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/expenditure")
public class ExpenditureController {

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenditureStatsService expenditureStatsService;

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
        expenditureRepository.save(expenditure);
        return "redirect:menu";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String getEditxpenditureView(@PathVariable Long id, Model model) {
        Expenditure expenditure = expenditureRepository.getOne(id);
        model.addAttribute("expenditure", expenditure);
        return "expenditure/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editExpenditure(@ModelAttribute Expenditure expenditure, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        expenditure.setUser(user);
        expenditure.setModified(String.valueOf(LocalDate.now()));
        expenditureRepository.save(expenditure);
        return "redirect:/";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    private String getListView(Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        model.addAttribute("expenditures", expenditureRepository.findAllByUser(user));
        return "expenditure/list";
    }

    @RequestMapping(value = "/list/{date}/{id}", method = RequestMethod.GET)
    private String getListView(@PathVariable String date, @PathVariable Long id, Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        Tag tag = tagRepository.getOne(id);
        model.addAttribute("expenditures", expenditureRepository.findAllByTagAndDateAndUser(tag,date,user));
        return "expenditure/list";
    }


    @RequestMapping(value = "/date/{date}", method = RequestMethod.GET)
    @ResponseBody
    private List<Expenditure> getJson(@PathVariable String date, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return expenditureRepository.findAllByUserAndDate(user, date);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    private String deleteExpenditure(@RequestParam("id") Long id) {
        Expenditure expenditure = expenditureRepository.getOne(id);
        expenditureRepository.delete(expenditure);
        return "redirect:/";
    }

    @RequestMapping(value = "/stats/{date}")
    @ResponseBody
    private String getMonthTotal(@PathVariable String date, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return expenditureStatsService.getStats(user, date).toString();
    }

    @RequestMapping(value = "/menu")
    private String getExpenditureHomeView(Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        String date = String.valueOf(LocalDate.now());
        model.addAttribute("expenditures", expenditureRepository.findAllByUserAndDate(user, date));
        return "expenditure/menu";
    }

    @ModelAttribute("tags")
    public List<Tag> tags(Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return tagRepository.findAllByUser(user);
    }
}
