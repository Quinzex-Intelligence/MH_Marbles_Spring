package com.qi.repository;

import com.qi.modal.Blogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogsRepo extends JpaRepository<Blogs,Long> {
}
