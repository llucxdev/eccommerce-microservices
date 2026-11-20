package org.example.usersapi.dto;

import jakarta.validation.constraints.*;

import java.util.Objects;

public class UserRequest {
    @NotBlank(message = "name cannot be blank")
    private String name;
    @NotBlank(message = "name cannot be blank")
    @Email(message = "email should be valid")
    private String email;
    @NotBlank(message = "name cannot be blank")
    @Size(min = 6, message = "password must be longer than 6 characters")
    private String password;

    public UserRequest() {
    }

    public UserRequest(String name, String email, String password) {
        this.name = name;
        this.email = email;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
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
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserRequest that = (UserRequest) o;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email) && Objects.equals(password, that.password);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email, password);
    }
}
