package com.example.Student.Management.service;
import com.example.Student.Management.Model.User;
import com.example.Student.Management.Model.Student;
import com.example.Student.Management.repository.UserRepository;
import com.example.Student.Management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.Student.Management.Model.Role;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService
{
    private final UserRepository ur;
    private final PasswordEncoder pe;
    private final StudentRepository sr;
    
    public UserServiceImpl(UserRepository ur, PasswordEncoder pe, StudentRepository sr)
    {
        this.ur=ur;
        this.pe=pe;
        this.sr=sr;
    }
    
    @Override
    public User saveUser(User user)
    {
        return ur.save(user);
    }
    
    @Override
    public Optional<User> findByUsername(String username)
    {
        return ur.findByUsername(username);
        
    }
    
    @Override
    public User getUserByUsername(String username)
    {
        return ur.findByUsername(username).orElseThrow(()->new RuntimeException("User not found"));
    }
    
    @Override
    public void createUser(User user, String role) 
    {
        user.setEmail(user.getUsername());
        user.setPassword(pe.encode(user.getPassword()));
        user.setRole(Role.valueOf(role.toUpperCase()));
        ur.save(user);
    }   
    
    @Override
    public List<User> getAllStudentsWithoutProfile() {

    List<User> students = ur.findByRole(Role.STUDENT);
    List<Student> studentProfiles = sr.findAll();

    Set<Long> usedUserIds = studentProfiles.stream()
            .map(s -> s.getUser().getId())
            .collect(Collectors.toSet());

    return students.stream()
            .filter(u -> !usedUserIds.contains(u.getId()))
            .toList();
}
    
     @Override
    public List<User> getAllStudentUsersWithoutProfile() {

        List<Long> usedUserIds =
                sr.findAll()
                        .stream()
                        .filter(s -> s.getUser() != null)
                        .map(s -> s.getUser().getId())
                        .toList();

        return ur.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.STUDENT)
                .filter(u -> !usedUserIds.contains(u.getId()))
                .collect(Collectors.toList());
    }

    @Override
    public User getUserById(Long id) {
        return ur.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));
    }
    
    @Override
    public boolean userHasStudentProfile(Long userId) {
        return sr.findByUserId(userId) != null;
}

    @Override
    public void deleteUserById(Long userId) {
        ur.deleteById(userId);
}
    @Override
    public List<User> getAllUsers() {
    return ur.findAll();
}
}


