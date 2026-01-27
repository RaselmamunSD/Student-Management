package com.example.Student.Management.repository;
import com.example.Student.Management.Model.User;
import com.example.Student.Management.Model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface UserRepository extends JpaRepository<User, Long>
{
    Optional<User> findByUsername(String username);
    List<User> findByRole(Role role);
}
