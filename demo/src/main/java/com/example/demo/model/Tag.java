package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Tag {

    @Id
    @SequenceGenerator(name = "tag_gen",sequenceName = "tag_seq",allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator ="tag_gen" )
    @Column(name = "tag_id")
    private Integer id;

    private String name;

}
