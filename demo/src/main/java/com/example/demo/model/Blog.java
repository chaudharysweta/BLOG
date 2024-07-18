package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Data
public class Blog {

    @Id
    @SequenceGenerator(name = "blog_gen" ,sequenceName = "blog_seq")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "blog_gen")
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @ManyToMany(cascade = CascadeType.DETACH,fetch = FetchType.EAGER)
    @JoinTable(name = "blog_tag" , joinColumns = @JoinColumn(name = "blog_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "tag_id", referencedColumnName = "tag_id"))
    private Set<Tag> tags = new HashSet<>();

    private String description;

    private String imageName;

}
