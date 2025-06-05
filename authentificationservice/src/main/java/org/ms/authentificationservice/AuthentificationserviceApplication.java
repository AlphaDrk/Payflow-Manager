package org.ms.authentificationservice;

import org.ms.authentificationservice.entities.AppRole;
import org.ms.authentificationservice.entities.AppUser;
import org.ms.authentificationservice.services.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

@SpringBootApplication
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class AuthentificationserviceApplication {
    private static final Logger logger = LoggerFactory.getLogger(AuthentificationserviceApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(AuthentificationserviceApplication.class, args);
    }

    @Bean
    CommandLineRunner start(UserService userService) {
        return args -> {
            try {
                // Try to create users
                try {
                    AppUser user1 = new AppUser(null, "user1", "123", new ArrayList<>());
                    userService.addUser(user1);
                    logger.info("Created user1");
                } catch (RuntimeException e) {
                    logger.info("User1 already exists");
                }

                try {
                    AppUser user2 = new AppUser(null, "user2", "456", new ArrayList<>());
                    userService.addUser(user2);
                    logger.info("Created user2");
                } catch (RuntimeException e) {
                    logger.info("User2 already exists");
                }

                // Try to create roles
                AppRole roleUser = new AppRole(null, "USER");
                AppRole roleAdmin = new AppRole(null, "ADMIN");
                try {
                    userService.addRole(roleUser);
                    logger.info("Created USER role");
                } catch (Exception e) {
                    logger.info("USER role already exists");
                }

                try {
                    userService.addRole(roleAdmin);
                    logger.info("Created ADMIN role");
                } catch (Exception e) {
                    logger.info("ADMIN role already exists");
                }

                // Add roles to users
                try {
                    userService.addRoleToUser("user1", "USER");
                    userService.addRoleToUser("user2", "USER");
                    userService.addRoleToUser("user2", "ADMIN");
                    logger.info("Added roles to users");
                } catch (Exception e) {
                    logger.warn("Error adding roles to users: {}", e.getMessage());
                }

                // Log current users
                logger.info("Current users in the system:");
                for (AppUser appUser : userService.getAllUsers()) {
                    logger.info("User: {}", appUser.getUsername());
                    logger.info("Roles: {}", appUser.getRoles());
                }

            } catch (Exception e) {
                logger.error("Error during initialization: {}", e.getMessage());
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
