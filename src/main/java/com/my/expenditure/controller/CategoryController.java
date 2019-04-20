package com.my.expenditure.controller;

import com.my.expenditure.entity.Category;
import com.my.expenditure.entity.Subcategory;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.CategoryRepository;
import com.my.expenditure.repository.SubcategoryRepository;
import com.my.expenditure.service.MyUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubcategoryRepository subcategoryRepository;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    private String getAddCategoryView(Model model) {
        model.addAttribute("category", new Category());
        return "category/addCategoryTest";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    private String addCategory(@ModelAttribute Category category) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserPrincipal myUserPrincipal = (MyUserPrincipal) authentication.getPrincipal();
        User user = myUserPrincipal.getUser();
        category.setUser(user);
        categoryRepository.save(category);
        return "redirect:list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    private String list() {
        return "category/categoryListTest";
    }

    @RequestMapping(value = "/addsub", method = RequestMethod.GET)
    private String getAddSubcategoryView(Model model) {
        model.addAttribute("subcategory", new Subcategory());
        return "subcategory/addSubcategoryTest";
    }

    @RequestMapping(value = "/addsub", method = RequestMethod.POST)
    private String addSubcategory(@ModelAttribute Subcategory subcategory) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserPrincipal myUserPrincipal = (MyUserPrincipal) authentication.getPrincipal();
        User user = myUserPrincipal.getUser();
        subcategory.setUser(user);
        subcategoryRepository.save(subcategory);
        return "redirect:sublist";
    }

    @RequestMapping(value = "/sublist", method = RequestMethod.GET)
    private String subList() {
        return "subcategory/subcategoryListTest";

    }

    @ModelAttribute("categories")
    public List<Category> categories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserPrincipal myUserPrincipal = (MyUserPrincipal) authentication.getPrincipal();
        User user = myUserPrincipal.getUser();
        return categoryRepository.findAllByUser(user);
    }

    @ModelAttribute("subcategories")
    public List<Subcategory> subcategories() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        MyUserPrincipal myUserPrincipal = (MyUserPrincipal) authentication.getPrincipal();
        User user = myUserPrincipal.getUser();
        return subcategoryRepository.findAllByUser(user);
    }

}
