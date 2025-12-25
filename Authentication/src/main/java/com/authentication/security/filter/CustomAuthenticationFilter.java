package com.authentication.security.filter;

import com.authentication.security.VariableSecurity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.basedata.CodeException;
import com.infrastructure.form.OutputAPIForm;
import com.infrastructure.security.UserSecurity;
import com.infrastructure.utility.InfraSecurityUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;

@Slf4j
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final VariableSecurity variableSecurity;

    public CustomAuthenticationFilter(AuthenticationManager authenticationManager, VariableSecurity variableSecurity) {
        this.authenticationManager = authenticationManager;
        this.variableSecurity = variableSecurity;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        Authentication retVal ;
        try{
            String base64Credentials = request.getHeader("Authorization").substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            final String[] values = credentials.split(":", 2);
            String username = values[0];
            String password = values[1];
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,password);
            retVal = authenticationManager.authenticate(authenticationToken);
        }catch (Exception e){
            generateResponse(response, CodeException.BAD_USER_PASS,true);
            retVal = null;
        }
        return retVal;
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {
        ObjectMapper objectMapper  = new ObjectMapper();
        try{
            UserSecurity user = (UserSecurity)authentication.getPrincipal();
            Map<String,String> tokens = InfraSecurityUtils.createToken(user,
                    request.getRequestURI(),
                    variableSecurity.getExpireToken(),
                    variableSecurity.getExpireRefreshToken(),
                    variableSecurity.getSecretKey());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            objectMapper.writeValue(response.getOutputStream(),tokens);
        }catch (Exception e){
            objectMapper.writeValue(response.getOutputStream(), "Problem in Create Token");
        }


    }

    public static void generateResponse(HttpServletResponse response, CodeException codeException, boolean returnHttpStatus){
        OutputAPIForm<Object> retval = new OutputAPIForm<>();
        try {
            if(codeException != null) {
                retval.setSuccess(false);
                retval.getErrors().add(codeException);
            }
            if (returnHttpStatus){
                response.setStatus(InfraSecurityUtils.getHttpCode(retval.getErrors()));
            }
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(response.getOutputStream(), retval);
        } catch (IOException e) {
            log.error("Error writeValue in ObjectMapper! caused by : " + e.getMessage());
        }
    }
}
