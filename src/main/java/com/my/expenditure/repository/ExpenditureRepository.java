package com.my.expenditure.repository;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findAllByUser(User user);

    @Query("SELECT e FROM Expenditure e where e.date=:date and e.user=:user")
    List<Expenditure> findAllByUserAndDate(@Param("user") User user,@Param("date") String date);


    @Query("SELECT SUM(e.amount) FROM Expenditure e where " +
            "YEAR(e.date)=YEAR(:date) and " +
            "MONTH(e.date)=MONTH(:date) and " +
            "e.user=:user")
    Double getCurrentMonthTotal(@Param("user") User user, @Param("date") String date);


    @Query("SELECT SUM(e.amount) FROM Expenditure  e where YEAR(e.date)=YEAR(:date) and e.user=:user")
    Double getCurrentYearTotal(@Param("user") User user, @Param("date") String date);

//    @Query("SELECT new map(MONTH(e.date) as month, SUM(e.amount) as amount) FROM Expenditure e " +
//            "where YEAR(e.date)=YEAR(:date) and " +
//            "e.user=:user GROUP BY MONTH(e.date)")
//    List<?> getCurrentYearMonthlyExpenditures(@Param("user") User user, @Param("date") String date);

    @Query("SELECT e FROM Expenditure e WHERE YEAR(e.date)=YEAR(:date) and e.user=:user")
    List<Expenditure> getCurrentYearMonthlyExpenditures(@Param("user") User user, @Param("date") String date);
//
//    @Query("")
//    List<Expenditure> getCurrentYearMonthlyExpenditures(User user, String date);
}
