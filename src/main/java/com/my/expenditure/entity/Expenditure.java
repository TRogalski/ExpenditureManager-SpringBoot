package com.my.expenditure.entity;

import javax.persistence.*;

@Entity
@Table(name = "expenditures")
public class Expenditure {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private Double amount;
    private String date;
    private String created;
    private String modified;
//    TODO many to one with category
//    TODO many to one with user
}
