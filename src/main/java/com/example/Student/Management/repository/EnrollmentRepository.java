package com.example.Student.Management.repository;

import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.EnrollmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    Optional<Enrollment> findByStudent_IdAndCourse_Id(Long studentId, Long courseId);

    List<Enrollment> findByStudent_Id(Long studentId);

    List<Enrollment> findByCourse_Id(Long courseId);

    List<Enrollment> findByStatus(EnrollmentStatus status);

    @Query("SELECT DISTINCT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.course WHERE e.status = :status")
    List<Enrollment> findByStatusWithDetails(@Param("status") EnrollmentStatus status);

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.student JOIN FETCH e.course WHERE e.course.id = :courseId AND e.status = :status")
    List<Enrollment> findByCourseWithStudents(@Param("courseId") Long courseId, @Param("status") EnrollmentStatus status);
}
