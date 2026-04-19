package com.example.Student.Management.repository;

import com.example.Student.Management.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

    List<Course> findByFacultyIgnoreCase(String faculty);
}
