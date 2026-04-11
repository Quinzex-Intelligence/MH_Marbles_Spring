package com.qi.modal;


import jakarta.persistence.*;
import lombok.Data;

@Table(name = "users")
@Data
@Entity
public class TilesLogin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;


    private String picture;

    private String role;

    @Column(length = 500)
    private String refreshToken;
}
