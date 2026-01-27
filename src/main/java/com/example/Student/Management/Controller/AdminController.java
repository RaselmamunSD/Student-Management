package com.example.Student.Management.Controller;

import com.example.Student.Management.Model.Student;
import com.example.Student.Management.Model.User;
import com.example.Student.Management.service.StudentService;
import com.example.Student.Management.service.UserService;
import com.example.Student.Management.service.CourseService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService ss;
    private final UserService us;
    private final CourseService cs;

    public AdminController(StudentService ss,
                           UserService us,
                           CourseService cs) {
        this.ss = ss;
        this.us = us;
        this.cs = cs;
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
    model.addAttribute("users", us.getAllUsers());
    return "admin/users";
}


    @GetMapping("/courses")
    public String manageCourses() {
        return "admin/courses";
    }

    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/register";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, @RequestParam String role,Model model) {
    try {
        us.createUser(user, role);
        return "redirect:/admin/users";
    } catch (Exception e) {
        model.addAttribute("error","Username or email already exists. Please use a different one.");
        return "admin/register";
    }
}

    @GetMapping("/students/create")
    public String showCreateStudentForm(Model model) {

        model.addAttribute("student", new Student());

        model.addAttribute(
                "users",
                us.getAllStudentUsersWithoutProfile()
        );

        return "admin/create-student";
    }

    @PostMapping("/students/create")
    public String createStudent(@ModelAttribute Student student, @RequestParam Long userId) {

        User user = us.getUserById(userId);

        Student existingStudent =
                ss.getStudentByUserId(userId);

        if (existingStudent != null) {
            return "redirect:/admin/users";
        }

        student.setUser(user);
        ss.saveStudent(student);

        return "redirect:/admin/users";
    }

    @GetMapping("/assign-courses")
    public String showAssignCourses(Model model) {

        model.addAttribute("students", ss.getAllStudents());
        model.addAttribute("courses", cs.getAllCourses());

        return "admin/assign-courses";
    }

    @PostMapping("/assign-courses")
    public String assignCourses(@RequestParam Long studentId,
                                @RequestParam List<Long> courseIds) {

        ss.assignCoursesToStudent(studentId, courseIds);
        return "redirect:/admin/assign-courses";
    }

    @GetMapping("/dashboard")
    public String returnDashboard() {
        return "dashboard";
    }
    
    @GetMapping("/cleanup-students")
    public String cleanupStudents() {
    ss.cleanupDuplicateStudents();
    return "redirect:/admin/users";
    }

    @DeleteMapping("/users/{id}")
    public String deleteUser(@PathVariable Long id) {

    if (us.userHasStudentProfile(id)) {
        return "redirect:/admin/users?error=linked";
    }

    us.deleteUserById(id);
    return "redirect:/admin/users";
}


}
