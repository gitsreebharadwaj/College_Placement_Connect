package com.collegeplacementconnect.controller;

import com.collegeplacementconnect.entity.Application;
import com.collegeplacementconnect.entity.PlacementDrive;
import com.collegeplacementconnect.entity.Student;
import com.collegeplacementconnect.repository.ApplicationRepository;
import com.collegeplacementconnect.repository.PlacementDriveRepository;
import com.collegeplacementconnect.repository.StudentRepository;
import com.collegeplacementconnect.service.ApplicationService;
import com.collegeplacementconnect.service.EligibilityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private PlacementDriveRepository driveRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private EligibilityService eligibilityService;
    
    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication) {
        Student student = studentRepository.findByUserEmail(authentication.getName()).orElse(null);
        model.addAttribute("student", student);
        
        long totalDrives = driveRepository.count();
        long applications = student != null ? applicationRepository.findByStudentId(student.getId()).size() : 0;
        
        model.addAttribute("totalDrives", totalDrives);
        model.addAttribute("totalApplications", applications);
        return "student/dashboard";
    }

    @GetMapping("/profile")
    public String profile(Model model, Authentication authentication) {
        Student student = studentRepository.findByUserEmail(authentication.getName()).orElse(null);
        model.addAttribute("student", student);
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute("student") Student updatedStudent, Authentication authentication, RedirectAttributes redirectAttributes) {
        Student student = studentRepository.findByUserEmail(authentication.getName()).orElse(null);
        if (student != null) {
            // Validation
            if (updatedStudent.getCgpa() != null && (updatedStudent.getCgpa() < 0 || updatedStudent.getCgpa() > 10)) {
                redirectAttributes.addFlashAttribute("error", "CGPA must be between 0 and 10.");
                return "redirect:/student/profile";
            }
            if (updatedStudent.getBacklogs() != null && updatedStudent.getBacklogs() < 0) {
                redirectAttributes.addFlashAttribute("error", "Backlogs cannot be negative.");
                return "redirect:/student/profile";
            }
            if (updatedStudent.getGraduationYear() != null && updatedStudent.getGraduationYear() < 2000) {
                redirectAttributes.addFlashAttribute("error", "Invalid graduation year.");
                return "redirect:/student/profile";
            }

            student.setBranch(updatedStudent.getBranch());
            student.setGraduationYear(updatedStudent.getGraduationYear());
            student.setCgpa(updatedStudent.getCgpa());
            student.setBacklogs(updatedStudent.getBacklogs());
            student.setSkills(updatedStudent.getSkills());
            studentRepository.save(student);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully.");
        }
        return "redirect:/student/profile";
    }

    @GetMapping("/placement-drives")
    public String placementDrives(Model model) {
        List<PlacementDrive> drives = driveRepository.findAll();
        model.addAttribute("drives", drives);
        return "student/placement-drives";
    }

    @GetMapping("/placement-details/{id}")
    public String placementDetails(@PathVariable Long id, Model model, Authentication authentication) {
        PlacementDrive drive = driveRepository.findById(id).orElse(null);
        Student student = studentRepository.findByUserEmail(authentication.getName()).orElse(null);
        
        if (drive == null || student == null) {
            return "redirect:/student/placement-drives";
        }
        
        EligibilityService.EligibilityResult eligibility = eligibilityService.checkEligibility(student, drive);
        boolean hasApplied = applicationRepository.existsByStudentIdAndPlacementDriveId(student.getId(), drive.getId());
        
        model.addAttribute("drive", drive);
        model.addAttribute("eligibility", eligibility);
        model.addAttribute("hasApplied", hasApplied);
        return "student/placement-details";
    }

    @PostMapping("/apply/{driveId}")
    public String applyForDrive(@PathVariable Long driveId, Authentication authentication, RedirectAttributes redirectAttributes) {
        try {
            applicationService.applyForDrive(authentication.getName(), driveId);
            redirectAttributes.addFlashAttribute("success", "Successfully applied for the placement drive!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/student/placement-details/" + driveId;
    }

    @GetMapping("/applications")
    public String applications(Model model, Authentication authentication) {
        Student student = studentRepository.findByUserEmail(authentication.getName()).orElse(null);
        if (student != null) {
            List<Application> applications = applicationRepository.findByStudentId(student.getId());
            model.addAttribute("applications", applications);
        }
        return "student/applications";
    }
}
