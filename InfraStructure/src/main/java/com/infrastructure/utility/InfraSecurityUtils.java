package com.infrastructure.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.infrastructure.basedata.CodeException;
import com.infrastructure.form.OutputAPIForm;
import com.infrastructure.security.UserSecurity;
import com.infrastructure.service.dto.UserDto;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.*;

@Slf4j
@Component
public class InfraSecurityUtils {

    public static final String strongPassMatcher = ".*";
    public static Long DEFAULT_EXPIRE_TOKEN = 3 * 60 * 60 * 1000L;
    public static Long DEFAULT_EXPIRE_REFRESH_TOKEN = 6 * 60 * 60 * 1000L;
    public static String secret = "Application will unveil as soon as possible";
    public static final Long defaultUser = -1L;

    public static Long getCurrentUser(){
        Long retVal;
        try{
            retVal = ((UserSecurity) SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getCurUser().getUserId();
            LocaleContextHolder.getLocale();
        }catch (Exception e){
            log.info("Current user is anonymous");
            retVal = defaultUser;
        }
        return retVal;
    }

    public static UserSecurity getCurrentUserObj(){
        UserSecurity retVal;
        try{
            retVal = ((UserSecurity)SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        }catch (Exception e){
            log.info("Current user is anonymous");
            retVal = null;
        }
        return retVal;
    }

    public static boolean checkLogin(){
        boolean retVal = false;
        try{
            if(!getCurrentUser().equals(defaultUser)){
                retVal = true;
            }
        }catch (Exception e){
            log.error("Error in get current user",e);
            retVal = false;
        }
        return retVal;
    }

    public static Map<String,String> createToken(UserSecurity user,
                                                 String url,
                                                 Long expireTime,
                                                 Long expireRefreshTime,
                                                 String secretKey){
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> retVal = new HashMap<>();
        try {
        Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
        Timestamp expireToken = new Timestamp(System.currentTimeMillis() + (expireTime));
        Timestamp expireRefreshToken = new Timestamp(System.currentTimeMillis() + (expireRefreshTime));
        String access_token = null;
            access_token = JWT.create()
                    .withSubject(objectMapper.writeValueAsString(user.getCurUser()))
                    .withExpiresAt(expireToken)
                    .withIssuer(url)
                    .sign(algorithm);
        String refresh_token = JWT.create()
                .withSubject(objectMapper.writeValueAsString(user.getCurUser()))
                .withExpiresAt(expireRefreshToken)
                .withIssuer(url)
                .sign(algorithm);
        retVal.put("access_token",access_token);
        retVal.put("refresh_token",refresh_token);

        } catch (JsonProcessingException e) {
            retVal = new HashMap<>();
            log.error("Error in Create Token",e);
        }

        return retVal;
    }

    public static Map<String,String> createToken(UserSecurity user,String url){
        Map<String,String> retVal = createToken(user,url,DEFAULT_EXPIRE_TOKEN,DEFAULT_EXPIRE_REFRESH_TOKEN,secret);
        return retVal;
    }

    public static OutputAPIForm validateToken(String token, String secretKey){
        OutputAPIForm retVal = new OutputAPIForm();
        try{
            Algorithm algorithm = Algorithm.HMAC256(secretKey.getBytes());
            JWTVerifier verifier = JWT.require(algorithm).build();

            verifier.verify(token);
        }catch (Exception e){
            retVal.setSuccess(false);
        }
        return retVal;
    }

    public static UserSecurity extractUserFromToken(String token) {
        UserSecurity retVal ;
        try {
            String userJson  = JWT.decode(token).getClaims().get("sub").toString();
            UserDto dto = parseUserFromJson(userJson);
            retVal = new UserSecurity(dto.getUserName(), "",new ArrayList<>(),dto);
        } catch (Exception e) {
            throw new RuntimeException("Invalid token", e);
        }
        return retVal;
    }

    private static UserDto parseUserFromJson(String userJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            if (userJson.startsWith("\"") && userJson.endsWith("\"")) {
                userJson = userJson.substring(1, userJson.length() - 1);
            }
            userJson = userJson.replace("\\\"", "\"");
            UserDto retVal  = objectMapper.readValue(userJson, UserDto.class);
            return retVal;
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user JSON: " + e.getMessage(), e);
        }
    }

    public static int getHttpCode(List<? extends CodeException> codeException){
        int result = 200;
        try {
            if (codeException != null && !codeException.isEmpty()){
                ArrayList<Integer> temp = new ArrayList<>();
                for (CodeException c : codeException) {
                    String httpCode = String.valueOf(c.getCodeException()).substring(0, 3);
                    temp.add(HttpStatus.valueOf(Integer.parseInt(httpCode)).value());
                }
                result = temp.get(0);
                for (int i = 1 ; i < temp.size() ; i++) {
                    if (result < temp.get(i))
                        result = temp.get(i);
                }
            }
        } catch (Exception e){
            result = 400;
            log.error(codeException + ":" + new Date() + " => " + e);
        }
        return result;
    }
}
