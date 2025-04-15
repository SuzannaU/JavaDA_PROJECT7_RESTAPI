package com.nnk.springboot.serviceTests;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import com.nnk.springboot.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class UserServiceTest {

    @Autowired
    private UserService userService;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    public void findAll_shouldReturnList() {
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        List<User> users = userService.findAll();

        assertNotNull(users);
        verify(userRepository).findAll();
    }

    @Test
    public void save_shouldReturnSavedUser() {
        User user = new User("username", "password", "fullname", "role");
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgument(0));

        User savedUser = userService.save(user);

        assertNotNull(savedUser);
        assertEquals("encodedPassword", savedUser.getPassword());
        verify(userRepository).save(any(User.class));
    }

    @Test
    public void findById_shouldReturnUser() {
        when(userRepository.findById(anyInt()))
                .thenReturn(Optional.of(new User("username", "password", "fullname", "role")));

        User user = userService.findById(1);

        assertNotNull(user);
        verify(userRepository).findById(anyInt());
    }

    @Test
    public void findById_withInvalidId_shouldThrowException() {
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class,
                () -> userService.findById(1));

        verify(userRepository).findById(anyInt());
    }

    @Test
    public void delete_shouldCallRepo() {
        User user = new User("username", "password", "fullname", "role");
        doNothing().when(userRepository).delete(any(User.class));

        userService.delete(user);

        verify(userRepository).delete(user);
    }
}
