package com.example.demo.service;

import com.example.demo.model.Blog;
import com.example.demo.projection.BlogProjection;
import com.example.demo.repository.BlogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final BlogRepository blogRepository;

    public List<BlogProjection> getAllBlog(){
        return blogRepository.getAllBlogs();
    }

    public void addBlog(Blog blog){
        blogRepository.save(blog);
    }

    public void removeBlogById(Long id){
        blogRepository.deleteById(id);
    }

    public Optional<Blog> getBlogById(Long id){
        return blogRepository.findById(id);
    }

//    public List<Blog> getAllBlogByCategoryId(int id){
//        return blogRepository.finAllByCategory_Id(id);
//    }
}
