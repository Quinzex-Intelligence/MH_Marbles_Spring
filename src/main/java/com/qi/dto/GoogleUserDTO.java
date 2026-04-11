package com.qi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoogleUserDTO {

    private String email;
    private String name;
    private String picture;
}
