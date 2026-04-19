package com.example.Student.Management.repository;

import com.example.Student.Management.Model.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry, Long> {

    @Query("SELECT DISTINCT t FROM TimetableEntry t JOIN FETCH t.course WHERE t.course.id = :courseId ORDER BY t.dayOfWeek ASC, t.startTime ASC")
    List<TimetableEntry> findByCourse_IdOrderByDayOfWeekAscStartTimeAsc(@Param("courseId") Long courseId);

    @Query("SELECT DISTINCT t FROM TimetableEntry t JOIN FETCH t.course WHERE t.course.id IN :ids ORDER BY t.dayOfWeek ASC, t.startTime ASC")
    List<TimetableEntry> findByCourse_IdInOrderByDayOfWeekAscStartTimeAsc(@Param("ids") Collection<Long> courseIds);
}
