package com.jp.auth.model.dto;

import lombok.Data;

@Data
public class UserDto {
    private int id;
    private String username;
    private String password;
}
