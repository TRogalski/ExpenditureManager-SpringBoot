package com.my.expenditure.controller;

import com.my.expenditure.model.Expenditure;
import com.my.expenditure.model.Tag;
import com.my.expenditure.model.User;
import com.my.expenditure.repository.ExpenditureRepository;
import com.my.expenditure.repository.TagRepository;
import com.my.expenditure.repository.UserRepository;
import com.my.expenditure.service.tag.TagService;
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
@RequestMapping("/tag")
public class TagController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private TagService tagService;

    @RequestMapping(value = "/add", method = RequestMethod.GET)
    public String getAddTagView(Model model) {
        model.addAttribute("tag", new Tag());
        return "tag/add";
    }

    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public String addTag(@ModelAttribute @Valid Tag tag, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "tag/add";
        }
        User user = userRepository.findFirstByEmail(principal.getName());
        tag.setUser(user);
        tagRepository.save(tag);
        return "redirect:list";
    }

    @RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
    public String getEditTagView(@PathVariable Long id, Model model) {
        Tag tag = tagRepository.getOne(id);
        model.addAttribute("tag", tag);
        return "tag/edit";
    }

    @RequestMapping(value = "/edit", method = RequestMethod.POST)
    public String editTag(@ModelAttribute @Valid Tag tag, BindingResult result, Principal principal) {
        if (result.hasErrors()) {
            return "tag/edit";
        }
        User user = userRepository.findFirstByEmail(principal.getName());
        tag.setUser(user);
        tagRepository.save(tag);
        return "redirect:list";
    }

    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    public String deleteTag(@RequestParam("id") Long id, Principal principal) {
        Tag tag = tagRepository.getOne(id);
        User user = userRepository.findFirstByEmail(principal.getName());
        List<Expenditure> expenditures = expenditureRepository.findAllByUser(user);

        for (Expenditure expenditure : expenditures) {
            expenditure.getTags().remove(tag);
        }

        tagRepository.delete(tag);
        return "redirect:list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    public String listTagsCurrentMonth(Model model, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        model.addAttribute("tagDashboardData", tagService.getTagViewDashboardDataforDate(user, Date.valueOf(LocalDate.now())));
        return "tag/list";
    }

    @RequestMapping(value = "/list/{date}", method = RequestMethod.GET)
    @ResponseBody
    public String listTagsSelectedMonth(@PathVariable Date date, Principal principal) {
        User user = userRepository.findFirstByEmail(principal.getName());
        return tagService.getTagViewDashboardDataforDate(user, date).toString();
    }
}
