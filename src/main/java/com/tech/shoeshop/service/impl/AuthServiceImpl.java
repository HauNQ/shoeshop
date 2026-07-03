package com.tech.shoeshop.service.impl;

import com.tech.shoeshop.dto.register.RegisterRequest;
import com.tech.shoeshop.entity.Role;
import com.tech.shoeshop.entity.User;
import com.tech.shoeshop.enums.RoleName;
import com.tech.shoeshop.exception.DuplicateEmailException;
import com.tech.shoeshop.exception.DuplicateUsernameException;
import com.tech.shoeshop.exception.ResourceNotFoundException;
import com.tech.shoeshop.impl.RoleRepository;
import com.tech.shoeshop.impl.UserRepository;
import com.tech.shoeshop.mapper.AuthMapper;
import com.tech.shoeshop.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthMapper authMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Registers a new user.
     *
     * @throws DuplicateUsernameException if username already exists
     * @throws DuplicateEmailException if email already exists
     * @throws ResourceNotFoundException if role not found
     */
    @Override
    @Transactional
    public void register(RegisterRequest request) {
        log.info("Registration request received for username: {}", request.getUsername());

        if(userRepository.existsByUsername(request.getUsername())){
            log.warn("Registration failed. Username '{}' already exists.", request.getUsername());
            throw new DuplicateUsernameException(request.getUsername());
        }

        if(userRepository.existsByEmail(request.getEmail())){
            log.warn("Registration failed. Email '{}' already exists.", request.getEmail());
            throw new DuplicateEmailException(request.getEmail());
        }

        Role userRole = roleRepository.findByName(RoleName.ROLE_USER)
                .orElseThrow(() -> {
                    log.error("Default role '{}' not found.", RoleName.ROLE_USER);
                    return new ResourceNotFoundException("Default role not found");});

        User user = authMapper.toEntity(request, passwordEncoder.encode(request.getPassword()));
        user.addRole(userRole);

        userRepository.save(user);

        log.info("User '{}' registered successfully with role '{}'.",
                user.getUsername(),
                userRole.getName());
    }
}
