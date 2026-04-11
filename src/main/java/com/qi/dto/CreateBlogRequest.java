package com.qi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateBlogRequest {
    private String ytUrl;
    private String title;
    private String description;
}
