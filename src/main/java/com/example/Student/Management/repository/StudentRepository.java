package com.example.Student.Management.repository;

import com.example.Student.Management.Model.Student;
import com.example.Student.Management.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByUser(User user);

    Student findByUserUsername(String username);

    Student findByUserId(Long userId);

    @Query("SELECT DISTINCT s FROM Student s "
            + "JOIN FETCH s.user "
            + "LEFT JOIN FETCH s.enrollments e "
            + "LEFT JOIN FETCH e.course "
            + "WHERE s.user.username = :username")
    Optional<Student> findDetailedByUsername(@Param("username") String username);
}
