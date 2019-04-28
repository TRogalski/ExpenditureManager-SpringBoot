package com.my.expenditure.service.expenditure;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.User;
import com.my.expenditure.repository.ExpenditureRepository;
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

    public JSONObject getStats(User user, String date) {
        JSONObject jsonObject = new JSONObject()
                .put("date", date)
                .put("monthTotal", getCurrentMonthTotal(user, date))
                .put("yearTotal", getCurrentYearTotal(user, date))
                .put("timeSeries", getCurrentYearMonthTotals(user, date));
        return jsonObject;
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
//
//    private Double getCurrentMonthTagsTotal(User user, String date, List<Tag> tags) {
//    }
//
//    private Double getCurrentYearTagsTotal(User user, String date, List<Tag> tags) {
//    }

}
