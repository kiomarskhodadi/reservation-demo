package com.authentication.service;

import com.authentication.dao.entity.User;
import com.authentication.dao.repo.IUserRepo;
import com.infrastructure.form.OutputAPIForm;
import com.infrastructure.security.UserSecurity;
import com.infrastructure.service.dto.BaseUserDto;
import com.infrastructure.service.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {

    private final IUserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

    public UserService(IUserRepo userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetails retVal;
        Optional<User> user = userRepo.findByUsername(username);
        if(user.isPresent()){
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            UserDto userDto = new UserDto(user.get().getId(),user.get().getEmail(), user.get().getUsername());
            retVal = new UserSecurity(user.get().getUsername(), user.get().getPassword(), true,true,true,true,authorities,userDto);
        }else{
            log.error("The User do not find in database");
            throw new UsernameNotFoundException("The User do not find in database");
        }
        return retVal;
    }
    public UserDetailsService userDetailsService() {
        return username -> {
            UserDetails retVal;
            Optional<User> user = userRepo.findByUsername(username);
            if(user.isPresent()){
                Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                UserDto userDto = new UserDto(user.get().getId(),user.get().getEmail(), user.get().getUsername());
                retVal = new UserSecurity(user.get().getUsername(), user.get().getPassword(), true,true,true,true,authorities,userDto);
            }else{
                log.error("The User do not find in database");
                throw new UsernameNotFoundException("The User do not find in database");
            }
            return retVal;
        };
    }
    public OutputAPIForm saveUser(BaseUserDto data){
        OutputAPIForm retVal = new OutputAPIForm();
        User user = new User(data,passwordEncoder);
        userRepo.saveAndFlush(user);
        retVal.setData(new UserDto(user.getId(),user.getEmail(),user.getUsername()));
        return retVal;
    }
}
