package com.my.expenditure.service.expenditure;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.Tag;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.ExpenditureRepository;
import com.my.expenditure.repository.TagRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ExpenditureStatsService {

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private TagRepository tagRepository;

    public JSONObject getStats(User user, String date) {
        JSONObject jsonObject = new JSONObject()
                .put("date", date)
                .put("currentYear", getCurrentYear(user, date))
                .put("monthTotal", getCurrentMonthTotal(user, date))
                .put("yearTotal", getCurrentYearTotal(user, date))
                .put("timeSeries", getCurrentYearMonthTotals(user, date))
                .put("topTags",getCurrentMonthTopTags(user,date));



        return jsonObject;
    }

    private Integer getCurrentYear(User user, String date) {
        return LocalDate.parse(date).getYear();
    }


    private Double getCurrentMonthTotal(User user, String date) {
        Double currentMonthTotal = expenditureRepository.getCurrentMonthTotal(user, date);
        return currentMonthTotal;
    }

    private Double getCurrentYearTotal(User user, String date) {
        Double currentYearTotal = expenditureRepository.getCurrentMonthTotal(user, date);
        return currentYearTotal;
    }

    public List<Double> getCurrentYearMonthTotals(User user, String date) {
        List<Expenditure> expenditures = expenditureRepository.getCurrentYearMonthlyExpenditures(user, date);

        Map<String, Double> timeSeries = new LinkedHashMap<>();

        for (Integer i = 1; i <= 12; i++) {
            timeSeries.put(String.valueOf(i), 0.0);
        }

        Double monthExpenditure = 0.0;
        String month;

        for (Expenditure expenditure : expenditures) {
            month = String.valueOf(LocalDate.parse(expenditure.getDate()).getMonthValue());
            monthExpenditure = timeSeries.get(month);
            timeSeries.put(month, monthExpenditure + expenditure.getAmount());
        }

        return new ArrayList<>(timeSeries.values());
    }

    public JSONArray getCurrentMonthTopTags(User user, String date) {
        Map<Tag, Double> topCurrentMonthTags = new LinkedHashMap<>();

        List<Expenditure> expenditures = expenditureRepository.findAllByUserAndMonth(user, date);

        for (Expenditure expenditure : expenditures) {
            for (Tag tag : expenditure.getTags()) {
                if (topCurrentMonthTags.containsKey(tag)) {
                    topCurrentMonthTags.put(tag, topCurrentMonthTags.get(tag) + expenditure.getAmount());
                } else {
                    topCurrentMonthTags.put(tag, expenditure.getAmount());
                }
            }
        }

        JSONArray jsonArray = new JSONArray();

        for(Tag tag:topCurrentMonthTags.keySet()){
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id",tag.getId());
            jsonObject.put("name",tag.getName());
            jsonObject.put("monthTotal",topCurrentMonthTags.get(tag));
            jsonArray.put(jsonObject);
        }

        return jsonArray;
    }

//    public Map<String,Double> getCurrentMonthTopTags(){}
//
//    private Double getCurrentMonthTagsTotal(User user, String date, List<Tag> tags) {
//    }
//
//    private Double getCurrentYearTagsTotal(User user, String date, List<Tag> tags) {
//    }

}
