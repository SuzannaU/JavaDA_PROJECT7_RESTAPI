package com.nnk.springboot.controllerTests;

import com.nnk.springboot.controllers.UserController;
import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Optional;

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

    @MockBean
    private UserRepository userRepository;

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

        User validUser = new User("username", "password", "fullname", "role");
        when(userRepository.save(any())).thenReturn(validUser);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());


        this.mockMvc.perform(post("/user/validate")
                        .flashAttr("user", validUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        //verify(bCryptPasswordEncoder).encode(anyString());
        verify(userRepository).save(any());
        verify(userRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void validateTest_withErrors() throws Exception {

        User invalidUserUsername = new User("", "password", "fullname", "role");
        User invalidUserPassword = new User("username", "", "fullname", "role");
        User invalidUserFullname = new User("username", "password", "", "role");
        User invalidUserRole = new User("username", "password", "fullname", "");

        this.mockMvc.perform(post("/user/validate")
                        .flashAttr("user", invalidUserUsername)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(content().string(containsString("Add New User")))
                .andExpect(model().attributeHasFieldErrors("user", "username"));

        this.mockMvc.perform(post("/user/validate")
                        .flashAttr("user", invalidUserPassword)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(content().string(containsString("Add New User")))
                .andExpect(model().attributeHasFieldErrors("user", "password"));

        this.mockMvc.perform(post("/user/validate")
                        .flashAttr("user", invalidUserFullname)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(content().string(containsString("Add New User")))
                .andExpect(model().attributeHasFieldErrors("user", "fullname"));

        this.mockMvc.perform(post("/user/validate")
                        .flashAttr("user", invalidUserRole)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/add"))
                .andExpect(content().string(containsString("Add New User")))
                .andExpect(model().attributeHasFieldErrors("user", "role"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void showUpdateFormTest() throws Exception {

        User validUser = new User("username", "password", "fullname", "role");
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

        User validUser = new User("username", "password", "fullname", "role");when(userRepository.save(any())).thenReturn(validUser);
        when(userRepository.findAll()).thenReturn(new ArrayList<>());


        this.mockMvc.perform(post("/user/update/{id}", 1)
                        .flashAttr("user", validUser)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/list"));

        verify(userRepository).save(any());
        verify(userRepository).findAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    public void updateUserTest_withErrors() throws Exception {

        User invalidUserUsername = new User("", "password", "fullname", "role");
        User invalidUserPassword = new User("username", "", "fullname", "role");
        User invalidUserFullname = new User("username", "password", "", "role");
        User invalidUserRole = new User("username", "password", "fullname", "");

        this.mockMvc.perform(post("/user/update/{id}", 1)
                        .flashAttr("user", invalidUserUsername)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(content().string(containsString("Update User")))
                .andExpect(model().attributeHasFieldErrors("user", "username"));

        this.mockMvc.perform(post("/user/update/{id}", 1)
                        .flashAttr("user", invalidUserPassword)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(content().string(containsString("Update User")))
                .andExpect(model().attributeHasFieldErrors("user", "password"));

        this.mockMvc.perform(post("/user/update/{id}", 1)
                        .flashAttr("user", invalidUserFullname)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(content().string(containsString("Update User")))
                .andExpect(model().attributeHasFieldErrors("user", "fullname"));

        this.mockMvc.perform(post("/user/update/{id}", 1)
                        .flashAttr("user", invalidUserRole)
                        .with(csrf().asHeader()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("user/update"))
                .andExpect(content().string(containsString("Update User")))
                .andExpect(model().attributeHasFieldErrors("user", "role"));
    }

    @Test
    @WithMockUser(roles = "USER")
    public void deleteUserTest() throws Exception {

        User validUser = new User("username", "password", "fullname", "role");
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
