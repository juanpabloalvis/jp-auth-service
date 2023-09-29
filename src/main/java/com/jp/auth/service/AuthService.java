package com.jp.auth.service;

import com.jp.auth.config.JwtProvider;
import com.jp.auth.mapper.UserMapper;
import com.jp.auth.model.dto.TokenDto;
import com.jp.auth.model.dto.UserDto;
import com.jp.auth.model.entity.UserEntity;
import com.jp.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.security.NoSuchAlgorithmException;
import java.util.Optional;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    JwtProvider jwtProvider;

    public UserDto save(UserDto userDto) {
        Optional<UserEntity> userEntity = userRepository.findByUsername(userDto.getUsername());
        if (userEntity.isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, String.format("User %s already exists", userDto.getUsername()));
        }
        UserEntity userSaved = userRepository.save(UserEntity.builder()
                .username(userDto.getUsername())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .build()
        );

        return UserMapper.INSTANCE.toDto(userSaved);
    }

    public TokenDto login(UserDto userDto) {
        UserEntity userEntity = userRepository.findByUsername(userDto.getUsername())
                .orElseThrow(() -> getResponseStatusException(userDto));

        if (passwordEncoder.matches(userDto.getPassword(), userEntity.getPassword())) {
            return new TokenDto(jwtProvider.createToken(userEntity));
        }

        throw getResponseStatusException(userDto);

    }


    public TokenDto validate(String token) {
        jwtProvider.validate(token);
        String usernameFromToken = jwtProvider.getUsernameFromToken(token);
        userRepository.findByUsername(usernameFromToken)
                .orElseThrow(() -> new ResponseStatusException(UNAUTHORIZED, String.format("%s no autorizado", usernameFromToken)));
        return new TokenDto(token);
    }

    private static ResponseStatusException getResponseStatusException(UserDto userDto) {
        return new ResponseStatusException(UNAUTHORIZED, String.format("%s no autorizado", userDto.getUsername()));
    }

}
