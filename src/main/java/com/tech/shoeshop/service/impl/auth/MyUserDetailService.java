package com.tech.shoeshop.service.impl.auth;

import com.tech.shoeshop.entity.auth.User;
import com.tech.shoeshop.model.MyUserDetail;
import com.tech.shoeshop.repository.auth.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MyUserDetailService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Starting load user by username");

        User user = userRepository.findByUsername(username);

        if(user == null){
            log.warn("User not found: username: {}", username);
            throw new UsernameNotFoundException("Username not found: "+ username);
        }

        log.info("Load user by username successfully");

        return new MyUserDetail(user);
    }
}
