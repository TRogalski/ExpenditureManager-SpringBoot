package com.my.expenditure.repository;

import com.my.expenditure.entity.Category;
import com.my.expenditure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    List<Category> findAllByUser(User user);
}
