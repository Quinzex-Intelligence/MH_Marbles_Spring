package com.qi.controller;

import com.qi.dto.BulkBlogReq;
import com.qi.dto.CreateBlogRequest;
import com.qi.service.BlogsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blogs")
@RequiredArgsConstructor
public class BlogsController {

    private final BlogsService blogsService;


    @PostMapping("/create")
    public ResponseEntity<String> createBlog(@RequestBody BulkBlogReq request){
        return ResponseEntity.ok(blogsService.createBlog(request.getBulkRequest()));
    }


    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteBlog(@RequestBody List<Long> ids){
        return ResponseEntity.ok(blogsService.deleteBlog(ids));
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAll(){
        return ResponseEntity.ok(blogsService.getAllBlogs());
    }
}