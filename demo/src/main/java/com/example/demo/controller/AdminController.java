package com.example.demo.controller;

import com.example.demo.dto.BlogDto;
import com.example.demo.dto.Response;
import com.example.demo.model.Blog;
import com.example.demo.model.Category;
import com.example.demo.model.Tag;
import com.example.demo.projection.BlogProjection;
import com.example.demo.service.BlogService;
import com.example.demo.service.CategoryService;
import com.example.demo.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AdminController {

    public static String uploadDir = System.getProperty("user.dir") + "/demo/src/main/resources/static/productImages/";

    private final CategoryService categoryService;

    private final BlogService blogService;

    private final TagService tagService;




    @GetMapping("/admin")
    public String adminHome() {
        return "adminHome";
    }


    @GetMapping("/admin/categories")
    public String getCat(Model model) {
        model.addAttribute("categories", categoryService.getAllCategory());
        return "categories";
    }

    @GetMapping("/admin/categories/add")
    public String getCatAdd(Model model) {
        model.addAttribute("category", new Category());
        return "categoriesAdd";
    }

    @PostMapping("/admin/find-all-categories")
    public ResponseEntity<Response> getAllCategories(){
        var success = false;
        String message = null;
        List<Category> categories = null;
        try {
            categories = categoryService.getAllCategory();
            if (categories != null && !categories.isEmpty()){
                success=true;
            }
        }catch (Exception e){
            message = "Category not found";
        }
        return new ResponseEntity<>(new Response(categories,message,success),HttpStatus.OK);

    }

    @PostMapping("/admin/categories/add")
    public String postCatAdd(@ModelAttribute("category") Category category) {
        categoryService.addCategory(category);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/delete/{id}")
    public String deleteCat(@PathVariable int id) {
        categoryService.remoteCategoryById(id);
        return "redirect:/admin/categories";
    }

    @GetMapping("/admin/categories/update/{id}")
    public String updateCat(@PathVariable int id, Model model) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            return "categoriesAdd";
        } else
            return "404";
    }


    //BLOG Section

    @GetMapping("/admin/blogs")
    public String blogs(Model model) {
        var blogs = blogService.getAllBlog();
        model.addAttribute("blogs", blogs);
        return "blogs";
    }

    @PostMapping("/admin/find-all")
    public ResponseEntity<Response> getAll() {

        var success = false;
        String message = null;
        List<BlogProjection> blogs = null;
        try {
            blogs = blogService.getAllBlog();

            if (blogs != null && !blogs.isEmpty())
                success = true;

        } catch (Exception e) {
            message = "Something went wrong";
        }

        return new ResponseEntity<>(new Response(blogs, message, success), HttpStatus.OK);
    }


    @GetMapping("/admin/blogs/add")
    public String blogAddGet(Model model) {
        model.addAttribute("blogDto", new BlogDto());
        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("tags", tagService.getAllTag());
        return "blogsAdd";
    }

    @PostMapping("/admin/blogs/add")
    public String blogAddPost(@ModelAttribute("blogDto") BlogDto blogDto,
                              @RequestParam(value = "productImage", required = false) MultipartFile file,
                              @RequestParam("imgName") String imgName) throws IOException {

        Blog blog = new Blog();
        blog.setId(blogDto.getId());
        blog.setName(blogDto.getName());
        blog.setCategory(categoryService.getCategoryById(blogDto.getCategoryId()).get());
        blog.setDescription(blogDto.getDescription());

        //TODO: convert to lambda
        if (blogDto.getTagId() != null && !blogDto.getTagId().isEmpty()) {
            var tags = new HashSet<Tag>();
            for (var tagId : blogDto.getTagId()) {
                var tag = tagService.getTagById(tagId);
                tag.ifPresent(tags::add);
            }
            blog.setTags(tags);
        }

//        Optional.ofNullable(blogDto.getTagId())
//                .filter(tagIds -> !tagIds.isEmpty())
//                .ifPresent(tagIds -> {
//                    Set<Tag> tags = tagIds.stream()
//                                          .map(tagService::getTagById)
//                                          .flatMap(Optional::stream)
//                                          .collect(Collectors.toSet());
//                    blog.setTags(tags);
//                });


        String imageUUID;

        if (file != null && !file.isEmpty()) {
            imageUUID = file.getOriginalFilename();
            Path fileNameAndPath = Paths.get(uploadDir, imageUUID);
            Files.write(fileNameAndPath, file.getBytes());
        } else {
            imageUUID = imgName;
        }

        blog.setImageName(imageUUID);
        blogService.addBlog(blog);

        return "redirect:/admin/blogs";
    }

    @GetMapping("/admin/blog/delete/{id}")
    public String deleteBlog(@PathVariable Long id) {
        Optional<Blog> blogOptional = this.blogService.getBlogById(id);
        if (blogOptional.isPresent()) {
            Blog blog = blogOptional.get();
            String imageName = blog.getImageName();

            this.blogService.removeBlogById(id);

            if (imageName != null && !imageName.isEmpty()) {
                try {
                    Path imagePath = Paths.get(uploadDir, imageName);
                    Files.delete(imagePath);
                } catch (IOException e) {
                    //exception
                }
            }
        }
        return "redirect:/admin/blogs";
    }

    @GetMapping("/admin/blog/update/{id}")
    public String updateBlogGet(@PathVariable long id, Model model) {
        Blog blog = blogService.getBlogById(id).orElseThrow(() -> new IllegalArgumentException("Invalid blog Id:" + id));
        BlogDto blogDto = new BlogDto();
        blogDto.setId(blog.getId());
        blogDto.setName(blog.getName());
        blogDto.setCategoryId(blog.getCategory().getId());
        blogDto.setDescription(blog.getDescription());
        blogDto.setImageName(blog.getImageName());

        // Set the tags to blogDto
        Set<Integer> tagIds = blog.getTags().stream()
                .map(Tag::getId)
                .collect(Collectors.toSet());
        blogDto.setTagId(tagIds);

        model.addAttribute("categories", categoryService.getAllCategory());
        model.addAttribute("tags", tagService.getAllTag());
        model.addAttribute("blogDto", blogDto);
        return "blogsAdd";
    }


    //Tag Section

    @GetMapping("/admin/tags")
    public String getTag(Model model) {
        model.addAttribute("tags", tagService.getAllTag());
        return "tags";
    }

    @PostMapping("/admin/find-all-tags")
    public ResponseEntity<Response> getAllTags(){
        var success = false;
        String message = null;
        List<Tag> tags = null;
        try {
            tags = tagService.getAllTag();
            if (tags != null && !tags.isEmpty()){
                success=true;
            }
        }catch (Exception e){
            message = "Tags not found";
        }
        return new ResponseEntity<>(new Response(tags,message,success),HttpStatus.OK);

    }
    @GetMapping("/admin/tags/add")
    public String tagAddGet(Model model) {
        model.addAttribute("tag", new Tag());
        return "tagsAdd";
    }

    @PostMapping("/admin/tags/add")
    public String tagAddPost(@ModelAttribute("tag") Tag tag) {
        tagService.addTag(tag);
        return "redirect:/admin/tags";
    }

    @GetMapping("/admin/tags/delete/{id}")
    public String deleteTag(@PathVariable int id) {
        tagService.removeTagById(id);
        return "redirect:/admin/tags";
    }

    @GetMapping("/admin/tags/update/{id}")
    public String updateTag(@PathVariable int id, Model model) {
        Optional<Tag> tag = tagService.getTagById(id);
        if (tag.isPresent()) {
            model.addAttribute("tag", tag.get());
            return "tagsAdd";
        } else
            return "404";
    }
}



















