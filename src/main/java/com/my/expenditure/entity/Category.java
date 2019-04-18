package com.my.expenditure.entity;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    private User user;

    @OneToMany(mappedBy = "category")
    private List<Expenditure> expenditures = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private List<Subcategory> subcategories = new ArrayList<>();
    
}