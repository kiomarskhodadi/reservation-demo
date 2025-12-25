package com.authentication.dao.entity;

import com.infrastructure.service.dto.BaseUserDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.sql.Timestamp;

@Entity
@Table(name = "USERS",schema = "USER_MNG")
@Cacheable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "SEQ_USER", strategy = GenerationType.SEQUENCE)
    @SequenceGenerator(name = "SEQ_USER", allocationSize = 1, sequenceName = "SEQ_USER",schema = "USER_MNG")
    private Long id;
    @Column(name = "USERNAME")
    private String username;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "PASSWORD")
    private String password;
    @Column(name = "CREATION_AT")
    private Timestamp creationAt;

    public User(BaseUserDto dto, PasswordEncoder passwordEncoder){
        this(null,
                dto.getUserName(),
                dto.getMail(),
                passwordEncoder.encode(dto.getPassword()),
                new Timestamp(System.currentTimeMillis())
                );
    }

}
