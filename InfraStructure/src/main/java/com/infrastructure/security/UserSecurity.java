package com.infrastructure.security;


import com.infrastructure.service.dto.UserDto;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

public class UserSecurity extends User  {

    public UserSecurity(String username, String password, Collection<? extends GrantedAuthority> authorities, UserDto curUser) {
        super(username, password, authorities);
        this.curUser = curUser;
    }

    private UserDto curUser;

    public UserSecurity(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public UserSecurity(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
    }
    public UserSecurity(String username, String password, boolean enabled, boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, Collection<? extends GrantedAuthority> authorities, UserDto envUser) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.setCurUser(envUser);
    }

    public UserDto getCurUser() {
        return curUser;
    }

    public void setCurUser(UserDto envUser) {
        this.curUser = envUser;
    }
}
