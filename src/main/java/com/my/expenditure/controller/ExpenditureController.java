package com.my.expenditure.controller;

import com.my.expenditure.entity.Category;
import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.Subcategory;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.CategoryRepository;
import com.my.expenditure.repository.ExpeditureRepository;
import com.my.expenditure.repository.SubcategoryRepository;
import com.my.expenditure.repository.UserRepository;
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
    private ExpeditureRepository expeditureRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(value = "/add",method = RequestMethod.GET)
    public String getAddExpenditureView(Model model){
        model.addAttribute("expenditure",new Expenditure());
        return "expenditure/add";
    }

    @RequestMapping(value = "/add",method = RequestMethod.POST)
    public String addExpenditure(@ModelAttribute Expenditure expenditure, Principal principal){
        User user=userRepository.findFirstByEmail(principal.getName());
        expenditure.setUser(user);
        expenditure.setCreated(String.valueOf(LocalDate.now()));
        expeditureRepository.save(expenditure);
        return "redirect:list";
    }

    @RequestMapping(value="/list",method=RequestMethod.GET)
    private String getListView(Model model, Principal principal){
        User user=userRepository.findFirstByEmail(principal.getName());
        model.addAttribute("expenditures",expeditureRepository.findAllByUser(user));
        return "expenditure/list";
    }


    @RequestMapping(value="/date/{date}",method = RequestMethod.GET)
    @ResponseBody
    private List<Expenditure> getJson(@PathVariable String date, Principal principal){
        User user=userRepository.findFirstByEmail(principal.getName());
        return expeditureRepository.findAllByUserAndDate(date,user);
    }

    @ModelAttribute("categories")
    public List<Category> categories(Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return categoryRepository.findAllByUser(user);
    }

    @ModelAttribute("subcategories")
    public List<Subcategory> subcategories(Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return subcategoryRepository.findAllByUser(user);
    }

}
