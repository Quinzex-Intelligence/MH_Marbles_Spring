package com.qi.controller;

import com.qi.dto.OwnerInfo;
import com.qi.service.UsersService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/spring")
public class AdminController {
    private final UsersService usersService;
    @GetMapping("/owner/info")
    @PreAuthorize("hasRole('OWNER')")
    public ResponseEntity<OwnerInfo> getOwnerInfo(Authentication authentication){
        return new ResponseEntity<>(usersService.getOwnerInfo(authentication), HttpStatus.OK);
    }
}
