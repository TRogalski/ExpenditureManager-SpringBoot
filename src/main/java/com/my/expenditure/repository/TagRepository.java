package com.my.expenditure.repository;

import com.my.expenditure.model.Tag;
import com.my.expenditure.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("SELECT t FROM Tag t WHERE t.user=:user")
    List<Tag> findAllByUser(@Param("user") User user);

}
