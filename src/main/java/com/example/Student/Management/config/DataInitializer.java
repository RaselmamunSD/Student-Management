package com.example.Student.Management.config;

import com.example.Student.Management.Model.User;
import com.example.Student.Management.Model.Role;
import com.example.Student.Management.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@DependsOn("entityManagerFactory")
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("student").isEmpty()) {
            User student = new User();
            student.setUsername("student");
            student.setEmail("student@example.com");
            student.setPassword(passwordEncoder.encode("std123"));
            student.setRole(Role.STUDENT);
            userRepository.save(student);
        }

        if (userRepository.findByUsername("faculty").isEmpty()) {
            User faculty = new User();
            faculty.setUsername("faculty");
            faculty.setEmail("faculty@example.com");
            faculty.setPassword(passwordEncoder.encode("fac123"));
            faculty.setRole(Role.FACULTY);
            userRepository.save(faculty);
        }
    }
}
