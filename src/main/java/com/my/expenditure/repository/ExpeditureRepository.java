package com.my.expenditure.repository;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ExpeditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findAllByUser(User user);

    @Query("SELECT e FROM Expenditure e where e.date=:date and e.user=:user")
    List<Expenditure> findAllByUserAndDate(@Param("date") String date, @Param("user") User user);
}
