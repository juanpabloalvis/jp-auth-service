package com.jp.auth.controller;

import com.jp.auth.model.dto.TokenDto;
import com.jp.auth.model.dto.UserDto;
import com.jp.auth.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<TokenDto> login(@RequestBody UserDto userDto) {
        TokenDto login = authService.login(userDto);
        return ResponseEntity.ok(login);
    }

    @PostMapping("/validate")
    public ResponseEntity<TokenDto> validate(@RequestParam String token) {
        TokenDto login = authService.validate(token);
        return ResponseEntity.ok(login);
    }

    @PostMapping("/create")
    public ResponseEntity<UserDto> create(@RequestBody UserDto userDtoRequest) {
        UserDto userDto = authService.save(userDtoRequest);
        return ResponseEntity.ok(userDto);
    }
}
