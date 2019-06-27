package com.my.expenditure.repository;

import com.my.expenditure.model.Expenditure;
import com.my.expenditure.model.Tag;
import com.my.expenditure.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.List;

@Repository
public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findAllByUser(User user);

    @Query("SELECT e FROM Expenditure e WHERE YEAR(e.date)=YEAR(:date) and e.user=:user")
    List<Expenditure> findAllYearExpendituresForUser(User user, Date date);

    @Query("SELECT e FROM Expenditure e where MONTH(e.date)=MONTH(:date) and YEAR(e.date)=YEAR(:date) and e.user=:user")
    List<Expenditure> findAllByUserAndMonth(@Param("user") User user, @Param("date") Date date);

    @Query("SELECT SUM(e.amount) FROM Expenditure e where " +
            "YEAR(e.date)=YEAR(:date) and " +
            "MONTH(e.date)=MONTH(:date) and " +
            "e.user=:user")
    Double getMonthTotalForUser(@Param("user") User user, @Param("date") Date date);

    @Query("SELECT SUM(e.amount) FROM Expenditure  e where YEAR(e.date)=YEAR(:date) and e.user=:user")
    Double getYearTotalForUser(@Param("user") User user, @Param("date") Date date);

    @Query("SELECT e FROM Expenditure e where e.date=:date and e.user=:user")
    List<Expenditure> findAllByUserAndDate(@Param("user") User user, @Param("date") Date date);

    @Query("SELECT e FROM Expenditure e " +
            "WHERE :tag MEMBER OF e.tags " +
            "AND MONTH(e.date)=MONTH(:date) " +
            "AND YEAR(e.date)=YEAR(:date) " +
            "AND e.user=:user")
    List<Expenditure> findAllByTagAndDateAndUser(@Param("tag") Tag tag, @Param("date") Date date, @Param("user") User user);

    @Query("SELECT e FROM Expenditure e " +
            "WHERE e.tags is empty " +
            "AND MONTH(e.date)=MONTH(:date) " +
            "and YEAR(e.date)=YEAR(:date) " +
            "and e.user=:user")
    List<Expenditure> findAllUnassignedByDateAndUser(@Param("date") Date date, @Param("user") User user);

    @Query("SELECT e FROM Expenditure e WHERE YEAR(e.date)=YEAR(:date) AND e.user=:user")
    List<Expenditure> findAllByUserAndYear(@Param("user") User user, @Param("date") Date date);
}
