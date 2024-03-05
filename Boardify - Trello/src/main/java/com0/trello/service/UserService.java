package com0.trello.service;

import com0.trello.model.User;
import com0.trello.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired

    UserRepository userRepository;

    public String createUser(User user){

        userRepository.save(user);
        return "User data created";
    }
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
