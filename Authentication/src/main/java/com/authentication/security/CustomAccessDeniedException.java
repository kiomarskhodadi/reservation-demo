package com.authentication.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.basedata.CodeException;
import com.infrastructure.form.OutputAPIForm;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedException implements AccessDeniedHandler {



    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)  {
        OutputAPIForm  dto = new OutputAPIForm();
        dto.getErrors().add(CodeException.ACCESS_DENIED);
        dto.setSuccess(false);
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        try {
            new ObjectMapper().writeValue(response.getOutputStream(), dto);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
