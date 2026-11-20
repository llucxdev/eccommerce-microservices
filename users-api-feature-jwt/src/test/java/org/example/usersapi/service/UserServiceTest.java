package org.example.usersapi.service;

import org.example.usersapi.dto.UserRequest;
import org.example.usersapi.dto.UserRequestUpdate;
import org.example.usersapi.dto.UserResponse;
import org.example.usersapi.exception.EmailExistsException;
import org.example.usersapi.exception.NotFoundException;
import org.example.usersapi.exception.UnauthorizecAccessException;
import org.example.usersapi.model.User;
import org.example.usersapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponse userResponse;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Alberto", "alberto@example.com", "alberto123");
        userResponse = new UserResponse(1L, "Alberto", "alberto@example.com", false);
    }

    @Test
    void getUserById_idInDatabase_returnUserResponse() {
        // Given
        UserResponse expected = userResponse;

        // When
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        UserResponse actual = userService.getUserById(user.getId());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void getUserById_idNotInDatabase_returnNotFound() {
        // Given

        // When
        Mockito.when(userRepository.findById(user.getId())).thenThrow(NotFoundException.class);

        // Then
        assertThrows(NotFoundException.class, () -> userService.getUserById(user.getId()));
    }

    @Test
    void getAllUsers_request_returnListOfUserResponse() {
        // Given
        List<UserResponse> expected = List.of(userResponse);

        // When
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));
        List<UserResponse> actual = userService.getAllUsers();

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void createUser_validUserRequest_returnCreatedUserResponse() {
        // Given
        UserResponse expected = userResponse;

        // When
        Mockito.when(authService.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findByEmail(expected.getEmail())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse actual = userService.createUser(new UserRequest("Alberto", "alberto@example.com", "alberto123"));

        // Then
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
    }

    @Test
    void createUser_emailExists_throwsEmailExistsException() {
        // Given
        UserResponse expected = userResponse;

        // When
        Mockito.when(authService.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findByEmail(expected.getEmail())).thenReturn(Optional.ofNullable(user));

        // Then
        assertThrows(EmailExistsException.class, () -> userService.createUser(new UserRequest("Alberto", "alberto@example.com", null)));
    }

    @Test
    void createUser_notAdmin_throwsUnauthorizecAccessException() {
        // Given

        // When
        Mockito.when(authService.isAdmin()).thenReturn(false);

        // Then
        assertThrows(UnauthorizecAccessException.class, () -> userService.createUser(new UserRequest("Alberto", "alberto", null)));
    }

    @Test
    void updateUser_validUserRequest_returnUpdatedUserResponse() {
        // Given
        UserResponse expected = new UserResponse(1L, "Pepe", "pepe@example.com", false);

        // When
        Mockito.when(authService.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse acutal = userService.updateUser(user.getId(), new UserRequestUpdate("Pepe", "pepe@example.com"));

        // Then
        assertEquals(expected.getId(), acutal.getId());
        assertEquals(expected.getName(), acutal.getName());
        assertEquals(expected.getEmail(), acutal.getEmail());
    }

    @Test
    void updateUser_nullUserRequest_returnNotUpdatedUserResponse() {
        // Given
        UserResponse expected = userResponse;

        // When
        Mockito.when(authService.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse acutal = userService.updateUser(user.getId(), new UserRequestUpdate());

        // Then
        assertEquals(expected.getId(), acutal.getId());
        assertEquals(expected.getName(), acutal.getName());
        assertEquals(expected.getEmail(), acutal.getEmail());
    }

    @Test
    void updateUser_idNotInDatabase_returnNotFound() {
        // Given

        // When
        Mockito.when(authService.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findById(user.getId())).thenThrow(NotFoundException.class);

        // Then
        assertThrows(NotFoundException.class, () -> userService.updateUser(user.getId(), new UserRequestUpdate()));
    }

    @Test
    void updateUser_emailExists_throwsEmailExistsException() {
        // Given
        User existingUser = new User("Otro", "existing@example.com", "pass123");

        User userToUpdate = new User("Alberto", "alberto@example.com", "pass123");
        userToUpdate.setId(1L);

        UserRequestUpdate requestUpdate = new UserRequestUpdate("Alberto", "existing@example.com");

        // When
        Mockito.when(authService.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findById(1L)).thenReturn(Optional.of(userToUpdate));
        Mockito.when(userRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingUser));

        // Then
        assertThrows(EmailExistsException.class, () -> userService.updateUser(1L, requestUpdate));
    }

    @Test
    void updateUser_notAdminAndNotSameUser_throwsUnauthorizecAccessException() {
        // Given

        // When
        Mockito.when(authService.isAdmin()).thenReturn(false);
        Mockito.when(authService.isSameUser(user.getId())).thenReturn(false);

        // Then
        assertThrows(UnauthorizecAccessException.class, () -> userService.updateUser(user.getId(), new UserRequestUpdate()));
    }


    @Test
    void setAdmin_validUser_returnUserResponse() {
        // Given
        UserResponse expected = new UserResponse(1L, "Alberto", "alberto@example.com", true);

        // When
        Mockito.when(authService.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse actual = userService.setAdmin(user.getId());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void setAdmin_notAdmin_throwsUnauthorizecAccessException() {
        // Given

        // When
        Mockito.when(authService.isAdmin()).thenReturn(false);

        // Then
        assertThrows(UnauthorizecAccessException.class, () -> userService.setAdmin(user.getId()));
    }

    @Test
    void deleteUser_idInDatabase_returnUserResponse() {
        // Given
        Long expected = user.getId();

        // When
        Mockito.when(authService.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.doNothing().when(userRepository).deleteById(user.getId());
        Long actual = userService.deleteUser(user.getId());

        // Then
        assertEquals(expected, actual);
    }

    @Test
    void deleteUser_idNotInDatabase_returnUserResponse() {
        // Given

        // When
        Mockito.when(authService.isAdmin()).thenReturn(true);
        Mockito.when(userRepository.findById(user.getId())).thenThrow(NotFoundException.class);

        // Then
        assertThrows(NotFoundException.class, () -> userService.deleteUser(user.getId()));
    }

    @Test
    void deleteUser_deleteOwnUser_returnUserResponse() {
        // Given
        Long expected = user.getId();

        // When
        Mockito.when(authService.isAdmin()).thenReturn(false);
        Mockito.when(authService.isSameUser(user.getId())).thenReturn(true);
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.doNothing().when(userRepository).deleteById(user.getId());
        Long actual = userService.deleteUser(user.getId());

        // Then
        assertEquals(expected, actual);
    }
}