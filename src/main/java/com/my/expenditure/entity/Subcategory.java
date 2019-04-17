package com.my.expenditure.entity;

import org.omg.CORBA.INITIALIZE;

import javax.persistence.*;

@Entity
@Table(name="subcategories")
public class Subcategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    //TODO many to one with category (optional)
}
