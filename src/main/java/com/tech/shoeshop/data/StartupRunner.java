package com.tech.shoeshop.data;

import com.tech.shoeshop.entity.Role;
import com.tech.shoeshop.entity.User;
import com.tech.shoeshop.impl.RoleRepository;
import com.tech.shoeshop.impl.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StartupRunner implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {

        // Skip if data already exists
        if (userRepository.existsByUsername("admin")) {
            System.out.println(userRepository.findByUsername("admin"));
            return;
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .name("ROLE_ADMIN")
                                        .description("Administrator")
                                        .build()
                        )
                );

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseGet(() ->
                        roleRepository.save(
                                Role.builder()
                                        .name("ROLE_USER")
                                        .description("Normal User")
                                        .build()
                        )
                );

        User admin = User.builder()
                .username("admin")
                .email("admin@shoeshop.com")
                .password("admin123")
                .enabled(true)
                .build();

        admin.addRole(adminRole);
        admin.addRole(userRole);

        userRepository.save(admin);

        System.out.println("Seed data inserted.");
    }
}
