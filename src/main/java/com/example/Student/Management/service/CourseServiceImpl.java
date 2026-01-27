package com.example.Student.Management.service;

import com.example.Student.Management.Model.Course;
import com.example.Student.Management.repository.CourseRepository;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class CourseServiceImpl implements CourseService
{
    private final CourseRepository cr;
     public CourseServiceImpl(CourseRepository cr)
     {
         this.cr=cr;
     }
     
     @Override
     public Course saveCourse(Course course){
         return cr.save(course);
     }
     
     @Override
     public List<Course> getAllCourses()
     {
         return cr.findAll();
     }
     
     @Override
     public void deleteCourseById(Long id)
     {
         cr.deleteById(id);
     }
}
