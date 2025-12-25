package com.authentication.controller;

import com.authentication.service.UserService;
import com.infrastructure.basedata.CodeException;
import com.infrastructure.form.OutputAPIForm;
import com.infrastructure.service.dto.BaseUserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@Slf4j
public class ManipulateUserController {
    private final UserService userService;

    public ManipulateUserController(UserService userService) {
        this.userService = userService;
    }
    @PostMapping("")
    public ResponseEntity<OutputAPIForm> saveUser(@RequestBody BaseUserDto user){
        OutputAPIForm retVal = new OutputAPIForm();
        try{
           retVal = userService.saveUser(user);
        }catch (Exception e){
            log.error("Error in save user",e);
            retVal.setSuccess(false);
            retVal.getErrors().add(CodeException.SYSTEM_EXCEPTION);
        }
        return ResponseEntity.ok().body(retVal);
    }
}
