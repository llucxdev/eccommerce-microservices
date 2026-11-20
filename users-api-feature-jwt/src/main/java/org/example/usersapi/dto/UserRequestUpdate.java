package org.example.usersapi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public class UserRequestUpdate {
    @Size(min = 1, message = "name must be longer than 1 character")
    private String name;
    @Size(min = 1, message = "name must be valid")
    @Email(message = "email must be valid")
    private String email;

    public UserRequestUpdate() {
    }

    public UserRequestUpdate(String name, String email) {
        this.name = name;
        this.email = email;
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserRequestUpdate that = (UserRequestUpdate) o;
        return Objects.equals(name, that.name) && Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, email);
    }
}
