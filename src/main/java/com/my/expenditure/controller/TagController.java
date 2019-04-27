package com.my.expenditure.controller;

import com.my.expenditure.entity.Tag;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.TagRepository;
import com.my.expenditure.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String getAddTagView(Model model) {
        model.addAttribute("tag", new Tag());
        return "tag/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addTag(@ModelAttribute Tag tag, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        tag.setUser(user);
        tagRepository.save(tag);
        return "redirect:list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listTags(Model model, Principal principal) {
        List<Tag> tags = tagRepository.findAllByUser(userRepository.findFirstByEmail(principal.getName()));
        model.addAttribute("tags", tags);
        return "tag/list";
    }
}
