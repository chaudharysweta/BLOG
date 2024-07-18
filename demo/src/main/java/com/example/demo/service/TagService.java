package com.example.demo.service;

import com.example.demo.model.Tag;
import com.example.demo.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TagService {

    private final TagRepository tagRepository;

    public List<Tag> getAllTag(){
        return tagRepository.findAll();
    }

    public void addTag(Tag tag){
        tagRepository.save(tag);
    }

    public void removeTagById(Integer id){
        tagRepository.deleteById(id);
    }

    public Optional<Tag> getTagById(int id){
        return tagRepository.findById(id);
    }
}
