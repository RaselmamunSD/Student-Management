package com.example.Student.Management.service;

import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.EnrollmentStatus;

import java.util.List;

public interface EnrollmentService {

    void assignCoursesToStudent(Long studentId, List<Long> courseIds);

    void requestEnrollment(Long studentId, Long courseId);

    List<Enrollment> listPending();

    void setEnrollmentStatus(Long enrollmentId, EnrollmentStatus status);

    void updateMarks(Long enrollmentId, Long courseId, Double marks, String semester);

    List<Enrollment> listApprovedForCourse(Long courseId);
}
