package com0.trello;

import com0.trello.model.User;
import com0.trello.repository.UserRepository;
import com0.trello.service.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateUser() {
        // Mocking data
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setSecurityAnswer("answer");

        // Call the createUser method
        String result = userService.createUser(user);

        // Verify that userRepository.save() is called once and the returned message is correct
        verify(userRepository, times(1)).save(user);
        Assertions.assertEquals("User data created", result);
    }

    @Test
    public void testFindUserByEmail() {
        // Mocking data
        String email = "user@example.com";
        User user = new User();
        user.setEmail(email);
        user.setPassword("password");
        user.setSecurityAnswer("answer");

        // Mocking userRepository.findByEmail()
        when(userRepository.findByEmail(email)).thenReturn(user);

        // Call the findUserByEmail method
        User foundUser = userService.findUserByEmail(email);

        // Verify that userRepository.findByEmail() is called once and the returned user is correct
        verify(userRepository, times(1)).findByEmail(email);
        Assertions.assertEquals(user, foundUser);
    }
}
