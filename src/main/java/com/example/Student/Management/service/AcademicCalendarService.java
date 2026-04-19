package com.example.Student.Management.service;

import com.example.Student.Management.Model.CalendarEvent;
import com.example.Student.Management.Model.TimetableEntry;
import com.example.Student.Management.repository.CalendarEventRepository;
import com.example.Student.Management.repository.TimetableEntryRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class AcademicCalendarService {

    private final CalendarEventRepository calendarEventRepository;
    private final TimetableEntryRepository timetableEntryRepository;

    public AcademicCalendarService(CalendarEventRepository calendarEventRepository,
                                   TimetableEntryRepository timetableEntryRepository) {
        this.calendarEventRepository = calendarEventRepository;
        this.timetableEntryRepository = timetableEntryRepository;
    }

    public List<CalendarEvent> listEvents() {
        return calendarEventRepository.findAllByOrderByStartDateAsc();
    }

    public void saveEvent(CalendarEvent event) {
        calendarEventRepository.save(event);
    }

    public void deleteEvent(Long id) {
        calendarEventRepository.deleteById(id);
    }

    public List<TimetableEntry> listTimetableForCourseIds(List<Long> courseIds) {
        if (courseIds == null || courseIds.isEmpty()) {
            return Collections.emptyList();
        }
        return timetableEntryRepository.findByCourse_IdInOrderByDayOfWeekAscStartTimeAsc(courseIds);
    }

    public List<TimetableEntry> listTimetableForCourse(Long courseId) {
        return timetableEntryRepository.findByCourse_IdOrderByDayOfWeekAscStartTimeAsc(courseId);
    }

    public void saveTimetableEntry(TimetableEntry entry) {
        timetableEntryRepository.save(entry);
    }

    public void deleteTimetableEntry(Long id) {
        timetableEntryRepository.deleteById(id);
    }
}
