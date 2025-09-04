package com.vo.legendscombat.config;

import com.vo.legendscombat.domain.entity.Role;
import com.vo.legendscombat.domain.entity.User;
import com.vo.legendscombat.repository.RoleRepository;
import com.vo.legendscombat.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataInitializer {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;

    @Bean
    @Transactional
    CommandLineRunner initData() {
        return args -> {
            // Ensure base roles exist
            Role userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(new Role("ROLE_USER")));
            Role adminRole = roleRepository.findByName("ROLE_ADMIN").orElseGet(() -> roleRepository.save(new Role("ROLE_ADMIN")));
            log.info("Ensured base roles exist: {}, {}", userRole.getName(), adminRole.getName());

            // Optionally create an admin user when CREATE_ADMIN=true
            boolean createAdmin = Boolean.parseBoolean(System.getenv().getOrDefault("CREATE_ADMIN", "false"));

            if (createAdmin) {
                String adminEmail = System.getenv().getOrDefault("ADMIN_EMAIL", "admin@example.com");
                String adminPassword = System.getenv().getOrDefault("ADMIN_PASSWORD", "admin123");
                if (!userRepository.existsByEmail(adminEmail)) {
                    User admin = new User();
                    admin.setFirstName("Admin");
                    admin.setLastName("User");
                    admin.setEmail(adminEmail);
                    admin.setPassword(encoder.encode(adminPassword));
                    admin.setEnabled(true);
                    admin.setRoles(Set.of(adminRole, userRole));
                    userRepository.save(admin);
                    log.warn("Admin user created: {} (CHANGE THE PASSWORD!)", adminEmail);
                } else {
                    log.info("Admin user already exists: {}", adminEmail);
                }
            }
        };
    }
}
