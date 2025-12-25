package com.infrastructure.service.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor
@AllArgsConstructor
@Slf4j
@Setter
@Getter
public class UserDto extends BaseUserDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long userId;

    public UserDto(Long userId, String mail, String userName) {
        super("", "", mail, userName,"");
        this.userId = userId;
    }
}
