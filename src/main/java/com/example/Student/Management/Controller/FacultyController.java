package com.example.Student.Management.Controller;
import com.example.Student.Management.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/faculty")
public class FacultyController 
{
    private final StudentService ss;
    public FacultyController(StudentService ss)
    {
        this.ss=ss;
    }
    
    @GetMapping("/students")
    public String viewStudents(Model model)
    {
        model.addAttribute("students", ss.getAllStudents());
        return "faculty/students";
    }
    
    @GetMapping("/grades")
    public String manageGrades()
    {
        return "faculty/grades";
    }
    
    @GetMapping("/dashboard")
    public String getDashboard()
    {
        return "dashboard";
    }
}
