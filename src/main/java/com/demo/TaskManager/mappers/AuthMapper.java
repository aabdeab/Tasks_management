package com.demo.TaskManager.mappers;

import com.demo.TaskManager.dtos.LoginRequest;
import com.demo.TaskManager.dtos.RegisterRequest;
import com.demo.TaskManager.entities.User;

public class AuthMapper {

    private AuthMapper() {
        throw new UnsupportedOperationException("This class should never be instantiated");
    }

    /**
     * Convert RegisterRequest DTO to User entity
     * @param dto RegisterRequest containing name, email, password
     * @return User entity (password needs to be encoded separately)
     */
    public static User fromDTO(RegisterRequest dto) {
        return User.builder()
                .name(dto.name())
                .email(dto.email())
                .password(dto.password())
                .build();
    }

    /**
     * Convert RegisterRequest to LoginRequest
     * Used after registration to automatically login the user
     * @param dto RegisterRequest containing email and password
     * @return LoginRequest with email and password
     */
    public static LoginRequest fromDto(RegisterRequest dto) {
        return new LoginRequest(dto.email(), dto.password());
    }
}

