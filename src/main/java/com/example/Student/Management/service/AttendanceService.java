package com.example.Student.Management.service;

import com.example.Student.Management.Model.AttendanceRecord;
import com.example.Student.Management.Model.Enrollment;
import com.example.Student.Management.Model.EnrollmentStatus;
import com.example.Student.Management.repository.AttendanceRecordRepository;
import com.example.Student.Management.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class AttendanceService {

    public static final double DEFAULT_THRESHOLD_PERCENT = 75.0;

    private final AttendanceRecordRepository attendanceRecordRepository;
    private final EnrollmentRepository enrollmentRepository;

    public AttendanceService(AttendanceRecordRepository attendanceRecordRepository,
                             EnrollmentRepository enrollmentRepository) {
        this.attendanceRecordRepository = attendanceRecordRepository;
        this.enrollmentRepository = enrollmentRepository;
    }

    public void saveAttendanceForClass(Long courseId, LocalDate classDate, Map<Long, Boolean> enrollmentIdToPresent) {
        for (Map.Entry<Long, Boolean> e : enrollmentIdToPresent.entrySet()) {
            Optional<Enrollment> enOpt = enrollmentRepository.findById(e.getKey());
            if (enOpt.isEmpty()) {
                continue;
            }
            Enrollment en = enOpt.get();
            if (!en.getCourse().getId().equals(courseId) || en.getStatus() != EnrollmentStatus.APPROVED) {
                continue;
            }
            boolean present = Boolean.TRUE.equals(e.getValue());
            AttendanceRecord rec = attendanceRecordRepository
                    .findByEnrollment_IdAndClassDate(e.getKey(), classDate)
                    .orElseGet(() -> new AttendanceRecord(en, classDate, present));
            rec.setEnrollment(en);
            rec.setClassDate(classDate);
            rec.setPresent(present);
            attendanceRecordRepository.save(rec);
        }
    }

    public Map<Long, Double> attendancePercentByEnrollment(List<Enrollment> enrollments) {
        Map<Long, Double> map = new HashMap<>();
        for (Enrollment e : enrollments) {
            long total = attendanceRecordRepository.countByEnrollment_Id(e.getId());
            if (total == 0) {
                map.put(e.getId(), 100.0);
            } else {
                long present = attendanceRecordRepository.countByEnrollment_IdAndPresentTrue(e.getId());
                map.put(e.getId(), Math.round((present * 10000.0 / total)) / 100.0);
            }
        }
        return map;
    }

    public boolean isBelowThreshold(Long enrollmentId, double threshold) {
        long total = attendanceRecordRepository.countByEnrollment_Id(enrollmentId);
        if (total == 0) {
            return false;
        }
        long present = attendanceRecordRepository.countByEnrollment_IdAndPresentTrue(enrollmentId);
        double pct = present * 100.0 / total;
        return pct < threshold;
    }

    public List<AttendanceRecord> listForEnrollment(Long enrollmentId) {
        return attendanceRecordRepository.findByEnrollment_IdOrderByClassDateDesc(enrollmentId);
    }
}
