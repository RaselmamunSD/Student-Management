package com.example.Student.Management.Controller;

import com.example.Student.Management.Model.Assignment;
import com.example.Student.Management.Model.Course;
import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.EnrollmentStatus;
import com.example.Student.Management.Model.Student;
import com.example.Student.Management.repository.AssignmentRepository;
import com.example.Student.Management.repository.EnrollmentRepository;
import com.example.Student.Management.service.AssignmentPortalService;
import com.example.Student.Management.service.AttendanceService;
import com.example.Student.Management.service.CourseService;
import com.example.Student.Management.service.EnrollmentService;
import com.example.Student.Management.service.StudentService;
import com.example.Student.Management.service.TranscriptPdfService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentDashboardController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentPortalService assignmentPortalService;
    private final AttendanceService attendanceService;
    private final TranscriptPdfService transcriptPdfService;

    public StudentDashboardController(StudentService studentService,
                                        CourseService courseService,
                                        EnrollmentService enrollmentService,
                                        EnrollmentRepository enrollmentRepository,
                                        AssignmentRepository assignmentRepository,
                                        AssignmentPortalService assignmentPortalService,
                                        AttendanceService attendanceService,
                                        TranscriptPdfService transcriptPdfService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.enrollmentRepository = enrollmentRepository;
        this.assignmentRepository = assignmentRepository;
        this.assignmentPortalService = assignmentPortalService;
        this.attendanceService = attendanceService;
        this.transcriptPdfService = transcriptPdfService;
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
            model.addAttribute("courses", List.of());
            return "student/courses";
        }
        model.addAttribute("courses", student.getCourses());
        model.addAttribute("student", student);
        return "student/courses";
    }

    @GetMapping("/catalog")
    public String catalog(Model model, Authentication authentication) {
        Student student = studentService.getStudentByUsername(authentication.getName());
        if (student == null) {
            model.addAttribute("error", "Student profile not found.");
            return "student/catalog";
        }
        List<Course> all = courseService.getAllCourses();
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Course c : all) {
            Map<String, Object> row = new HashMap<>();
            row.put("course", c);
            Optional<Enrollment> en = enrollmentRepository.findByStudent_IdAndCourse_Id(student.getId(), c.getId());
            String status = "OPEN";
            if (en.isPresent()) {
                status = en.get().getStatus().name();
            }
            row.put("status", status);
            rows.add(row);
        }
        model.addAttribute("catalogRows", rows);
        return "student/catalog";
    }

    @PostMapping("/catalog/request")
    public String requestEnrollment(@RequestParam Long courseId, Authentication authentication) {
        Student student = studentService.getStudentByUsername(authentication.getName());
        if (student == null) {
            return "redirect:/student/catalog?error=noprofile";
        }
        enrollmentService.requestEnrollment(student.getId(), courseId);
        return "redirect:/student/catalog?requested=1";
    }

    @GetMapping("/attendance")
    public String attendance(Model model, Authentication authentication) {
        Student student = studentService.getStudentByUsername(authentication.getName());
        if (student == null) {
            model.addAttribute("error", "Student profile not found.");
            return "student/attendance";
        }
        List<Enrollment> approved = student.getEnrollments().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.APPROVED)
                .collect(Collectors.toList());
        Map<Long, Double> pctMap = attendanceService.attendancePercentByEnrollment(approved);
        Map<Long, Boolean> flagMap = new HashMap<>();
        for (Enrollment e : approved) {
            flagMap.put(e.getId(), attendanceService.isBelowThreshold(e.getId(), AttendanceService.DEFAULT_THRESHOLD_PERCENT));
        }
        model.addAttribute("enrollments", approved);
        model.addAttribute("attendancePct", pctMap);
        model.addAttribute("lowAttendance", flagMap);
        model.addAttribute("threshold", AttendanceService.DEFAULT_THRESHOLD_PERCENT);
        return "student/attendance";
    }

    @GetMapping("/assignments")
    public String assignments(Model model, Authentication authentication) {
        Student student = studentService.getStudentByUsername(authentication.getName());
        if (student == null) {
            model.addAttribute("error", "Student profile not found.");
            return "student/assignments";
        }
        List<Long> courseIds = student.getEnrollments().stream()
                .filter(e -> e.getStatus() == EnrollmentStatus.APPROVED)
                .map(e -> e.getCourse().getId())
                .collect(Collectors.toList());
        List<Assignment> list = courseIds.isEmpty()
                ? List.of()
                : assignmentRepository.findByCourse_IdInWithCourseOrderByDueAtAsc(courseIds);
        model.addAttribute("assignments", list);
        model.addAttribute("student", student);
        return "student/assignments";
    }

    @PostMapping("/assignments/{assignmentId}/submit")
    public String submitAssignment(@PathVariable Long assignmentId,
                                   @RequestParam("file") MultipartFile file,
                                   Authentication authentication) throws IOException {
        Student student = studentService.getStudentByUsername(authentication.getName());
        Assignment a = assignmentPortalService.findById(assignmentId).orElseThrow();
        if (student == null || file.isEmpty()) {
            return "redirect:/student/assignments?error=upload";
        }
        assignmentPortalService.submit(a, student, file);
        return "redirect:/student/assignments?submitted=1";
    }

    @GetMapping("/transcript.pdf")
    public ResponseEntity<Resource> downloadTranscript(Authentication authentication) {
        Student student = studentService.getStudentByUsername(authentication.getName());
        if (student == null) {
            return ResponseEntity.notFound().build();
        }
        byte[] pdf = transcriptPdfService.buildTranscript(student);
        ByteArrayResource resource = new ByteArrayResource(pdf);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=transcript.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .contentLength(pdf.length)
                .body(resource);
    }

}
