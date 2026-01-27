package com.example.Student.Management.repository;

import com.example.Student.Management.Model.Course;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CourseRepository extends JpaRepository<Course, Long>
{
    
}
