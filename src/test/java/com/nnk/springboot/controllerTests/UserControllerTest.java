package com.nnk.springboot.controllerTests;

import com.nnk.springboot.config.SpringSecurityConfig;
import com.nnk.springboot.controllers.UserController;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.services.CustomUserDetailsService;
import com.nnk.springboot.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
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
@Import({SpringSecurityConfig.class})
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private UserService userService;

    private User validUser;

    @BeforeEach
    public void setup() {
        validUser = new User("username", "Password123&", "fullname", "role");
    }

    private static Stream<Arguments> invalidUserProvider() {
        return Stream.of(
                Arguments.of("nullUsername", new User(null, "Password123&", "fullname", "role"), "username"),
                Arguments.of("emptyUsername", new User("", "Password123&", "fullname", "role"), "username"),
                Arguments.of("nullPassword", new User("username", null, "fullname", "role"), "password"),
                Arguments.of("emptyPassword", new User("username", "", "fullname", "role"), "password"),
                Arguments.of("nullFullname", new User("username", "Password123&", null, "role"), "fullname"),
                Arguments.of("emptyFullname", new User("username", "Password123&", "", "role"), "fullname"),
                Arguments.of("nullRole", new User("username", "Password123&", "fullname", null), "role"),
                Arguments.of("emptyRole", new User("username", "Password123&", "fullname", ""), "role")
        );
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getHome_shouldReturnList() throws Exception {

        when(userService.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/user/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/list"))
                .andExpect(content().string(containsString("User List")));

        verify(userService).findAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void getAddUserForm_shouldReturnForm() throws Exception {

        this.mockMvc.perform(get("/user/add"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(content().string(containsString("Add New User")));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void postValidate_shouldSaveUser() throws Exception {

        when(userService.save(any())).thenReturn(validUser);
        when(userService.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/user/validate")
                        .flashAttr("user", validUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        verify(userService).save(any());
        verify(userService).findAll();
    }

    @ParameterizedTest
    @ValueSource(strings = {"nouppercase1&", "NoDigits&", "NoSymbol123", "Under8&"})
    @WithMockUser(roles = "ADMIN")
    public void postValidateUpdate_withInvalidPassword_shouldShowError(String invalidPassword) throws Exception {
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
    @WithMockUser(roles = "ADMIN")
    public void postValidate_withInvalidUser_shouldShowError(String testedAttribute, User invalidUser, String error) throws Exception {

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
    @WithMockUser(roles = "ADMIN")
    public void getShowUpdateForm_shouldReturnForm() throws Exception {

        when(userService.findById(anyInt())).thenReturn(validUser);

        this.mockMvc.perform(get("/user/update/{id}", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(content().string(containsString("Update User")));

        verify(userService).findById(1);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    public void postUpdateUser_shouldSaveUser() throws Exception {

        when(userService.save(any())).thenReturn(validUser);
        when(userService.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(post("/user/update/{id}", 1)
                        .flashAttr("user", validUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        verify(userService).save(any());
        verify(userService).findAll();
    }

    @ParameterizedTest(name = "{0} should return {2} error")
    @MethodSource("invalidUserProvider")
    @WithMockUser(roles = "ADMIN")
    public void postUpdateUser_withInvalidUser_shouldShowError(String testedAttribute, User invalidUser, String error) throws Exception {

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
    @WithMockUser(roles = "ADMIN")
    public void getDeleteUser_shouldDeleteUser() throws Exception {

        when(userService.findById(anyInt())).thenReturn(validUser);
        doNothing().when(userService).delete(any(User.class));
        when(userService.findAll()).thenReturn(new ArrayList<>());

        this.mockMvc.perform(get("/user/delete/{id}", 1))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        verify(userService).findById(1);
        verify(userService).delete(any(User.class));
        verify(userService).findAll();
    }
}
