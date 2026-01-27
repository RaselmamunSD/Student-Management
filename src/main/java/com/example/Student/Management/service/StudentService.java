package com.example.Student.Management.service;

import com.example.Student.Management.Model.Student;
import java.util.List;

public interface StudentService {
    List<Student> getAllStudents();
    Student saveStudent(Student student);
    Student getStudentById(Long id);
    void deleteStudentById(Long id);
    Student getStudentByUsername(String username);
    void assignCoursesToStudent(Long studentId, List<Long> courseIds);
    Student createStudentProfile(Long userId, String name, String department);
    public Student getStudentByUserId(Long userId);
    void cleanupDuplicateStudents();
    
}
