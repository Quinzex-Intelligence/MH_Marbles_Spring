package com.qi.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class OwnerInfo {
    private String name;

    private String email;


    private String picture;

    private String role;
}
