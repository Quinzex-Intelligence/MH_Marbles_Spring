package com.qi.filter;

import com.qi.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain) throws ServletException, IOException {
        String token =extractTokenFromCookies(request);
        if(token != null){
            try{
                String email = jwtService.extractEmail(token);
                String role =jwtService.extractRole(token);
                if(jwtService.validateAccessToken(token,email)){
                   if(SecurityContextHolder.getContext().getAuthentication()==null) {
                       UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email,null, List.of(new SimpleGrantedAuthority("ROLE_"+role)));
                       auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                       SecurityContextHolder.getContext().setAuthentication(auth);
                   }
                }

            } catch (Exception ignored) {

            }
        }
        filterChain.doFilter(request,response);
    }

    private String extractTokenFromCookies(HttpServletRequest request){
        if(request.getCookies()==null){
            return null;
        }
        return Arrays.stream(request.getCookies())
                .filter(c->"access_token".equals(c.getName()))
                .map(Cookie::getValue).findFirst().orElse(null);
    }
}
