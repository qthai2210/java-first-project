package com.example.infrastructure.config;

import com.example.application.port.out.PasswordEncoderPort;
import com.example.application.port.out.UserPersistencePort;
import com.example.domain.model.Role;
import com.example.domain.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Component
public class DatabaseSeeder implements CommandLineRunner {

    private final UserPersistencePort userPersistencePort;
    private final PasswordEncoderPort passwordEncoderPort;

    @Value("${app.seed.admin.email:admin@stockai.com}")
    private String adminEmail;

    @Value("${app.seed.admin.password:Admin123!}")
    private String adminPassword;

    @Value("${app.seed.demo.email:demo@stockai.com}")
    private String demoEmail;

    @Value("${app.seed.demo.password:Demo123!}")
    private String demoPassword;

    public DatabaseSeeder(UserPersistencePort userPersistencePort, PasswordEncoderPort passwordEncoderPort) {
        this.userPersistencePort = userPersistencePort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Checking database seed requirements...");

        // Seed Admin User
        if (!userPersistencePort.existsByEmail(adminEmail)) {
            User admin = User.builder()
                    .name("System Admin")
                    .email(adminEmail)
                    .password(passwordEncoderPort.encode(adminPassword))
                    .role(Role.ADMIN)
                    .createdAt(LocalDateTime.now())
                    .build();
            userPersistencePort.save(admin);
            log.info("Seeded Admin user: {}", adminEmail);
        } else {
            log.debug("Admin user {} already exists. Skipping seed.", adminEmail);
        }

        // Seed Demo User
        if (!userPersistencePort.existsByEmail(demoEmail)) {
            User demo = User.builder()
                    .name("Demo User")
                    .email(demoEmail)
                    .password(passwordEncoderPort.encode(demoPassword))
                    .role(Role.USER)
                    .createdAt(LocalDateTime.now())
                    .build();
            userPersistencePort.save(demo);
            log.info("Seeded Demo user: {}", demoEmail);
        } else {
            log.debug("Demo user {} already exists. Skipping seed.", demoEmail);
        }
        
        log.info("Database seeding completed.");
    }
}
