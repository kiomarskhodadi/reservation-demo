package com.authentication.security.filter;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.authentication.security.VariableSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.form.OutputAPIForm;
import com.infrastructure.security.UserSecurity;
import com.infrastructure.utility.InfraSecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {


    private final VariableSecurity variableSecurity;

    public CustomAuthorizationFilter( VariableSecurity variableSecurity) {
        this.variableSecurity = variableSecurity;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        if(request.getServletPath().equals("/api/login")){
            filterChain.doFilter(request,response);
        }else{
            String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            String token ="";
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
                token = authorizationHeader.substring("Bearer ".length());
            }
            if(StringUtils.hasLength(token)){
                OutputAPIForm curUser = new OutputAPIForm();
                try{
                    curUser = InfraSecurityUtils.validateToken(token,variableSecurity.getSecretKey());
                    if(curUser.isSuccess() ){
                        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(curUser.getData(),null,((UserSecurity)curUser.getData()).getAuthorities());
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

}
