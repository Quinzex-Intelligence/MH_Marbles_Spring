package com.qi.service;

import com.qi.dto.CreateBlogRequest;
import com.qi.modal.Blogs;
import com.qi.repository.BlogsRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BlogsService {

    private final BlogsRepo blogsRepo;
    @Transactional
   public String createBlog(List<CreateBlogRequest> blogRequest){
        if (blogRequest == null || blogRequest.isEmpty()) {
            throw new IllegalArgumentException("Blog list cannot be empty");
        }
        long existingCount = blogsRepo.count();
        if (existingCount + blogRequest.size() > 5) {
            throw new RuntimeException("Owner can create maximum 5 blogs only");
        }
       List<Blogs> blogs = blogRequest.stream().map(req->{
        Blogs blog = new Blogs();
        blog.setYtUrl(req.getYtUrl());
        blog.setTitle(req.getTitle());
        blog.setDescription(req.getDescription());
        return blog;
       }).toList();

       blogsRepo.saveAll(blogs);

        return blogs.size() + " blogs created successfully";
   }

   @Transactional
    public String deleteBlog(List<Long> ids){
       if (ids == null || ids.isEmpty()) {
           throw new RuntimeException("IDs cannot be empty");
       }
        blogsRepo.deleteAllById(ids);

        return "Deleted successfully";
   }

   public List<Blogs> getAllBlogs(){
        return blogsRepo.findAll();
   }
}
