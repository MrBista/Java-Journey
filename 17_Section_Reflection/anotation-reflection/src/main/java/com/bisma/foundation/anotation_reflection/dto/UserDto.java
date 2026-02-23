package com.bisma.foundation.anotation_reflection.dto;

import com.bisma.foundation.anotation_reflection.anotation.NotBlank;
import com.bisma.foundation.anotation_reflection.anotation.Required;

public class UserDto {
    @Required
    @NotBlank(false)
    private String username;

    @Required
    @NotBlank
    private String email;

    @Required
    @NotBlank
    private String password;

    public UserDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public UserDto() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmmil() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "UserDto{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
