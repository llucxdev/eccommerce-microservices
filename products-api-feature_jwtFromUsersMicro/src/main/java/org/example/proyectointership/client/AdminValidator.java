package org.example.proyectointership.client;

import org.example.proyectointership.exception.UserIsNotAdminException;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class AdminValidator {

    private final UserClient userClient;

    public AdminValidator(UserClient userClient) {
        this.userClient = userClient;
    }

    public void validateAdmin(String token) {
        Map<String, Object> claims = userClient.getClaims(token);
        boolean isAdmin = Boolean.parseBoolean(claims.get("isAdmin").toString());
        if (!isAdmin) {
            String userId = claims.get("userId").toString();
            throw new UserIsNotAdminException(String.format("User with id %s is not admin", userId));
        }
    }
}
