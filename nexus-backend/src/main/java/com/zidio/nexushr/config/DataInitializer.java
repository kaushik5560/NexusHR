package com.zidio.nexushr.config;

import com.zidio.nexushr.entity.Role;
import com.zidio.nexushr.entity.RoleType;
import com.zidio.nexushr.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        // Automatically seed system roles if they don't already exist
        Arrays.stream(RoleType.values()).forEach(roleType -> {
            if (roleRepository.findByName(roleType).isEmpty()) {
                Role role = Role.builder()
                        .name(roleType)
                        .build();
                roleRepository.save(role);
                logger.info("Seeded system role into PostgreSQL: {}", roleType);
            }
        });
    }
}