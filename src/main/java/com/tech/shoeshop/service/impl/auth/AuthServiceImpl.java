package com.tech.shoeshop.service.impl.auth;

import com.tech.shoeshop.dto.request.auth.LoginRequest;
import com.tech.shoeshop.dto.request.auth.RegisterRequest;
import com.tech.shoeshop.dto.response.auth.LoginResponse;
import com.tech.shoeshop.entity.auth.Role;
import com.tech.shoeshop.entity.auth.User;
import com.tech.shoeshop.enums.RoleName;
import com.tech.shoeshop.exception.DuplicateEmailException;
import com.tech.shoeshop.exception.DuplicateUsernameException;
import com.tech.shoeshop.exception.ResourceNotFoundException;
import com.tech.shoeshop.repository.auth.RoleRepository;
import com.tech.shoeshop.repository.auth.UserRepository;
import com.tech.shoeshop.mapper.AuthMapper;
import com.tech.shoeshop.security.jwt.JwtService;
import com.tech.shoeshop.service.auth.AuthService;
import com.tech.shoeshop.util.BCryptHasherUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final AuthMapper authMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${ACCESS_TOKEN_EXPIRATION}")
    private String ACCESS_TOKEN_EXPIRATION;

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

        User user = authMapper.toEntity(request, BCryptHasherUtil.hash(request.getPassword()));
        user.addRole(userRole);

        userRepository.save(user);

        log.info("User '{}' registered successfully with role '{}'.",
                user.getUsername(),
                userRole.getName());
    }

    /**
     * Authenticate user credentials.
     *
     * @param request login request containing username and password
     * @return login response with JWT token
     * @throws AuthenticationException if username or password is incorrect
     */
    @Override
    public LoginResponse verify(LoginRequest request) {

        log.info("Authenticating user '{}'", request.getUsername());

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );

            String accessToken = jwtService.generateToken(authentication);

            log.info("User '{}' authenticated successfully.", authentication.getName());

            return LoginResponse.builder()
                    .accessToken(accessToken)
                    .expiresIn(ACCESS_TOKEN_EXPIRATION)
                    .build();

        } catch (BadCredentialsException ex) {

            log.warn("Authentication failed for user '{}': Invalid credentials.",
                    request.getUsername());
            throw ex;

        } catch (LockedException ex) {

            log.warn("Authentication failed for user '{}': Account is locked.",
                    request.getUsername());
            throw ex;

        } catch (DisabledException ex) {

            log.warn("Authentication failed for user '{}': Account is disabled.",
                    request.getUsername());
            throw ex;

        } catch (CredentialsExpiredException ex) {

            log.warn("Authentication failed for user '{}': Credentials have expired.",
                    request.getUsername());
            throw ex;

        } catch (AuthenticationException ex) {

            log.warn("Authentication failed for user '{}'.",
                    request.getUsername());
            throw ex;
        }
    }
}
