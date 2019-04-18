package com.my.expenditure.controller;

import com.my.expenditure.entity.Category;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.CategoryRepository;
import com.my.expenditure.service.MyUserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;

@Controller
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

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
        return "category/categoryListTest";
    }

}
