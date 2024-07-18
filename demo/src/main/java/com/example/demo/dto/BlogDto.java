package com.example.demo.dto;

import com.example.demo.model.Category;
import com.example.demo.model.Tag;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
public class BlogDto {

    private Long id;

    private String name;

    private int categoryId;

//    private int tagId;

    private String description;

    private String imageName;

    private Set<Integer> tagId;



}
