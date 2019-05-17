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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.sql.Date;
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
        expenditure.setDate(Date.valueOf(date));
        model.addAttribute("expenditure", expenditure);
        return "expenditure/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addExpenditure(@ModelAttribute @Valid Expenditure expenditure,
                                 BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "expenditure/add";
        }
        User user = userRepository.findFirstByEmail(principal.getName());
        expenditure.setUser(user);
        expenditure.setCreated(Date.valueOf(LocalDate.now()));
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
    public String editExpenditure(@ModelAttribute @Valid Expenditure expenditure,
                                  BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "expenditure/edit";
        }
        User user = userRepository.findFirstByEmail(principal.getName());
        expenditure.setUser(user);
        expenditure.setModified(Date.valueOf(LocalDate.now()));
        expenditureRepository.save(expenditure);
        return "redirect:menu";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    private String getListView(Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        model.addAttribute("expenditures", expenditureRepository.findAllByUser(user));
        return "expenditure/list";
    }

    @RequestMapping(value = "/list/{date}/{id}", method = RequestMethod.GET)
    private String getListView(@PathVariable Date date, @PathVariable Long id, Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());

        List<Expenditure> expenditures;

        if (id == -1) {
            expenditures = expenditureRepository.findAllUnassignedByDateAndUser(date, user);
        } else {
            Tag tag = tagRepository.getOne(id);
            expenditures = expenditureRepository.findAllByTagAndDateAndUser(tag, date, user);
        }
        model.addAttribute("expenditures", expenditures);
        return "expenditure/list";
    }

    @RequestMapping(value = "/date/{date}", method = RequestMethod.GET)
    @ResponseBody
    private List<Expenditure> getJson(@PathVariable Date date, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return expenditureRepository.findAllByUserAndDate(user, date);
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    private String deleteExpenditure(@RequestParam("id") Long id,
                                     @RequestParam("redirectPage") String redirectPage) {
        Expenditure expenditure = expenditureRepository.getOne(id);
        expenditureRepository.delete(expenditure);
        return "redirect:" + redirectPage;
    }

    @RequestMapping(value = "/stats/{date}", method = RequestMethod.GET)
    @ResponseBody
    private String getMonthTotal(@PathVariable Date date, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return expenditureStatsService.getStats(user, date).toString();
    }

    @RequestMapping(value = "/menu", method = RequestMethod.GET)
    private String getExpenditureHomeView(Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        Date date = Date.valueOf(LocalDate.now());
        model.addAttribute("expenditures", expenditureRepository.findAllByUserAndDate(user, date));
        return "expenditure/menu";
    }

    @ModelAttribute("tags")
    public List<Tag> tags(Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return tagRepository.findAllByUser(user);
    }
}
