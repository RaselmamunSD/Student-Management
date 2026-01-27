package com.example.Student.Management.service;

import com.example.Student.Management.Model.Course;
import java.util.*;

public interface CourseService 
{
    Course saveCourse(Course course);
    List<Course> getAllCourses();
    void deleteCourseById(Long id);
}
