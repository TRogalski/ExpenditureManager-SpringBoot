package com.my.expenditure.repository;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.Tag;
import com.my.expenditure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findAllByUser(User user);

    @Query("SELECT e FROM Expenditure e where e.date=:date and e.user=:user")
    List<Expenditure> findAllByUserAndDate(@Param("user") User user, @Param("date") Date date);

//    @Query("SELECT e FROM Expenditure e where SUBSTRING(e.date,6,2)=SUBSTRING(:date,6,2) and SUBSTRING(e.date,1,4)=SUBSTRING(:date,1,4) and e.user=:user")
//    List<Expenditure> findAllByUserAndMonth(@Param("user") User user,@Param("date") String date);

    @Query("SELECT e FROM Expenditure e where EXTRACT(MONTH FROM e.date)=EXTRACT(MONTH FROM :date) and EXTRACT(YEAR FROM e.date)=EXTRACT( YEAR FROM :date) and e.user=:user")
    List<Expenditure> findAllByUserAndMonth(@Param("user") User user, @Param("date") Date date);

//    @Query("SELECT SUM(e.amount) FROM Expenditure e where " +
//            "SUBSTRING(e.date,1,4)=SUBSTRING(:date,1,4) and " +
//            "SUBSTRING(e.date,6,2)=SUBSTRING(:date,6,2) and " +
//            "e.user=:user")
//    Double getCurrentMonthTotal(@Param("user") User user, @Param("date") String date);

    @Query("SELECT SUM(e.amount) FROM Expenditure e where " +
            "EXTRACT(YEAR FROM e.date)=EXTRACT(YEAR FROM :date) and " +
            "EXTRACT(MONTH FROM e.date)=EXTRACT(MONTH FROM :date) and " +
            "e.user=:user")
    Double getCurrentMonthTotal(@Param("user") User user, @Param("date") Date date);

//    @Query("SELECT SUM(e.amount) FROM Expenditure  e where SUBSTRING(e.date,1,4)=SUBSTRING(:date,1,4) and e.user=:user")
//    Double getCurrentYearTotal(@Param("user") User user, @Param("date") String date);

    @Query("SELECT SUM(e.amount) FROM Expenditure  e where EXTRACT(YEAR FROM e.date)=EXTRACT(YEAR FROM :date) and e.user=:user")
    Double getCurrentYearTotal(@Param("user") User user, @Param("date") Date date);

//    @Query("SELECT SUM(e.amount) FROM Expenditure  e where SUBSTRING(e.date,1,4)=(SUBSTRING(:date,1,4)) and e.user=:user")
//        //-1
//    Double getPreviousYearTotal(@Param("user") User user, @Param("date") String date);

    @Query("SELECT SUM(e.amount) FROM Expenditure  e where EXTRACT(YEAR FROM e.date)=(EXTRACT(YEAR FROM :date)-1) and e.user=:user")
    Double getPreviousYearTotal(@Param("user") User user, @Param("date") Date date);

//    @Query("SELECT e FROM Expenditure e WHERE SUBSTRING(e.date,1,4)=SUBSTRING(:date,1,4) and e.user=:user")
//    List<Expenditure> getCurrentYearMonthlyExpenditures(@Param("user") User user, @Param("date") String date);

    @Query("SELECT e FROM Expenditure e WHERE EXTRACT(YEAR FROM e.date)=EXTRACT(YEAR FROM :date) and e.user=:user")
    List<Expenditure> getCurrentYearMonthlyExpenditures(@Param("user") User user, @Param("date") Date date);

//    @Query("SELECT e FROM Expenditure e " +
//            "WHERE :tag MEMBER OF e.tags " +
//            "AND SUBSTRING(e.date,6,2)=SUBSTRING(:date,6,2) " +
//            "AND SUBSTRING(e.date,1,4)=SUBSTRING(:date,1,4) " +
//            "AND e.user=:user")
//    List<Expenditure> findAllByTagAndDateAndUser(@Param("tag") Tag tag, @Param("date") String date, @Param("user") User user);

    @Query("SELECT e FROM Expenditure e " +
            "WHERE :tag MEMBER OF e.tags " +
            "AND EXTRACT(MONTH FROM e.date)=EXTRACT(MONTH FROM :date) " +
            "AND EXTRACT(YEAR FROM e.date)=EXTRACT(YEAR FROM :date) " +
            "AND e.user=:user")
    List<Expenditure> findAllByTagAndDateAndUser(@Param("tag") Tag tag, @Param("date") Date date, @Param("user") User user);


//    @Query("SELECT e FROM Expenditure e " +
//            "WHERE e.tags is empty " +
//            "AND SUBSTRING(e.date,6,2)=SUBSTRING(:date,6,2) " +
//            "and SUBSTRING(e.date,1,4)=SUBSTRING(:date,1,4) " +
//            "and e.user=:user")
//    List<Expenditure> findAllUnassignedByDateAndUser(@Param("date") String date, @Param("user") User user);

    @Query("SELECT e FROM Expenditure e " +
            "WHERE e.tags is empty " +
            "AND EXTRACT(MONTH FROM e.date)=EXTRACT(MONTH FROM :date) " +
            "and EXTRACT(YEAR FROM e.date)=EXTRACT(YEAR FROM :date) " +
            "and e.user=:user")
    List<Expenditure> findAllUnassignedByDateAndUser(@Param("date") Date date, @Param("user") User user);


//    @Query("SELECT SUM(e.amount) FROM Expenditure e where " +
//            "SUBSTRING(e.date,1,4)=SUBSTRING(:date,1,4) and " +
//            "SUBSTRING(e.date,6,2)=(SUBSTRING(:date,6,2)) and " +    //-1
//            "e.user=:user")
//    Double getPreviousMonthTotal(@Param("user") User user, @Param("date") String date);

    @Query("SELECT SUM(e.amount) FROM Expenditure e where " +
            "EXTRACT(YEAR FROM e.date)=EXTRACT(YEAR FROM :date) and " +
            "EXTRACT(MONTH FROM e.date)=(EXTRACT(MONTH FROM :date)-1) and " +    //-1
            "e.user=:user")
    Double getPreviousMonthTotal(@Param("user") User user, @Param("date") Date date);

//    @Query("SELECT e FROM Expenditure e " +
//            "WHERE :tag MEMBER OF e.tags " +
//            "AND SUBSTRING(e.date,6,2)=(SUBSTRING(:date,6,2)) " +  //-1
//            "AND SUBSTRING(e.date,1,4)=SUBSTRING(:date,1,4) " +
//            "AND e.user=:user")
//    List<Expenditure> findAllByTagAndDatePreviousAndUser(@Param("tag") Tag tag, @Param("date") String date, @Param("user") User user);

    @Query("SELECT e FROM Expenditure e " +
            "WHERE :tag MEMBER OF e.tags " +
            "AND EXTRACT(MONTH FROM e.date)=(EXTRACT(MONTH FROM :date)-1) " +  //-1
            "AND EXTRACT(YEAR FROM e.date)=EXTRACT(YEAR FROM :date) " +
            "AND e.user=:user")
    List<Expenditure> findAllByTagAndDatePreviousAndUser(@Param("tag") Tag tag, @Param("date") Date date, @Param("user") User user);
}
