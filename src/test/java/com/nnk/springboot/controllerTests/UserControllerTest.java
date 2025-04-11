package com.nnk.springboot.controllerTests;

import com.nnk.springboot.controllers.UserController;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private BCryptPasswordEncoder passwordEncoder;

    private User validUser;

    @BeforeEach
    public void setup() {
        validUser = new User("username", "Password123&", "fullname", "role");
    }

    private static Stream<Arguments> invalidUserProvider() {
        return Stream.of(
                Arguments.of("invalidUsername", new User("", "Password123&", "fullname", "role"), "username"),
                Arguments.of("invalidFullname", new User("username", "Password123&", "", "role"), "fullname"),
                Arguments.of("invalidRole", new User("username", "Password123&", "fullname", ""), "role")
        );
    }

    @Test
    @WithMockUser(roles = "USER")
    public void homeTest() throws Exception {

        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/user/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(content().string(containsString("User List")));

        verify(userRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void addUserFormTest() throws Exception {

        this.mockMvc.perform(get("/user/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(content().string(containsString("Add New User")));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest() throws Exception {

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(validUser);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/user/validate")
                        .flashAttr("user", validUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any());
        verify(userRepository).findAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"nouppercase1&", "NoDigits&", "NoSymbol123", "Under8&"})
    @WithMockUser(roles = "USER")
    public void passwordValidationTest(String invalidPassword) throws Exception {
        User invalidUser = new User("username", "", "fullname", "role");
        invalidUser.setPassword(invalidPassword);

        this.mockMvc.perform(post("/user/validate")
                        .flashAttr("user", invalidUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(content().string(containsString("Add New User")))
                .andExpect(model().attributeHasFieldErrors("user", "password"));

        this.mockMvc.perform(post("/user/update/{id}", 1)
                        .flashAttr("user", invalidUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(content().string(containsString("Update User")))
                .andExpect(model().attributeHasFieldErrors("user", "password"));
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidUserProvider")
    @WithMockUser(roles = "USER")
    public void validateTest_withErrors(String testedAttribute, User invalidUser, String error) throws Exception {

        this.mockMvc.perform(post("/user/validate")
                        .flashAttr("user", invalidUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(content().string(containsString("Add New User")))
                .andExpect(model().attributeHasFieldErrors("user", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void showUpdateFormTest() throws Exception {

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(validUser));

        this.mockMvc.perform(get("/user/update/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(content().string(containsString("Update User")));

        verify(userRepository).findById(1);
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateUserTest() throws Exception {

        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(validUser);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/user/update/{id}", 1)
                        .flashAttr("user", validUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        verify(passwordEncoder).encode(anyString());
        verify(userRepository).save(any());
        verify(userRepository).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidUserProvider")
    @WithMockUser(roles = "USER")
    public void updateUserTest_withErrors(String testedAttribute, User invalidUser, String error) throws Exception {

        this.mockMvc.perform(post("/user/update/{id}", 1)
                        .flashAttr("user", invalidUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(content().string(containsString("Update User")))
                .andExpect(model().attributeHasFieldErrors("user", error));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteUserTest() throws Exception {

        when(userRepository.findById(anyInt())).thenReturn(Optional.of(validUser));
        doNothing().when(userRepository).delete(any(User.class));
        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/user/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        verify(userRepository).findById(1);
        verify(userRepository).delete(any(User.class));
        verify(userRepository).findAll();
    }
}
