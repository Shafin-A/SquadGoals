package com.github.shafina.squadgoals.dto;

import com.github.shafina.squadgoals.model.User;

public record UserDTO(Long id, String name, String email, String timezone) {
    public static UserDTO from(User user) {
        return new UserDTO(user.getId(), user.getName(), user.getEmail(), user.getTimezone());
    }
}