package com.example.VTM.userAdministartion.services;


import com.example.VTM.userAdministartion.entityOrDomain.User;
import com.example.VTM.userAdministartion.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws
            UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(usernameOrEmail , usernameOrEmail);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username :" + usernameOrEmail);
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), false,
                true, true, false,
                Collections.emptyList()

        );
    }
}
