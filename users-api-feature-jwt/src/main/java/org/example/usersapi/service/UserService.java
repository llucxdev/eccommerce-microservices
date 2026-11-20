package org.example.usersapi.service;

import org.example.usersapi.dto.UserRequest;
import org.example.usersapi.dto.UserRequestUpdate;
import org.example.usersapi.dto.UserResponse;
import org.example.usersapi.exception.EmailExistsException;
import org.example.usersapi.exception.NotFoundException;
import org.example.usersapi.exception.UnauthorizecAccessException;
import org.example.usersapi.model.User;
import org.example.usersapi.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder(12);

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id %s does not exists", id)));
        return convertToResponse(user);
    }

    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (User user : users) {
            userResponses.add(convertToResponse(user));
        }
        return userResponses;
    }

    public UserResponse registerUser(UserRequest userRequest) {
        if (isEmailTaken(userRequest.getEmail())) throw new EmailExistsException("Email is taken");
        return convertToResponse(userRepository.save(convertToEntity(userRequest)));
    }

    public UserResponse createUser(UserRequest userRequest) {
        if(!authService.isAdmin()) throw new UnauthorizecAccessException();
        return registerUser(userRequest);
    }

    public UserResponse updateUser(Long id, UserRequestUpdate userRequestUpdate) {
        if(!authService.isAdmin() && !authService.isSameUser(id)) throw new UnauthorizecAccessException();
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id %s does not exists", id)));
        if (isEmailTaken(userRequestUpdate.getEmail())) {
            throw new EmailExistsException("Email is taken");
        }
        if(userRequestUpdate.getName() != null) user.setName(userRequestUpdate.getName());
        if(userRequestUpdate.getEmail() != null) user.setEmail(userRequestUpdate.getEmail());
        return convertToResponse(userRepository.save(user));
    }

    public UserResponse setAdmin(Long id) {
        if(!authService.isAdmin()) throw new UnauthorizecAccessException();
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id %s does not exists", id)));
        user.setAdmin(true);
        return convertToResponse(userRepository.save(user));
    }

    public Long deleteUser(Long id) {
        if(!authService.isAdmin() && !authService.isSameUser(id)) throw new UnauthorizecAccessException();
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException(String.format("User with id %s does not exists", id)));
        userRepository.deleteById(user.getId());
        return user.getId();
    }

    private boolean isEmailTaken(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(user.getId(), user.getName(), user.getEmail(), user.isAdmin());
    }

    private User convertToEntity(UserRequest userRequest) {
        return new User(userRequest.getName(), userRequest.getEmail(), encoder.encode(userRequest.getPassword()));
    }
}
