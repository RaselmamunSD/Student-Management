package com.example.Student.Management.service;

import com.example.Student.Management.Model.Assignment;
import com.example.Student.Management.Model.Course;
import com.example.Student.Management.Model.Student;
import com.example.Student.Management.Model.Submission;
import com.example.Student.Management.repository.AssignmentRepository;
import com.example.Student.Management.repository.SubmissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssignmentPortalService {

    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final FileStorageService fileStorageService;

    public AssignmentPortalService(AssignmentRepository assignmentRepository,
                                   SubmissionRepository submissionRepository,
                                   FileStorageService fileStorageService) {
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.fileStorageService = fileStorageService;
    }

    public Assignment createAssignment(Course course, String title, String description, Instant dueAt, String facultyUsername) {
        Assignment a = new Assignment();
        a.setCourse(course);
        a.setTitle(title);
        a.setDescription(description);
        a.setDueAt(dueAt);
        a.setFacultyUsername(facultyUsername);
        a.setCreatedAt(Instant.now());
        return assignmentRepository.save(a);
    }

    public List<Assignment> listForFaculty(String facultyUsername) {
        return assignmentRepository.findByFacultyUsernameWithCourseOrderByDueAtAsc(facultyUsername);
    }

    public List<Assignment> listForCourse(Long courseId) {
        return assignmentRepository.findByCourse_IdOrderByDueAtAsc(courseId);
    }

    public Optional<Assignment> findById(Long id) {
        return assignmentRepository.findById(id);
    }

    public void deleteAssignment(Long id) {
        assignmentRepository.findById(id).ifPresent(a -> {
            submissionRepository.deleteAll(submissionRepository.findByAssignment_IdOrderBySubmittedAtDesc(id));
            assignmentRepository.delete(a);
        });
    }

    public Submission submit(Assignment assignment, Student student, MultipartFile file) throws IOException {
        Optional<Submission> existing = submissionRepository.findByAssignment_IdAndStudent_Id(assignment.getId(), student.getId());
        String stored = fileStorageService.storeSubmissionFile(file);
        Submission s = existing.orElseGet(Submission::new);
        s.setAssignment(assignment);
        s.setStudent(student);
        s.setStoredFileName(stored);
        s.setOriginalFileName(file.getOriginalFilename() != null ? file.getOriginalFilename() : stored);
        s.setSubmittedAt(Instant.now());
        return submissionRepository.save(s);
    }

    public Optional<Submission> findSubmission(Long assignmentId, Long studentId) {
        return submissionRepository.findByAssignment_IdAndStudent_Id(assignmentId, studentId);
    }

    public List<Submission> listSubmissions(Long assignmentId) {
        return submissionRepository.findByAssignment_IdOrderBySubmittedAtDesc(assignmentId);
    }

    public void gradeSubmission(Long submissionId, Double grade) {
        Submission s = submissionRepository.findById(submissionId).orElseThrow();
        s.setGrade(grade);
        submissionRepository.save(s);
    }
}
