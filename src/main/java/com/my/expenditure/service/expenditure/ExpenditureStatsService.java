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

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.*;


@Service
public class ExpenditureStatsService {

    @Autowired
    private ExpenditureRepository expenditureRepository;

    @Autowired
    private TagRepository tagRepository;

    public JSONObject getMainDashboardData(User user, Date date) {
        JSONObject jsonObject = new JSONObject()
                .put("date", date)
                .put("currentMonthName", getMonthName(date))
                .put("previousMonthName", getMonthName(Date.valueOf(date.toLocalDate().minusMonths(1))))
                .put("currentYearName", getYearName(date))
                .put("previousYearName", getYearName(Date.valueOf(date.toLocalDate().minusYears(1))))
                .put("currentMonthTotal", getMonthTotal(user, date))
                .put("previousMonthTotal", getMonthTotal(user, Date.valueOf(date.toLocalDate().minusMonths(1))))
                .put("currentYearTotal", getYearTotal(user, date))
                .put("previousYearTotal", getYearTotal(user, Date.valueOf(date.toLocalDate().minusMonths(1))))
                .put("currentYearMonthlyTimeSeries", getMonthlyTimeSeriesForYear(user, date))
                .put("currentMonthDailyTimeSeries", getDailyTimeSeriesForMonth(user, date))
                .put("previousMonthDailyTimeSeries", getDailyTimeSeriesForMonth(user, Date.valueOf(date.toLocalDate().minusMonths(1))))
                .put("currentMonthTagTotals", getMonthTotalsForTags(user, date))
                .put("currentYearMonthlyAverage", getYearAverage(user, date));
        return jsonObject;
    }

    private Double getYearAverage(User user, Date date) {
        List<Double> monthlySpendings = getMonthlyTimeSeriesForYear(user, date);

        Double sum=0.0;
        Integer count=0;

        for(Double monthlySpending:monthlySpendings){
            if(monthlySpending>0){
                sum+=monthlySpending;
                count++;
            }
        }

        return count==0?null:sum/count;
    }

    public JSONObject getDatepickerMonthlyTotals(User user, Date date) {
        JSONObject jsonObject = new JSONObject()
                .put("date", date)
                .put("monthlyTotalTimeSeries", getMonthlyTotalTimeSeries(user));
        return jsonObject;
    }

    public JSONObject getTagRadarChartMonthlyDataForYear(User user, Date date) {
        JSONObject jsonChartData = new JSONObject();

        List<Date> yearMonths = getMonthsForYear(date);

        for (Date month : yearMonths) {
            jsonChartData.put(getMonthName(month), getMonthTagTotals(user, month));
        }

        JSONObject jsonObject = new JSONObject()
                .put("date", date)
                .put("currentMonthName", getMonthName(date))
                .put("previousMonthName", getMonthName(Date.valueOf(date.toLocalDate().minusMonths(1))))
                .put("currentYearName", getYearName(date))
                .put("previousYearName", getYearName(Date.valueOf(date.toLocalDate().minusYears(1))))
                .put("tagRadarChartMonthlyDataForYear", jsonChartData)
                .put("tagCurrentMonthTotals", getMonthTotalsForTags(user, date))
                .put("tagPreviousMonthTotals", getMonthTotalsForTags(user, Date.valueOf(date.toLocalDate().minusMonths(1))));
        return jsonObject;
    }


    private String getMonthName(Date date) {
        return new SimpleDateFormat("MMM", Locale.US).format(date);
    }


    private String getYearName(Date date) {
        return new SimpleDateFormat("YYYY").format(date);
    }


    private Double getMonthTotal(User user, Date date) {
        return expenditureRepository.getMonthTotalForUser(user, date);
    }


    private Double getYearTotal(User user, Date date) {
        return expenditureRepository.getYearTotalForUser(user, date);
    }


    private List<Double> getMonthlyTimeSeriesForYear(User user, Date date) {
        List<Expenditure> expenditures = expenditureRepository.findAllYearExpendituresForUser(user, date);

        Map<Integer, Double> timeSeries = new LinkedHashMap<>();

        for (Integer i = 1; i <= 12; i++) {
            timeSeries.put(i, 0.0);
        }

        Double monthExpenditure = 0.0;
        Integer month;

        for (Expenditure expenditure : expenditures) {
            month = expenditure.getDate().toLocalDate().getMonthValue();
            monthExpenditure = timeSeries.get(month);
            timeSeries.put(month, monthExpenditure + expenditure.getAmount());
        }

        return new ArrayList<>(timeSeries.values());
    }


    private Map<Integer, Double> getDailyTimeSeriesForMonth(User user, Date date) {
        Map<Integer, Double> dailyTimeSeriesForMonth = new HashMap<>();

        Integer month = date.toLocalDate().getMonth().getValue();
        Integer year = date.toLocalDate().getYear();

        YearMonth yearMonthObject = YearMonth.of(year, month);
        Integer daysInMonth = yearMonthObject.lengthOfMonth();

        for (Integer i = 1; i <= daysInMonth; i++) {
            dailyTimeSeriesForMonth.put(i, 0.0);
        }

        List<Expenditure> expenditures = expenditureRepository.findAllByUserAndMonth(user, date);

        Integer day;
        for (Expenditure expenditure : expenditures) {
            day = expenditure.getDate().toLocalDate().getDayOfMonth();
            dailyTimeSeriesForMonth.put(day, dailyTimeSeriesForMonth.get(day) + expenditure.getAmount());
        }

        return dailyTimeSeriesForMonth;
    }


    public JSONArray getMonthTotalsForTags(User user, Date date) {
        Map<Tag, Double> monthTotalsOnTags = new LinkedHashMap<>();
        Map<Tag, Integer> monthCountsOnTags = new LinkedHashMap<>();

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

        JSONArray jsonArray = new JSONArray();

        for (Tag tag : monthTotalsOnTags.keySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", tag.getId());
            jsonObject.put("name", tag.getName());
            jsonObject.put("total", monthTotalsOnTags.get(tag));
            jsonObject.put("count", monthCountsOnTags.get(tag));
            jsonArray.put(jsonObject);
        }

        if (unassignedTotal > 0.0) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", -1);
            jsonObject.put("name", "Unassigned");
            jsonObject.put("total", unassignedTotal);
            jsonObject.put("count", unassignedCount);
            jsonArray.put(jsonObject);
        }
        return jsonArray;
    }


    private Map<String, Double> getMonthlyTotalTimeSeries(User user) {
        List<Expenditure> expenditures = expenditureRepository.findAllByUser(user);
        Map<String, Double> monthlyTotalTimeSeries = new HashMap<>();

        for (Expenditure expenditure : expenditures) {
            if (monthlyTotalTimeSeries.containsKey(String.valueOf(expenditure.getDate()))) {
                monthlyTotalTimeSeries.put(String.valueOf(expenditure.getDate()), monthlyTotalTimeSeries.get(String.valueOf(expenditure.getDate())) + expenditure.getAmount());
            } else {
                monthlyTotalTimeSeries.put(String.valueOf(expenditure.getDate()), expenditure.getAmount());
            }
        }
        return monthlyTotalTimeSeries;
    }

    private Map<String, Double> getMonthTagTotals(User user, Date date) {
        Map<String, Double> monthTagTotals = new HashMap<>();
        List<Tag> tags = tagRepository.findAllByUser(user);

        for (Tag tag : tags) {

            if (!monthTagTotals.containsKey(tag.getName())) {
                monthTagTotals.put(tag.getName(), 0.0);
            }

            List<Expenditure> expenditures = expenditureRepository.findAllByTagAndDateAndUser(tag, date, user);
            for (Expenditure expenditure : expenditures) {
                monthTagTotals.put(tag.getName(), monthTagTotals.get(tag.getName()) + expenditure.getAmount());
            }

            Double monthTotal = expenditureRepository.getMonthTotalForUser(user, date);

            if (monthTotal != null) {
                monthTagTotals.put(tag.getName(), Math.round(monthTagTotals.get(tag.getName()) / monthTotal * 100.0) / 100.0);
            }
        }
        return monthTagTotals;
    }


    private List<Date> getMonthsForYear(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set(Calendar.MONTH, 0);
        calendar.set(Calendar.YEAR, date.toLocalDate().getYear());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        List<Date> yearMonths = new ArrayList<>();

        for (int i = 0; i <= 11; i++) {
            yearMonths.add(Date.valueOf(simpleDateFormat.format(calendar.getTime())));
            calendar.add(Calendar.MONTH, 1);
        }
        return yearMonths;
    }

}
