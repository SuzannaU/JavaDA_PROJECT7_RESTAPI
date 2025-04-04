package com.nnk.springboot;

import com.nnk.springboot.domain.User;
import com.nnk.springboot.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
public class UserTests {

    @Autowired
    private UserRepository userRepository;

    @Test
    public void UserTest() {
        User user = new User("username", "password", "fullname", "role");

        // Save
        user = userRepository.save(user);
        assertNotNull(user.getId());
        assertEquals(user.getUsername(), "username");

        // Update
        user.setUsername("username2");
        user = userRepository.save(user);
        assertEquals(user.getUsername(), "username2");

        // Find
        List<User> listResult = userRepository.findAll();
        assertTrue(listResult.size() > 0);

        // Delete
        Integer id = user.getId();
        userRepository.deleteById(id);
        Optional<User> optUser = userRepository.findById(id);
        assertFalse(optUser.isPresent());
    }
}
