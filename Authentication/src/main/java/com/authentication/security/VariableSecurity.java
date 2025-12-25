package com.authentication.security;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Creator 2/22/2025
 * @Project IntelliJ IDEA
 * @Author k.khodadi
 **/


@Getter
@Component
public class VariableSecurity {
    @Value("${application.jwt.expire-time}")
    public  Long expireToken;
    @Value("${application.jwt.expire-refresh-token}")
    public  Long expireRefreshToken;
    @Value("${application.jwt.secret-key}")
    public String secretKey;

}
