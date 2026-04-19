package com.example.Student.Management.service;

import com.example.Student.Management.Model.Course;
import java.util.*;

public interface CourseService {
    Course saveCourse(Course course);

    List<Course> getAllCourses();

    Course getCourseById(Long id);

    List<Course> getCoursesByFacultyUsername(String facultyUsername);

    void deleteCourseById(Long id);
}
