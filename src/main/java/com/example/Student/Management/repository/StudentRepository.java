package com.example.Student.Management.repository;

import com.example.Student.Management.Model.Student;
import com.example.Student.Management.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser(User user);
    Student findByUserUsername(String username);
    Student findByUserId(Long userId);
}
