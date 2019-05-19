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
                .put("previousMonthTagTotals", getMonthTotalsForTags(user, Date.valueOf(date.toLocalDate().minusMonths(1))));
        return jsonObject;
    }

    public JSONObject getDatepickerMonthlyTotals(User user, Date date) {
        JSONObject jsonObject = new JSONObject()
                .put("date", date)
                .put("monthlyTotalTimeSeries", getMonthlyTotalTimeSeries(user));
        return jsonObject;
    }

    public JSONObject getTagRadarChartMonthlyDataForYear(User user, Date date) {
        JSONObject jsonObject = new JSONObject();

        List<Date> yearMonths=getMonthsForYear(date);

        for(Date month:yearMonths){
            jsonObject.put(getMonthName(month),getMonthTagTotals(user, month));
        }
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


    private JSONArray getMonthTotalsForTags(User user, Date date) {
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
            if (monthlyTotalTimeSeries.containsKey(expenditure.getDate())) {
                monthlyTotalTimeSeries.put(String.valueOf(expenditure.getDate()), monthlyTotalTimeSeries.get(expenditure.getDate()) + expenditure.getAmount());
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


//    OLD

//    public JSONObject getStats(User user, Date date) {
//        JSONObject jsonObject = new JSONObject()
//                .put("date", date)
//                .put("currentYear", getCurrentYear(user, date))
//                .put("monthTotal", getCurrentMonthTotal(user, date))
//                .put("yearTotal", getCurrentYearTotal(user, date))
//                .put("previousYearTotal", getPreviousYearTotal(user, date))
//                .put("previousMonthTotal", getPreviousMonthTotal(user, date))
//                .put("timeSeries", getCurrentYearMonthTotals(user, date))
//                .put("topTags", getCurrentMonthTopTags(user, date))
//                .put("totalTimeSeries", getTotalTimeSeries(user))
//                .put("tagTotals", getTagTotalsThisMonth(user, date))
//                .put("previousTagTotals", getTagTotalsLastMonth(user, date));
//                .put("currentMonthTotalsTimeSeries", getCurrentMonthTotalsTimeSeries(user, date));

//        return jsonObject;
//    }

//    private Integer getCurrentYear(User user, Date date) {
//        return date.toLocalDate().getYear();
//    }


//    private Double getCurrentMonthTotal(User user, Date date) {
//        Double currentMonthTotal = expenditureRepository.getCurrentMonthTotal(user, date);
//        return currentMonthTotal;
//    }

//    private Double getPreviousMonthTotal(User user, Date date) {
//        Double previousMonthTotal = expenditureRepository.getPreviousMonthTotal(user, date);
//
//        previousMonthTotal = previousMonthTotal == null ? 0.0 : previousMonthTotal;
//
//        return previousMonthTotal;
//    }
//
//    private Double getCurrentYearTotal(User user, Date date) {
//        Double currentYearTotal = expenditureRepository.getCurrentYearTotal(user, date);
//        return currentYearTotal;
//    }
//
//    private Double getPreviousYearTotal(User user, Date date) {
//        Double previousYearTotal = expenditureRepository.getPreviousMonthTotal(user, date);
//        return previousYearTotal;
//    }

//    private List<Double> getCurrentYearMonthTotals(User user, Date date) {
//        List<Expenditure> expenditures = expenditureRepository.getCurrentYearMonthlyExpenditures(user, date);
//
//        Map<String, Double> timeSeries = new LinkedHashMap<>();
//
//        for (Integer i = 1; i <= 12; i++) {
//            timeSeries.put(String.valueOf(i), 0.0);
//        }
//
//        Double monthExpenditure = 0.0;
//        String month;
//
//        for (Expenditure expenditure : expenditures) {
//            month = String.valueOf(expenditure.getDate().toLocalDate().getMonthValue());
//            monthExpenditure = timeSeries.get(month);
//            timeSeries.put(month, monthExpenditure + expenditure.getAmount());
//        }
//
//        return new ArrayList<>(timeSeries.values());
//    }

//    private JSONArray getCurrentMonthTopTags(User user, Date date) {
//        Map<Tag, Double> topCurrentMonthTagsTotals = new LinkedHashMap<>();
//        Map<Tag, Integer> topCurrentMonthTagsCounts = new LinkedHashMap<>();
//
//        List<Expenditure> expenditures = expenditureRepository.findAllByUserAndMonth(user, date);
//
//        Double unassignedTotal = 0.0;
//        Integer unassignedCount = 0;
//
//        for (Expenditure expenditure : expenditures) {
//            if (expenditure.getTags().isEmpty()) {
//                unassignedTotal += expenditure.getAmount();
//                unassignedCount++;
//            }
//            for (Tag tag : expenditure.getTags()) {
//                if (topCurrentMonthTagsTotals.containsKey(tag)) {
//                    topCurrentMonthTagsTotals.put(tag, topCurrentMonthTagsTotals.get(tag) + expenditure.getAmount());
//                    topCurrentMonthTagsCounts.put(tag, topCurrentMonthTagsCounts.get(tag) + 1);
//                } else {
//                    topCurrentMonthTagsTotals.put(tag, expenditure.getAmount());
//                    topCurrentMonthTagsCounts.put(tag, 1);
//                }
//            }
//        }
//
//        JSONArray jsonArray = new JSONArray();
//
//        for (Tag tag : topCurrentMonthTagsTotals.keySet()) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("id", tag.getId());
//            jsonObject.put("name", tag.getName());
//            jsonObject.put("monthTotal", topCurrentMonthTagsTotals.get(tag));
//            jsonObject.put("monthCount", topCurrentMonthTagsCounts.get(tag));
//            jsonArray.put(jsonObject);
//        }
//
//        if (unassignedTotal > 0.0) {
//            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("id", -1);
//            jsonObject.put("name", "Unassigned");
//            jsonObject.put("monthTotal", unassignedTotal);
//            jsonObject.put("monthCount", unassignedCount);
//            jsonArray.put(jsonObject);
//        }
//        return jsonArray;
//    }

//    private Map<String, Double> getTotalTimeSeries(User user) {
//        List<Expenditure> expenditures = expenditureRepository.findAllByUser(user);
//        Map<String, Double> totalTimeSeries = new HashMap<>();
//
//        for (Expenditure expenditure : expenditures) {
//            if (totalTimeSeries.containsKey(expenditure.getDate())) {
//                totalTimeSeries.put(String.valueOf(expenditure.getDate()), totalTimeSeries.get(expenditure.getDate()) + expenditure.getAmount());
//            } else {
//                totalTimeSeries.put(String.valueOf(expenditure.getDate()), expenditure.getAmount());
//            }
//        }
//        return totalTimeSeries;
//    }


//    private Map<String, Double> getTagTotalsLastMonth(User user, Date date) {
//        Map<String, Double> tagTotals = new HashMap<>();
//        List<Tag> tags = tagRepository.findAllByUser(user);
//
//        for (Tag tag : tags) {
//
//            if (!tagTotals.containsKey(tag.getName())) {
//                tagTotals.put(tag.getName(), 0.0);
//            }
//
//            List<Expenditure> expenditures = expenditureRepository.findAllByTagAndDatePreviousAndUser(tag, date, user);
//            for (Expenditure expenditure : expenditures) {
//                tagTotals.put(tag.getName(), tagTotals.get(tag.getName()) + expenditure.getAmount());
//            }
//
//            Double previousMonthTotal = expenditureRepository.getPreviousMonthTotal(user, date);
//
//            if (previousMonthTotal != null) {
//                tagTotals.put(tag.getName(), Math.round(tagTotals.get(tag.getName()) / previousMonthTotal * 100.0) / 100.0);
//            }
//        }
//
//        return tagTotals;
//    }

//    private Map<Integer, Double> getCurrentMonthTotalsTimeSeries(User user, Date date) {
//
//        Map<Integer, Double> currentMonthTotalsTimeSeries = new HashMap<>();
//
//        String month = String.valueOf(date.toLocalDate().getMonth().getValue());
//        String year = String.valueOf(date.toLocalDate().getYear());
//
//        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
//        Integer daysInMonth = yearMonthObject.lengthOfMonth();
//
//        for (Integer i = 1; i <= daysInMonth; i++) {
//            currentMonthTotalsTimeSeries.put(i, 0.0);
//        }
//
//        List<Expenditure> expenditures = expenditureRepository.findAllByUserAndMonth(user, date);
//
//        String day;
//        for (Expenditure expenditure : expenditures) {
//            day = String.valueOf(expenditure.getDate().toLocalDate().getDayOfMonth());
//            currentMonthTotalsTimeSeries.put(Integer.valueOf(day), currentMonthTotalsTimeSeries.get(Integer.valueOf(day)) + expenditure.getAmount());
//        }
//
//        return currentMonthTotalsTimeSeries;
//    }

}
