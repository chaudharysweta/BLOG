package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Category {

    @Id
    @SequenceGenerator(name = "category_gen", sequenceName = "category_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="category_gen" )
    @Column(name = "category_id")
    private Integer id;

    private String name;
}
