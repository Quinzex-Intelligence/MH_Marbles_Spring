package com.qi.controller;


import com.qi.dto.ContactRequest;
import com.qi.dto.OwnerInfo;
import com.qi.service.ContactService;
import com.qi.service.UsersService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UsersService usersService;
    private final ContactService contactService;


    @PostMapping("/google")
    public ResponseEntity<String> googleLogin(@RequestBody Map<String,String> body, HttpServletResponse response){
       String token = body.get("token");
        String msg = usersService.registerOrLogin(token,response);
       return new ResponseEntity<>(msg, HttpStatus.OK);
    }
    @PostMapping("/refresh")
    public ResponseEntity<String> refresh(@CookieValue(name = "refresh_token",required = false)String refreshToken,HttpServletResponse response){
        String msg = usersService.refreshToken(refreshToken,response);
        return new ResponseEntity<>(msg, HttpStatus.OK);

    }
    @PostMapping("/logout")
    public ResponseEntity<String> logout(HttpServletRequest request,
                                         HttpServletResponse response) {

        String msg = usersService.logout(request, response);
        return ResponseEntity.ok(msg);
    }

    @PostMapping("/contact/us")
    public ResponseEntity<?> sendContactus(@RequestBody ContactRequest request){
        contactService.sendContactUsMail(request);
        return ResponseEntity.ok(
                Map.of("message", "Message sent successfully")
        );
    }


}
