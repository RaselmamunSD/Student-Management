package com.example.Student.Management.config;

import com.example.Student.Management.Model.CalendarEvent;
import com.example.Student.Management.Model.CalendarEventType;
import com.example.Student.Management.Model.Course;
import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.EnrollmentStatus;
import com.example.Student.Management.Model.Role;
import com.example.Student.Management.Model.Student;
import com.example.Student.Management.Model.User;
import com.example.Student.Management.repository.CalendarEventRepository;
import com.example.Student.Management.repository.CourseRepository;
import com.example.Student.Management.repository.EnrollmentRepository;
import com.example.Student.Management.repository.StudentRepository;
import com.example.Student.Management.repository.UserRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Configuration
@DependsOn("entityManagerFactory")
public class DataInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CalendarEventRepository calendarEventRepository;

    public DataInitializer(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           CourseRepository courseRepository,
                           StudentRepository studentRepository,
                           EnrollmentRepository enrollmentRepository,
                           CalendarEventRepository calendarEventRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.courseRepository = courseRepository;
        this.studentRepository = studentRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.calendarEventRepository = calendarEventRepository;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@example.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);
        }

        if (userRepository.findByUsername("student").isEmpty()) {
            User student = new User();
            student.setUsername("student");
            student.setEmail("student@example.com");
            student.setPassword(passwordEncoder.encode("std123"));
            student.setRole(Role.STUDENT);
            userRepository.save(student);
        }

        if (userRepository.findByUsername("faculty").isEmpty()) {
            User faculty = new User();
            faculty.setUsername("faculty");
            faculty.setEmail("faculty@example.com");
            faculty.setPassword(passwordEncoder.encode("fac123"));
            faculty.setRole(Role.FACULTY);
            userRepository.save(faculty);
        }

        if (courseRepository.count() == 0) {
            courseRepository.save(new Course("CS 101 — Introduction to Programming", "faculty", 3));
            courseRepository.save(new Course("MATH 120 — Calculus I", "faculty", 4));
        }

        userRepository.findByUsername("student").ifPresent(studentUser -> {
            Student st = studentRepository.findByUserId(studentUser.getId());
            if (st == null) {
                st = new Student();
                st.setName("Demo Student");
                st.setEmail(studentUser.getEmail());
                st.setDepartment("Computer Science");
                st.setProgram("B.Sc. Software Engineering");
                st.setCurrentSemester(3);
                st.setUser(studentUser);
                st = studentRepository.save(st);
            }
            List<Course> courses = courseRepository.findAll();
            if (!courses.isEmpty()) {
                Course first = courses.get(0);
                if (enrollmentRepository.findByStudent_IdAndCourse_Id(st.getId(), first.getId()).isEmpty()) {
                    enrollmentRepository.save(new Enrollment(st, first, EnrollmentStatus.APPROVED));
                }
            }
        });

        if (calendarEventRepository.count() == 0) {
            CalendarEvent mid = new CalendarEvent();
            mid.setTitle("Midterm examination week");
            mid.setDescription("See faculty syllabi for room assignments.");
            mid.setEventType(CalendarEventType.EXAM);
            mid.setStartDate(LocalDate.now().plusMonths(2).withDayOfMonth(10));
            mid.setEndDate(LocalDate.now().plusMonths(2).withDayOfMonth(14));
            calendarEventRepository.save(mid);

            CalendarEvent holiday = new CalendarEvent();
            holiday.setTitle("Independence Day — campus closed");
            holiday.setEventType(CalendarEventType.HOLIDAY);
            holiday.setStartDate(LocalDate.now().plusMonths(1).withDayOfMonth(1));
            holiday.setEndDate(holiday.getStartDate());
            calendarEventRepository.save(holiday);
        }
    }
}
