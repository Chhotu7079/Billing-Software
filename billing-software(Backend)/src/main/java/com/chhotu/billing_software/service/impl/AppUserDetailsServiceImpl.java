package com.chhotu.billing_software.service.impl;

import com.chhotu.billing_software.entity.UserEntity;
import com.chhotu.billing_software.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;


@Service
@RequiredArgsConstructor
public class AppUserDetailsServiceImpl implements UserDetailsService {
    // Injecting the UserRepository to fetch user details from the database
    private final UserRepository userRepository;


    /**
     * This method is used by Spring Security to load user-specific data during authentication.
     * It returns a UserDetails object that includes the username, password, and granted authorities (roles).
     *
     * @param email the username/email of the user trying to authenticate
     * @return UserDetails object containing user credentials and roles
     * @throws UsernameNotFoundException if the user is not found in the database
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Fetch user from the database using the provided email
        UserEntity existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Email not found for that email : "+ email));
        // Return Spring Security's User object with email, password, and role
        return new User(existingUser.getEmail(), existingUser.getPassword(), Collections.singleton(new SimpleGrantedAuthority(existingUser.getRole())));
    }
}
