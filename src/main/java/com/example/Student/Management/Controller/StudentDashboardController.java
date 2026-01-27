package com.example.Student.Management.Controller;

import com.example.Student.Management.Model.Student;
import com.example.Student.Management.Security.CustomUserDetails;
import com.example.Student.Management.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/student")
public class StudentDashboardController {

    private final StudentService studentService;

    public StudentDashboardController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/profile")
    public String myProfile(Model model, Authentication authentication) {

        String username = authentication.getName();

        Student student = studentService.getStudentByUsername(username);

        if (student == null) {
            model.addAttribute("error",
                    "Student profile not found. Please contact administrator.");
            return "student/profile";
        }

        model.addAttribute("student", student);
        return "student/profile";
    }

    @GetMapping("/courses")
    public String myCourses(Model model, Authentication authentication) {

        String username = authentication.getName();

        Student student = studentService.getStudentByUsername(username);

        if (student == null) {
            model.addAttribute("error",
                    "Student profile not found. Please contact administrator.");
            model.addAttribute("courses", java.util.List.of());
            return "student/courses";
        }

        model.addAttribute("courses", student.getCourses());
        return "student/courses";
    }
}
