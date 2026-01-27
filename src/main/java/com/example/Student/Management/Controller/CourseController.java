package com.example.Student.Management.Controller;

import com.example.Student.Management.Model.Course;
import com.example.Student.Management.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequestMapping("/courses")
public class CourseController 
{
    private final CourseService cs;
    public CourseController(CourseService cs)
    {
        this.cs=cs;
    }
    
    @GetMapping
    public String listCourses(Model model){
        model.addAttribute("courses", cs.getAllCourses());
        return "courses";
    }
    
    @GetMapping("/new")
    public String showCourseForm(Model model)
    {
        model.addAttribute("course", new Course());
        return "course-form";
    }
    
    @PostMapping
    public String saveCourse(@ModelAttribute("course") Course course)
    {
        cs.saveCourse(course);
        return "redirect:/courses";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteCourse(@PathVariable Long id)
    {
        cs.deleteCourseById(id);
        return "redirect:/courses";
    }
}
