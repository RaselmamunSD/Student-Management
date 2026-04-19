package com.example.Student.Management.Controller;

import com.example.Student.Management.Model.CalendarEvent;
import com.example.Student.Management.Model.CalendarEventType;
import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.EnrollmentStatus;
import com.example.Student.Management.Model.Student;
import com.example.Student.Management.Model.User;
import com.example.Student.Management.service.AcademicCalendarService;
import com.example.Student.Management.service.CourseService;
import com.example.Student.Management.service.EnrollmentService;
import com.example.Student.Management.service.StudentService;
import com.example.Student.Management.service.UserService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final StudentService ss;
    private final UserService us;
    private final CourseService cs;
    private final EnrollmentService enrollmentService;
    private final AcademicCalendarService academicCalendarService;

    public AdminController(StudentService ss,
                           UserService us,
                           CourseService cs,
                           EnrollmentService enrollmentService,
                           AcademicCalendarService academicCalendarService) {
        this.ss = ss;
        this.us = us;
        this.cs = cs;
        this.enrollmentService = enrollmentService;
        this.academicCalendarService = academicCalendarService;
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
                                @RequestParam(required = false) List<Long> courseIds) {
        ss.assignCoursesToStudent(studentId, courseIds != null ? courseIds : List.of());
        return "redirect:/admin/assign-courses";
    }

    @GetMapping("/enrollment-requests")
    public String enrollmentRequests(Model model) {
        List<Enrollment> pending = enrollmentService.listPending();
        model.addAttribute("pending", pending);
        return "admin/enrollment-requests";
    }

    @PostMapping("/enrollment-requests/{id}/approve")
    public String approveEnrollment(@PathVariable Long id) {
        enrollmentService.setEnrollmentStatus(id, EnrollmentStatus.APPROVED);
        return "redirect:/admin/enrollment-requests?approved=1";
    }

    @PostMapping("/enrollment-requests/{id}/reject")
    public String rejectEnrollment(@PathVariable Long id) {
        enrollmentService.setEnrollmentStatus(id, EnrollmentStatus.REJECTED);
        return "redirect:/admin/enrollment-requests?rejected=1";
    }

    @GetMapping("/calendar")
    public String manageCalendar(Model model) {
        model.addAttribute("events", academicCalendarService.listEvents());
        model.addAttribute("eventTypes", CalendarEventType.values());
        return "admin/calendar";
    }

    @PostMapping("/calendar/events")
    public String addCalendarEvent(@RequestParam String title,
                                   @RequestParam(required = false) String description,
                                   @RequestParam CalendarEventType eventType,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        CalendarEvent event = new CalendarEvent();
        event.setTitle(title);
        event.setDescription(description);
        event.setEventType(eventType);
        event.setStartDate(startDate);
        event.setEndDate(endDate != null ? endDate : startDate);
        academicCalendarService.saveEvent(event);
        return "redirect:/admin/calendar?saved=1";
    }

    @PostMapping("/calendar/events/{id}/delete")
    public String deleteCalendarEvent(@PathVariable Long id) {
        academicCalendarService.deleteEvent(id);
        return "redirect:/admin/calendar?deleted=1";
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
