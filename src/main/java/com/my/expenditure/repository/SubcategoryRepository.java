package com.my.expenditure.repository;

import com.my.expenditure.entity.Subcategory;
import com.my.expenditure.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubcategoryRepository extends JpaRepository<Subcategory, Long> {
    List<Subcategory> findAllByUser(User user);
}
