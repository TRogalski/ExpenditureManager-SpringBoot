package com.my.expenditure.service.expenditure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.ExpenditureRepository;
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

    public JsonObject getStats(User user, String date) {
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("date", date);
        jsonObject.addProperty("MonthTotal", getCurrentMonthTotal(user, date));
        jsonObject.addProperty("YearTotal", getCurrentYearTotal(user, date));

        Gson gson = new Gson();
        String timeSeries = gson.toJson(getCurrentYearMonthTotals(user, date));
        jsonObject.addProperty("timeSeries", timeSeries);
        return jsonObject;
    }


    private Double getCurrentMonthTotal(User user, String date) {

        JsonObject jsonObject = new JsonObject();
        Double currentMonthTotal = expenditureRepository.getCurrentMonthTotal(user, date);
        return currentMonthTotal;
    }

    private Double getCurrentYearTotal(User user, String date) {
        JsonObject jsonObject = new JsonObject();
        Double currentYearTotal = expenditureRepository.getCurrentMonthTotal(user, date);
        return currentYearTotal;
    }

    public List<Double> getCurrentYearMonthTotals(User user, String date) {
        List<Expenditure> expenditures = expenditureRepository.getCurrentYearMonthlyExpenditures(user, date);

        Map<String, Double> timeSeries = new LinkedHashMap<>();

        for (Integer i = 1; i <= 12; i++) {
            timeSeries.put(String.valueOf(i), 0.0);
        }

        Double temp = 0.0;
        String month;

        for (Expenditure expenditure : expenditures) {
            month = String.valueOf(LocalDate.parse(expenditure.getDate()).getMonthValue());
            temp = timeSeries.get(month);
            timeSeries.put(month, temp + expenditure.getAmount());
        }

        List<Double> timeSeriesList = new ArrayList<>();


        for (String item : timeSeries.keySet()) {
            timeSeriesList.add(timeSeries.get(item));
        }

        return timeSeriesList;
    }
//
//    private Double getCurrentMonthTagsTotal(User user, String date, List<Tag> tags) {
//    }
//
//    private Double getCurrentYearTagsTotal(User user, String date, List<Tag> tags) {
//    }

}
