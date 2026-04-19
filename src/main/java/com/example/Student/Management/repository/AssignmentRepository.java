package com.example.Student.Management.repository;

import com.example.Student.Management.Model.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findByCourse_IdOrderByDueAtAsc(Long courseId);

    @Query("SELECT DISTINCT a FROM Assignment a JOIN FETCH a.course WHERE LOWER(a.facultyUsername) = LOWER(:u) ORDER BY a.dueAt ASC")
    List<Assignment> findByFacultyUsernameWithCourseOrderByDueAtAsc(@Param("u") String facultyUsername);

    @Query("SELECT DISTINCT a FROM Assignment a JOIN FETCH a.course WHERE a.course.id IN :ids ORDER BY a.dueAt ASC")
    List<Assignment> findByCourse_IdInWithCourseOrderByDueAtAsc(@Param("ids") Collection<Long> courseIds);
}
