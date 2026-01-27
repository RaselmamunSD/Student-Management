package com.example.Student.Management.Controller;

import com.example.Student.Management.Model.Student;
import com.example.Student.Management.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;

@Controller
@RequestMapping("/students")
public class StudentController 
{
    private final StudentService ss;
    public StudentController(StudentService ss)
    {
        this.ss=ss;
    }
    
    @GetMapping
    public String listStudents(Model model)
    {
        model.addAttribute("students",ss.getAllStudents());
        return "students";
    }
    
    @GetMapping("/new")
    public String showStudentForm(Model model)
    {
        model.addAttribute("student", new Student());
        return "student-form";
    }
    
    @PostMapping
    public String saveStudent(@ModelAttribute("student") Student student)
    {
        ss.saveStudent(student);
        return "redirect:/students";
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model)
    {
        Student student=ss.getStudentById(id);
        model.addAttribute("student",student);
        return "student-form";
    }
    
    @GetMapping("/delete/{id}")
    public String deleteStudent(@PathVariable Long id)
    {
        ss.deleteStudentById(id);
        return "redirect:/students";
    }
    
}
