package com.my.expenditure.service.tag;

import com.my.expenditure.entity.Tag;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.ExpenditureRepository;
import com.my.expenditure.repository.TagRepository;
import com.my.expenditure.repository.UserRepository;
import com.my.expenditure.service.expenditure.ExpenditureStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

@Service
public class TagService {

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExpenditureStatsService expenditureStatsService;

    public Model getTagViewDashboardData(Model model, Principal principal, Date date) {
        User user = userRepository.findFirstByEmail(principal.getName());
        List<Tag> tags = tagRepository.findAllByUser(user);

        model
                .addAttribute("tags", tags)
                .addAttribute("currentMonthName", new SimpleDateFormat("MMM", Locale.US).format(date))
                .addAttribute("previousMonthName", new SimpleDateFormat("MMM", Locale.US).format(Date.valueOf(date.toLocalDate().minusMonths(1))))
                .addAttribute("currentYearName", new SimpleDateFormat("yyyy", Locale.US).format(date))
                .addAttribute("currentMonthTagTotals", expenditureStatsService.getMonthTotalsForTags(user, date))
                .addAttribute("previousMonthTagTotals", expenditureStatsService.getMonthTotalsForTags(user, Date.valueOf(date.toLocalDate().minusMonths(1))));
        return model;
    }
}
