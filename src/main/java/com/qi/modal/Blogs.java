package com.qi.modal;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(name = "MH_BLOGS")
@Data
public class Blogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String ytUrl;
    private String title;
    private String description;
    private Instant instant=Instant.now();
}
