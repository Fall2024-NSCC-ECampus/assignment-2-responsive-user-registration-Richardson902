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


    // For authentication
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // Try to find user by given username
        Optional<User> userOptional = userRepository.findByUsername(username);

        // If user is found, return user details from optional to Spring Security
        if(userOptional.isPresent()) {
            User user = userOptional.get(); // creates a user details object

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getUsername()) // Set username
                    .password(user.getPassword()) // Set password
                    .roles("USER") // Set role
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

}
