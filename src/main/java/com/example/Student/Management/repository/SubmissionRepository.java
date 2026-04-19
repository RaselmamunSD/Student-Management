package com.example.Student.Management.repository;

import com.example.Student.Management.Model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {

    Optional<Submission> findByAssignment_IdAndStudent_Id(Long assignmentId, Long studentId);

    @Query("SELECT DISTINCT s FROM Submission s JOIN FETCH s.student WHERE s.assignment.id = :aid ORDER BY s.submittedAt DESC")
    List<Submission> findByAssignment_IdOrderBySubmittedAtDesc(@Param("aid") Long assignmentId);
}
