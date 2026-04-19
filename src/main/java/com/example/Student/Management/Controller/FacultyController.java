package com.example.Student.Management.Controller;

import com.example.Student.Management.Model.Assignment;
import com.example.Student.Management.Model.Course;
import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.Submission;
import com.example.Student.Management.Model.TimetableEntry;
import com.example.Student.Management.service.AssignmentPortalService;
import com.example.Student.Management.service.AttendanceService;
import com.example.Student.Management.service.CourseService;
import com.example.Student.Management.service.EnrollmentService;
import com.example.Student.Management.service.AcademicCalendarService;
import com.example.Student.Management.service.StudentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.core.Authentication;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/faculty")
public class FacultyController {

    private final StudentService studentService;
    private final CourseService courseService;
    private final EnrollmentService enrollmentService;
    private final AttendanceService attendanceService;
    private final AssignmentPortalService assignmentPortalService;
    private final AcademicCalendarService academicCalendarService;

    public FacultyController(StudentService studentService,
                               CourseService courseService,
                               EnrollmentService enrollmentService,
                               AttendanceService attendanceService,
                               AssignmentPortalService assignmentPortalService,
                               AcademicCalendarService academicCalendarService) {
        this.studentService = studentService;
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
        this.attendanceService = attendanceService;
        this.assignmentPortalService = assignmentPortalService;
        this.academicCalendarService = academicCalendarService;
    }

    private boolean ownsCourse(Authentication auth, Long courseId) {
        Course c = courseService.getCourseById(courseId);
        return c != null && c.getFaculty() != null
                && c.getFaculty().equalsIgnoreCase(auth.getName());
    }

    @GetMapping("/students")
    public String viewStudents(Model model) {
        model.addAttribute("students", studentService.getAllStudents());
        return "faculty/students";
    }

    @GetMapping("/grades")
    public String manageGrades(Model model, Authentication authentication) {
        model.addAttribute("courses", courseService.getCoursesByFacultyUsername(authentication.getName()));
        return "faculty/grades";
    }

    @GetMapping("/grades/course/{courseId}")
    public String gradeCourse(@PathVariable Long courseId, Model model, Authentication authentication) {
        if (!ownsCourse(authentication, courseId)) {
            return "redirect:/faculty/grades?error=forbidden";
        }
        List<Enrollment> enrollments = enrollmentService.listApprovedForCourse(courseId);
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("enrollments", enrollments);
        return "faculty/grade-course";
    }

    @PostMapping("/grades/course/{courseId}")
    public String saveGrades(@PathVariable Long courseId,
                             @RequestParam(required = false) String semester,
                             @RequestParam Map<String, String> allParams,
                             Authentication authentication) {
        if (!ownsCourse(authentication, courseId)) {
            return "redirect:/faculty/grades?error=forbidden";
        }
        for (Map.Entry<String, String> e : allParams.entrySet()) {
            if (!e.getKey().startsWith("marks_")) {
                continue;
            }
            String idStr = e.getKey().substring("marks_".length());
            try {
                Long enrollmentId = Long.parseLong(idStr);
                String raw = e.getValue();
                if (raw == null || raw.isBlank()) {
                    continue;
                }
                double marks = Double.parseDouble(raw.trim());
                enrollmentService.updateMarks(enrollmentId, courseId, marks, semester);
            } catch (NumberFormatException ignored) {
                // skip invalid
            }
        }
        return "redirect:/faculty/grades/course/" + courseId + "?saved=1";
    }

    @GetMapping("/attendance/course/{courseId}")
    public String attendanceForm(@PathVariable Long courseId,
                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate classDate,
                                 Model model,
                                 Authentication authentication) {
        if (!ownsCourse(authentication, courseId)) {
            return "redirect:/faculty/grades?error=forbidden";
        }
        LocalDate date = classDate != null ? classDate : LocalDate.now();
        List<Enrollment> enrollments = enrollmentService.listApprovedForCourse(courseId);
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("classDate", date);
        return "faculty/attendance-course";
    }

    @PostMapping("/attendance/course/{courseId}")
    public String saveAttendance(@PathVariable Long courseId,
                                 @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate classDate,
                                 @RequestParam(required = false) List<Long> presentEnrollmentIds,
                                 Authentication authentication) {
        if (!ownsCourse(authentication, courseId)) {
            return "redirect:/faculty/grades?error=forbidden";
        }
        List<Enrollment> enrollments = enrollmentService.listApprovedForCourse(courseId);
        Map<Long, Boolean> map = new HashMap<>();
        for (Enrollment en : enrollments) {
            boolean present = presentEnrollmentIds != null && presentEnrollmentIds.contains(en.getId());
            map.put(en.getId(), present);
        }
        attendanceService.saveAttendanceForClass(courseId, classDate, map);
        return "redirect:/faculty/attendance/course/" + courseId + "?classDate=" + classDate + "&saved=1";
    }

    @GetMapping("/assignments")
    public String listAssignments(Model model, Authentication authentication) {
        model.addAttribute("assignments", assignmentPortalService.listForFaculty(authentication.getName()));
        model.addAttribute("courses", courseService.getCoursesByFacultyUsername(authentication.getName()));
        return "faculty/assignments";
    }

    @PostMapping("/assignments")
    public String createAssignment(@RequestParam Long courseId,
                                   @RequestParam String title,
                                   @RequestParam(required = false) String description,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueAt,
                                   Authentication authentication) {
        if (!ownsCourse(authentication, courseId)) {
            return "redirect:/faculty/assignments?error=forbidden";
        }
        Course c = courseService.getCourseById(courseId);
        assignmentPortalService.createAssignment(c, title, description,
                dueAt.atZone(ZoneId.systemDefault()).toInstant(), authentication.getName());
        return "redirect:/faculty/assignments?created=1";
    }

    @GetMapping("/assignments/{assignmentId}/submissions")
    public String gradeSubmissions(@PathVariable Long assignmentId, Model model, Authentication authentication) {
        Assignment a = assignmentPortalService.findById(assignmentId).orElse(null);
        if (a == null || !a.getFacultyUsername().equalsIgnoreCase(authentication.getName())) {
            return "redirect:/faculty/assignments?error=forbidden";
        }
        model.addAttribute("assignment", a);
        model.addAttribute("submissions", assignmentPortalService.listSubmissions(assignmentId));
        return "faculty/assignment-submissions";
    }

    @PostMapping("/submissions/{submissionId}/grade")
    public String gradeSubmission(@PathVariable Long submissionId,
                                  @RequestParam Double grade,
                                  @RequestParam Long assignmentId) {
        assignmentPortalService.gradeSubmission(submissionId, grade);
        return "redirect:/faculty/assignments/" + assignmentId + "/submissions?graded=1";
    }

    @GetMapping("/timetable/course/{courseId}")
    public String timetable(@PathVariable Long courseId, Model model, Authentication authentication) {
        if (!ownsCourse(authentication, courseId)) {
            return "redirect:/faculty/grades?error=forbidden";
        }
        model.addAttribute("course", courseService.getCourseById(courseId));
        model.addAttribute("entries", academicCalendarService.listTimetableForCourse(courseId));
        return "faculty/timetable-course";
    }

    @PostMapping("/timetable/course/{courseId}")
    public String addTimetableSlot(@PathVariable Long courseId,
                                   @RequestParam String dayOfWeek,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime startTime,
                                   @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime endTime,
                                   @RequestParam(required = false) String room,
                                   @RequestParam(required = false) String semesterLabel,
                                   Authentication authentication) {
        if (!ownsCourse(authentication, courseId)) {
            return "redirect:/faculty/grades?error=forbidden";
        }
        Course c = courseService.getCourseById(courseId);
        TimetableEntry t = new TimetableEntry();
        t.setCourse(c);
        t.setDayOfWeek(dayOfWeek);
        t.setStartTime(startTime);
        t.setEndTime(endTime);
        t.setRoom(room);
        t.setSemesterLabel(semesterLabel);
        academicCalendarService.saveTimetableEntry(t);
        return "redirect:/faculty/timetable/course/" + courseId + "?saved=1";
    }

    @PostMapping("/timetable/delete/{entryId}")
    public String deleteTimetable(@PathVariable Long entryId,
                                  @RequestParam Long courseId,
                                  Authentication authentication) {
        if (!ownsCourse(authentication, courseId)) {
            return "redirect:/faculty/grades?error=forbidden";
        }
        academicCalendarService.deleteTimetableEntry(entryId);
        return "redirect:/faculty/timetable/course/" + courseId;
    }

    @GetMapping("/dashboard")
    public String getDashboard() {
        return "dashboard";
    }
}
