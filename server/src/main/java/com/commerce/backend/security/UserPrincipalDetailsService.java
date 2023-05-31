package com.commerce.backend.security;

import com.commerce.backend.dao.UserRepository;
import com.commerce.backend.model.entity.User;
import com.commerce.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserPrincipalDetailsService implements UserDetailsService {

    private UserRepository userService;

    @Autowired
    public UserPrincipalDetailsService(UserRepository userService) {
        this.userService = userService;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = this.userService.findByEmail(username).orElse(null);
        UserPrincipal userPrincipal = new UserPrincipal(user);

        return userPrincipal;
    }

}