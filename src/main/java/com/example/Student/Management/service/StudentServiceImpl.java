package com.example.Student.Management.service;

import com.example.Student.Management.Model.Student;
import com.example.Student.Management.Model.User;
import com.example.Student.Management.Model.Course;
import com.example.Student.Management.repository.StudentRepository;
import com.example.Student.Management.repository.UserRepository;
import com.example.Student.Management.repository.CourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentServiceImpl implements StudentService {

    private final StudentRepository studentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    public StudentServiceImpl(StudentRepository studentRepository,
                              UserRepository userRepository, CourseRepository courseRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
        this.courseRepository=courseRepository;
    }


    @Override
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @Override
    public Student saveStudent(Student student) {
        return studentRepository.save(student);
    }

    @Override
    public Student getStudentById(Long id) {
        return studentRepository.findById(id).orElse(null);
    }

    @Override
    public void deleteStudentById(Long id) {
        studentRepository.deleteById(id);
    }


    @Override
    public Student getStudentByUsername(String username) {

        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isEmpty()) {
            return null;
        }

        return studentRepository.findByUser(userOpt.get()).orElse(null);
    }
    
     @Override
    public void assignCoursesToStudent(Long studentId, List<Long> courseIds) {

        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) return;

        Set<Course> courses = courseRepository
                .findAllById(courseIds)
                .stream()
                .collect(Collectors.toSet());

        student.setCourses(courses);
        studentRepository.save(student);
    }
    
    @Override
    public Student createStudentProfile(Long userId, String name, String department) {

    User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

    Student student = new Student();
    student.setName(name);
    student.setEmail(user.getEmail());
    student.setDepartment(department);
    student.setUser(user);

    return studentRepository.save(student);
}

    @Override
    public Student getStudentByUserId(Long userId) {
    return studentRepository.findByUserId(userId);
}

    @Transactional
    public void cleanupDuplicateStudents() {

    List<Student> allStudents = studentRepository.findAll();

    Map<Long, List<Student>> groupedByUser =
            allStudents.stream()
                    .filter(s -> s.getUser() != null)
                    .collect(Collectors.groupingBy(s -> s.getUser().getId()));

    for (List<Student> students : groupedByUser.values()) {

        if (students.size() <= 1) continue;

        Student toKeep = students.stream()
                .filter(s -> s.getCourses() != null && !s.getCourses().isEmpty())
                .findFirst()
                .orElse(students.get(0));

        for (Student s : students) {
            if (!s.getId().equals(toKeep.getId())) {
                studentRepository.delete(s);
            }
        }
    }
}
    


}
