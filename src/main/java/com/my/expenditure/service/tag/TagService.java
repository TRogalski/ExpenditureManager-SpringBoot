package com.my.expenditure.service.tag;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.Tag;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.ExpenditureRepository;
import com.my.expenditure.repository.TagRepository;
import com.my.expenditure.repository.UserRepository;
import com.my.expenditure.service.expenditure.ExpenditureStatsService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.security.Principal;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.*;

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
                .addAttribute("currentMonthAllTagTotals", getMonthTotalsForAllTags(user, date))
                .addAttribute("previousMonthAllTagTotals", getMonthTotalsForAllTags(user, Date.valueOf(date.toLocalDate().minusMonths(1))))
                .addAttribute("currentYearAllTagTotals", getYearTotalsForAllTags(user, date));
        return model;
    }

    private JSONObject getYearTotalsForAllTags(User user, Date date) {
        Map<Tag, Double> yearTotalsOnTags = new HashMap<>();
        Map<Tag, Integer> yearCountsOnTags = new HashMap<>();

        List<Expenditure> expenditures = expenditureRepository.findAllByUserAndYear(user, date);

        Double unassignedTotal = 0.0;
        Integer unassignedCount = 0;

        for (Expenditure expenditure : expenditures) {
            if (expenditure.getTags().isEmpty()) {
                unassignedTotal += expenditure.getAmount();
                unassignedCount++;
            }
            for (Tag tag : expenditure.getTags()) {
                if (yearTotalsOnTags.containsKey(tag)) {
                    yearTotalsOnTags.put(tag, yearTotalsOnTags.get(tag) + expenditure.getAmount());
                    yearCountsOnTags.put(tag, yearCountsOnTags.get(tag) + 1);
                } else {
                    yearTotalsOnTags.put(tag, expenditure.getAmount());
                    yearCountsOnTags.put(tag, 1);
                }
            }
        }

        JSONObject tagData = new JSONObject();

        for (Tag tag : yearTotalsOnTags.keySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", tag.getId());
            jsonObject.put("name", tag.getName());
            jsonObject.put("total", yearTotalsOnTags.get(tag));
            jsonObject.put("count", yearCountsOnTags.get(tag));
            tagData.put(String.valueOf(tag.getId()), jsonObject);
        }

        List<Tag> tags = tagRepository.findAllByUser(user);

        for (Tag tag : tags) {
            if (!yearTotalsOnTags.keySet().contains(tag)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", tag.getId());
                jsonObject.put("name", tag.getName());
                jsonObject.put("total", 0.0);
                jsonObject.put("count", 0);
                tagData.put(String.valueOf(tag.getId()), jsonObject);
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", -1);
        jsonObject.put("name", "Unassigned");
        jsonObject.put("total", unassignedTotal);
        jsonObject.put("count", unassignedCount);
        tagData.put("-1", jsonObject);

        return tagData;

    }

    private JSONObject getMonthTotalsForAllTags(User user, Date date) {
        Map<Tag, Double> monthTotalsOnTags = new HashMap<>();
        Map<Tag, Integer> monthCountsOnTags = new HashMap<>();

        List<Expenditure> expenditures = expenditureRepository.findAllByUserAndMonth(user, date);

        Double unassignedTotal = 0.0;
        Integer unassignedCount = 0;

        for (Expenditure expenditure : expenditures) {
            if (expenditure.getTags().isEmpty()) {
                unassignedTotal += expenditure.getAmount();
                unassignedCount++;
            }
            for (Tag tag : expenditure.getTags()) {
                if (monthTotalsOnTags.containsKey(tag)) {
                    monthTotalsOnTags.put(tag, monthTotalsOnTags.get(tag) + expenditure.getAmount());
                    monthCountsOnTags.put(tag, monthCountsOnTags.get(tag) + 1);
                } else {
                    monthTotalsOnTags.put(tag, expenditure.getAmount());
                    monthCountsOnTags.put(tag, 1);
                }
            }
        }

        JSONObject tagData = new JSONObject();

        for (Tag tag : monthTotalsOnTags.keySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", tag.getId());
            jsonObject.put("name", tag.getName());
            jsonObject.put("total", monthTotalsOnTags.get(tag));
            jsonObject.put("count", monthCountsOnTags.get(tag));
            tagData.put(String.valueOf(tag.getId()), jsonObject);
        }

        List<Tag> tags = tagRepository.findAllByUser(user);

        for (Tag tag : tags) {
            if (!monthTotalsOnTags.keySet().contains(tag)) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", tag.getId());
                jsonObject.put("name", tag.getName());
                jsonObject.put("total", 0.0);
                jsonObject.put("count", 0);
                tagData.put(String.valueOf(tag.getId()), jsonObject);
            }
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", -1);
        jsonObject.put("name", "Unassigned");
        jsonObject.put("total", unassignedTotal);
        jsonObject.put("count", unassignedCount);
        tagData.put("-1", jsonObject);

        return tagData;
    }
}
