package com0.trello.controller;

import com0.trello.model.ChangePasswordAttempt;
import com0.trello.model.LoginAttempt;
import com0.trello.model.User;
import com0.trello.repository.UserRepository;
import com0.trello.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserRepository userRepository;

    @PostMapping("/create")
    public String createUser(@RequestBody User user){
        return userService.createUser(user);
    }

    @PostMapping("/login")
    public Long login(@RequestBody LoginAttempt request){
        User user = userRepository.findByEmail(request.getEmail());

        if (user != null && request.getPassword().equals(user.getPassword())){
            return user.getId();
        }else{
            return -1L;
        }
    }
    @PostMapping("/change-password")
    public String changePassword(@RequestBody ChangePasswordAttempt request){
        User user = userRepository.findByEmail(request.getEmail());

        if(user == null){
            return "No user found with that email";
        }else if(!request.getSecurityAnswer().equals(user.getSecurityAnswer())){
            return "Incorrect security answer";
        }else{
            System.out.println("Current: " + user.getPassword());
            System.out.println("New: " + request.getPassword());
            user.setPassword(request.getPassword());
            userRepository.save(user);
            return "Password reset successful";
        }
    }
}
