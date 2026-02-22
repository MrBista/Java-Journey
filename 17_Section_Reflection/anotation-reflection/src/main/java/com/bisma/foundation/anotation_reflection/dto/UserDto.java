package com.bisma.foundation.anotation_reflection.dto;

import com.bisma.foundation.anotation_reflection.anotation.Required;

public class UserDto {
    @Required
    private String username;

    @Required
    private String emamil;

    @Required
    private String password;

    public UserDto(String username, String emamil, String password) {
        this.username = username;
        this.emamil = emamil;
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

    public String getEmamil() {
        return emamil;
    }

    public void setEmamil(String emamil) {
        this.emamil = emamil;
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
                ", emamil='" + emamil + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
