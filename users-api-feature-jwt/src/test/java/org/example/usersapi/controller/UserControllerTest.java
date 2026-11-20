package org.example.usersapi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import org.example.usersapi.dto.UserRequest;
import org.example.usersapi.dto.UserRequestUpdate;
import org.example.usersapi.model.User;
import org.example.usersapi.repository.UserRepository;
import org.example.usersapi.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private UserRepository userRepository;

    private User user;
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() throws ServletException, IOException {
        user = new User(1L, "Alberto", "alberto@example.com", "alberto123");
        mapper = new ObjectMapper();
        Mockito.when(authService.isAdmin()).thenReturn(true);
    }

    @Test
    void getAllUsers_request_returnListOfUserResponse() throws Exception {
        // Given
        Mockito.when(userRepository.findAll()).thenReturn(List.of(user));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/users"));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(user.getId()))
                .andExpect(jsonPath("$[0].name").value(user.getName()))
                .andExpect(jsonPath("$[0].email").value(user.getEmail()));
    }

    @Test
    void getUser_idInDatabase_returnUserResponse() throws Exception {
        // Given
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        // Given
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", user.getId()));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void getUser_idNotInDatabase_returnNotFound() throws Exception {
        // Given
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        // Given
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}", user.getId()));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("User with id %s does not exists", user.getId())));
    }

    @Test
    void isAdmin_validIdAndIsUser_returnFalse() throws Exception {
        // Given
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.get("/api/users/{id}/is-admin", user.getId()));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(user.isAdmin()));

    }

    @Test
    void createUser_validRequest_returnUserResponse() throws Exception {
        // Given
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        UserRequest userRequest = new UserRequest("Alberto", "alberto@example.com", "alberto123");
        String jsonBody = mapper.writeValueAsString(userRequest);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON).content(jsonBody));

        // Then
        perform.andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void createUser_exmailExists_throwEmailExistsException() throws Exception {
        // Given
        Mockito.when(userRepository.save(any(User.class))).thenReturn(user);
        Mockito.when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        UserRequest userRequest = new UserRequest("Alberto", "alberto@example.com", "alberto123");
        String jsonBody = mapper.writeValueAsString(userRequest);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON).content(jsonBody));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email is taken"));
    }

    @Test
    void createUser_invalidRequest_returnValidationFailed() throws Exception {
        // Given
        String jsonBody = mapper.writeValueAsString(new UserRequest());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON).content(jsonBody));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid fields"))
                .andExpect(jsonPath("$.validationErrors").isNotEmpty());
    }

    @Test
    void updateUser_validRequest_returnUserResponse() throws Exception {
        // Given
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        UserRequestUpdate userRequestUpdate = new UserRequestUpdate("Alberto", "alberto@example.com");
        String jsonBody = mapper.writeValueAsString(userRequestUpdate);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON).content(jsonBody));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()));
    }

    @Test
    void updateUser_idNotInDatabase_returnNotFound() throws Exception {
        // Given
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Mockito.when(userRepository.save(user)).thenReturn(user);
        UserRequestUpdate userRequestUpdate = new UserRequestUpdate("Alberto", "alberto@example.com");
        String jsonBody = mapper.writeValueAsString(userRequestUpdate);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON).content(jsonBody));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("User with id %s does not exists", user.getId())));
    }

    @Test
    void updateUser_idInDatabaseButInvalidUserRequestUpdate_returnUserResponse() throws Exception {
        // Given
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);
        UserRequestUpdate userRequestUpdate = new UserRequestUpdate("", "");
        String jsonBody = mapper.writeValueAsString(userRequestUpdate);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.put("/api/users/{id}", user.getId())
                .contentType(MediaType.APPLICATION_JSON).content(jsonBody));

        // Then
        perform.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid fields"))
                .andExpect(jsonPath("$.validationErrors").isNotEmpty());
    }

    @Test
    void setAdmin_validIdAndIsUser_returnUserResponse() throws Exception {
        // Given
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.when(userRepository.save(user)).thenReturn(user);

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.patch("/api/users/{id}/admin", user.getId()));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.admin").value(true));
    }

    @Test
    void deleteUser_idInDatabase_returnDeletedId() throws Exception {
        // Given
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.ofNullable(user));
        Mockito.doNothing().when(userRepository).deleteById(user.getId());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", user.getId()));

        // Then
        perform.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(user.getId()));
    }

    @Test
    void deleteUser_idNotInDatabase_returnDeletedId() throws Exception {
        // Given
        Mockito.when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
        Mockito.doNothing().when(userRepository).deleteById(user.getId());

        // When
        ResultActions perform = mockMvc.perform(MockMvcRequestBuilders.delete("/api/users/{id}", user.getId()));

        // Then
        perform.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(String.format("User with id %s does not exists", user.getId())));
    }
}