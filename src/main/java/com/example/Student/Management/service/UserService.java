package com.example.Student.Management.service;
import com.example.Student.Management.Model.User;
import java.util.*;
public interface UserService 
{
    User saveUser(User user);
    Optional<User> findByUsername(String username);
    User getUserByUsername(String username);
    void createUser(User user, String role);
    List<User> getAllStudentsWithoutProfile();
    List<User> getAllStudentUsersWithoutProfile();
    User getUserById(Long id);
    boolean userHasStudentProfile(Long userId);
    void deleteUserById(Long userId);
    List<User> getAllUsers();
}
