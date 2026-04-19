package com.example.Student.Management.service;

import com.example.Student.Management.Model.Course;
import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.EnrollmentStatus;
import com.example.Student.Management.Model.Student;
import com.example.Student.Management.repository.CourseRepository;
import com.example.Student.Management.repository.EnrollmentRepository;
import com.example.Student.Management.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class EnrollmentServiceImpl implements EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentServiceImpl(EnrollmentRepository enrollmentRepository,
                                 StudentRepository studentRepository,
                                 CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Override
    public void assignCoursesToStudent(Long studentId, List<Long> courseIds) {
        Student student = studentRepository.findById(studentId).orElse(null);
        if (student == null) {
            return;
        }
        Set<Long> wanted = new HashSet<>(courseIds);
        List<Enrollment> existing = new ArrayList<>(enrollmentRepository.findByStudent_Id(studentId));
        for (Enrollment e : existing) {
            Long cid = e.getCourse().getId();
            if (!wanted.contains(cid)) {
                enrollmentRepository.delete(e);
            }
        }
        for (Long cid : wanted) {
            Course c = courseRepository.findById(cid).orElse(null);
            if (c == null) {
                continue;
            }
            Optional<Enrollment> opt = enrollmentRepository.findByStudent_IdAndCourse_Id(studentId, cid);
            if (opt.isPresent()) {
                Enrollment e = opt.get();
                e.setStatus(EnrollmentStatus.APPROVED);
                enrollmentRepository.save(e);
            } else {
                Enrollment en = new Enrollment(student, c, EnrollmentStatus.APPROVED);
                enrollmentRepository.save(en);
            }
        }
    }

    @Override
    public void requestEnrollment(Long studentId, Long courseId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        Course course = courseRepository.findById(courseId).orElseThrow();
        Optional<Enrollment> opt = enrollmentRepository.findByStudent_IdAndCourse_Id(studentId, courseId);
        if (opt.isPresent()) {
            Enrollment e = opt.get();
            if (e.getStatus() == EnrollmentStatus.APPROVED) {
                return;
            }
            if (e.getStatus() == EnrollmentStatus.PENDING) {
                return;
            }
            e.setStatus(EnrollmentStatus.PENDING);
            e.setRequestedAt(Instant.now());
            enrollmentRepository.save(e);
            return;
        }
        enrollmentRepository.save(new Enrollment(student, course, EnrollmentStatus.PENDING));
    }

    @Override
    public List<Enrollment> listPending() {
        return enrollmentRepository.findByStatusWithDetails(EnrollmentStatus.PENDING);
    }

    @Override
    public void setEnrollmentStatus(Long enrollmentId, EnrollmentStatus status) {
        Enrollment e = enrollmentRepository.findById(enrollmentId).orElseThrow();
        e.setStatus(status);
        enrollmentRepository.save(e);
    }

    @Override
    public void updateMarks(Long enrollmentId, Long courseId, Double marks, String semester) {
        Enrollment e = enrollmentRepository.findById(enrollmentId).orElseThrow();
        if (!e.getCourse().getId().equals(courseId)) {
            throw new IllegalArgumentException("Enrollment does not belong to course");
        }
        e.setMarks(marks);
        if (semester != null && !semester.isBlank()) {
            e.setSemester(semester);
        }
        enrollmentRepository.save(e);
    }

    @Override
    public List<Enrollment> listApprovedForCourse(Long courseId) {
        return enrollmentRepository.findByCourseWithStudents(courseId, EnrollmentStatus.APPROVED);
    }
}
