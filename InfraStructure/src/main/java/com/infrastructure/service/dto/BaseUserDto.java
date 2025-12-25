package com.infrastructure.service.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Builder
@Setter
@Getter
public class BaseUserDto {

    private String firstName;
    private String lastName;
    private String mail;
    private String userName;
    private String password;

    public BaseUserDto(String firstName, String lastName, String mail, String userName) {
        this( firstName,  lastName,  mail,  userName, "");
    }

    public BaseUserDto(UserDto dto){
        this( dto.getFirstName(),  dto.getLastName(),  dto.getMail(),  dto.getUserName(), "");
    }

}
