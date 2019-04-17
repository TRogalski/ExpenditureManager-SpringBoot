package com.my.expenditure.repository;

import com.my.expenditure.entity.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpeditureRepository extends JpaRepository<Expenditure, Long> {
}
