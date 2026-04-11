package com.qi.service;
import com.qi.config.GoogleTokenVerifier;
import com.qi.dto.GoogleUserDTO;
import com.qi.dto.OwnerInfo;
import com.qi.modal.TilesLogin;
import com.qi.repository.UsersRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Arrays;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepo usersRepo;
    private final JwtService jwtService;
    private final GoogleTokenVerifier googleTokenVerifier;

    public String registerOrLogin(String googleToken, HttpServletResponse response) {

        GoogleUserDTO googleUser = googleTokenVerifier.verify(googleToken);
        String email = googleUser.getEmail();
        String name = googleUser.getName();
        String picture = googleUser.getPicture();
        TilesLogin user;
        long count = usersRepo.count();
        if (count == 0) {
            user = new TilesLogin();
            user.setEmail(email);
            user.setName(name);
            user.setPicture(picture);
            user.setRole("OWNER");
        } else {
            user = usersRepo.findAll().stream().findFirst().orElseThrow();
            if (!user.getEmail().equals(email)) {
                throw new RuntimeException("Access Denied: Only owner can login");
            }
        }
        String accessToken = jwtService.generateAccessToken(email,user.getRole());
        String refreshToken = jwtService.generateRefreshToken(email);
        user.setRefreshToken(refreshToken);
        usersRepo.save(user);
        addCookie(response, "access_token", accessToken, 15 * 60);
        addCookie(response, "refresh_token", refreshToken, 24 * 60 * 60);

        return "login successful";
    }

    public String refreshToken(String refreshToken, HttpServletResponse response) {
        if (refreshToken == null) {
            throw new RuntimeException("Refresh token missing");
        }
        String email = jwtService.extractEmail(refreshToken);
        TilesLogin user = usersRepo.findByEmail(email).orElseThrow(() -> new RuntimeException("user not found"));
        if (!jwtService.validateRefreshToken(refreshToken, email)) {
            throw new RuntimeException("Invalid refresh token");
        }
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new RuntimeException("Refresh token mismatch");
        }
        String newAccessToken = jwtService.generateAccessToken(email,user.getRole());
        addCookie(response, "access_token", newAccessToken, 15 * 60);
        return "Token refreshed";
    }

    public String logout(HttpServletRequest request, HttpServletResponse response) {

        if (request.getCookies() != null) {
            String token = Arrays.stream(request.getCookies()).filter(c -> "access_token".equals(c.getName())).map(Cookie::getValue).findFirst().orElse(null);
            if (token != null) {
                try {
                    String email = jwtService.extractEmail(token);
                    usersRepo.findByEmail(email).ifPresent(user -> {
                        user.setRefreshToken(null);
                        usersRepo.save(user);
                    });

                } catch (Exception ignored) {

                }
            }
        }

        clearCookie(response, "access_token");
        clearCookie(response, "refresh_token");
        return "logged out successfully";
    }

    public OwnerInfo getOwnerInfo(Authentication auth){
        String email = auth.getName();
        TilesLogin user = usersRepo.findByEmail(email).orElseThrow(()->new RuntimeException("user not found"));
        return OwnerInfo.builder().name(user.getName()).picture(user.getPicture()).email(user.getEmail()).role(user.getRole()).build();
    }
    private void addCookie(HttpServletResponse response,String name  , String value,int maxAge){
        ResponseCookie cookie = ResponseCookie.from(name,value)
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(maxAge)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    }
    private void clearCookie(HttpServletResponse response,String name){
        ResponseCookie cookie = ResponseCookie.from(name,"")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}
