package com.example.Student.Management.Controller;

import com.example.Student.Management.Model.Course;
import com.example.Student.Management.Model.EnrollmentStatus;
import com.example.Student.Management.Model.Student;
import com.example.Student.Management.Model.TimetableEntry;
import com.example.Student.Management.service.AcademicCalendarService;
import com.example.Student.Management.service.CourseService;
import com.example.Student.Management.service.StudentService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/academic")
public class AcademicCalendarController {

    private final AcademicCalendarService academicCalendarService;
    private final StudentService studentService;
    private final CourseService courseService;

    public AcademicCalendarController(AcademicCalendarService academicCalendarService,
                                      StudentService studentService,
                                      CourseService courseService) {
        this.academicCalendarService = academicCalendarService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping("/calendar")
    public String calendar(Model model, Authentication authentication) {
        model.addAttribute("events", academicCalendarService.listEvents());

        List<TimetableEntry> timetable = new ArrayList<>();
        if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_STUDENT"))) {
            Student s = studentService.getStudentByUsername(authentication.getName());
            if (s != null) {
                List<Long> courseIds = s.getEnrollments().stream()
                        .filter(e -> e.getStatus() == EnrollmentStatus.APPROVED)
                        .map(e -> e.getCourse().getId())
                        .collect(Collectors.toList());
                timetable = academicCalendarService.listTimetableForCourseIds(courseIds);
            }
        } else if (authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_FACULTY"))) {
            List<Course> courses = courseService.getCoursesByFacultyUsername(authentication.getName());
            List<Long> ids = courses.stream().map(Course::getId).collect(Collectors.toList());
            timetable = academicCalendarService.listTimetableForCourseIds(ids);
        } else {
            List<Course> all = courseService.getAllCourses();
            List<Long> ids = all.stream().map(Course::getId).collect(Collectors.toList());
            timetable = academicCalendarService.listTimetableForCourseIds(ids);
        }
        model.addAttribute("timetable", timetable);
        return "academic/calendar";
    }
}
