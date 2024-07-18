package com.example.demo.repository;

import com.example.demo.model.Blog;
import com.example.demo.projection.BlogProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Long> {

    @Query(value = """
            select b.id, b.name, b.description, b.image_name as image, c.name as category, group_concat(t.name) as tags
            from blog b
            left join category c on c.category_id = b.category_id
            left join blog_tag bt on bt.blog_id = b.id
            left join tag t on t.tag_id = bt.tag_id
            group by b.id
            order by b.id desc;
            """,
            nativeQuery = true
    )
    List<BlogProjection> getAllBlogs();

}
