package org.example.responsiveuserregistration.service;

import org.example.responsiveuserregistration.model.User;
import org.example.responsiveuserregistration.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Service
public class UserService implements UserDetailsService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void registerUser(String username, String email, String password){

        username = username.trim().toLowerCase();
        email = email.trim().toLowerCase();

        if (userRepository.existsByUsernameOrEmail(username, email)) {
            throw new IllegalArgumentException("User already exists");
        }

        String hashedPassword = passwordEncoder.encode(password);
        logger.info("Hashed password: {}", hashedPassword);

        User user = new User(username, email, hashedPassword);
        userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepository.findByUsername(username);

        if(userOptional.isPresent()) {
            User user = userOptional.get();

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername())
                    .password(user.getPassword())
                    .roles("USER")
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

//    public boolean authenticateUser(String username, String password) {
//        username = username.trim().toLowerCase();
//
//        Optional<User> userOptional = userRepository.findByUsername(username);
//
//        if(userOptional.isPresent()) {
//            User user = userOptional.get();
//            String hashesPassword = passwordEncoder.encode(password);
//
//            return passwordEncoder.matches(password, user.getPassword());
//        }
//        return false;
//    }

}
