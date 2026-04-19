package com.example.Student.Management.repository;

import com.example.Student.Management.Model.AttendanceRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRecordRepository extends JpaRepository<AttendanceRecord, Long> {

    List<AttendanceRecord> findByEnrollment_IdOrderByClassDateDesc(Long enrollmentId);

    Optional<AttendanceRecord> findByEnrollment_IdAndClassDate(Long enrollmentId, LocalDate classDate);

    long countByEnrollment_IdAndPresentTrue(Long enrollmentId);

    long countByEnrollment_Id(Long enrollmentId);
}
