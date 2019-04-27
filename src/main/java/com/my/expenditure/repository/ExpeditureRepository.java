package com.my.expenditure.repository;

import com.my.expenditure.entity.Expenditure;
import com.my.expenditure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpeditureRepository extends JpaRepository<Expenditure, Long> {

    List<Expenditure> findAllByUser(User user);

    @Query("SELECT e FROM Expenditure e where e.date=:date and e.user=:user")
    List<Expenditure> findAllByUserAndDate(@Param("user") User user,@Param("date") String date);
}
