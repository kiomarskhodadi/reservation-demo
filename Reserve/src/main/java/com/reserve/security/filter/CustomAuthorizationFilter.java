package com.reserve.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.form.OutputAPIForm;
import com.infrastructure.security.UserSecurity;
import com.infrastructure.service.dto.UserDto;
import com.infrastructure.utility.InfraSecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    @Value("${application.jwt.secret-key}")
    public String secretKey;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token ="";
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                token = authorizationHeader.substring("Bearer ".length());
            }
            if(StringUtils.hasLength(token)){
                OutputAPIForm curUser = new OutputAPIForm();
                try{
                    curUser = InfraSecurityUtils.validateToken(token,secretKey);
                    if(curUser.isSuccess() ){
                        UserSecurity userSecurity = InfraSecurityUtils.extractUserFromToken(token);
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userSecurity,null,userSecurity.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    }
                }catch (JWTVerificationException e){
                    log.error("Error Log in is {}", e.getMessage());
                    curUser.setSuccess(false);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(),curUser);
                }catch (Exception e){
                    log.error("Error Log in is {}", e.getMessage());
                    curUser.setSuccess(false);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(),curUser);
                }
            }
            filterChain.doFilter(request,response);

    }

}
