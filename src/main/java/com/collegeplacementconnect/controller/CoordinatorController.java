package com.collegeplacementconnect.controller;

import com.collegeplacementconnect.entity.Application;
import com.collegeplacementconnect.entity.ApplicationStatus;
import com.collegeplacementconnect.entity.PlacementDrive;
import com.collegeplacementconnect.repository.ApplicationRepository;
import com.collegeplacementconnect.repository.PlacementDriveRepository;
import com.collegeplacementconnect.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/coordinator")
public class CoordinatorController {

    @Autowired
    private PlacementDriveRepository driveRepository;
    
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private ApplicationService applicationService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalDrives", driveRepository.count());
        model.addAttribute("totalApplications", applicationRepository.count());
        return "coordinator/dashboard";
    }

    @GetMapping("/placement-drives")
    public String placementDrives(Model model) {
        model.addAttribute("drives", driveRepository.findAll());
        return "coordinator/placement-drives";
    }

    @GetMapping("/placement-drives/new")
    public String newPlacementDriveForm(Model model) {
        model.addAttribute("drive", new PlacementDrive());
        return "coordinator/placement-drive-form";
    }

    @PostMapping("/placement-drives/save")
    public String savePlacementDrive(@ModelAttribute("drive") PlacementDrive drive, RedirectAttributes redirectAttributes) {
        driveRepository.save(drive);
        redirectAttributes.addFlashAttribute("success", "Placement Drive saved successfully!");
        return "redirect:/coordinator/placement-drives";
    }

    @GetMapping("/placement-drives/edit/{id}")
    public String editPlacementDrive(@PathVariable Long id, Model model) {
        PlacementDrive drive = driveRepository.findById(id).orElse(null);
        if (drive != null) {
            model.addAttribute("drive", drive);
            return "coordinator/placement-drive-form";
        }
        return "redirect:/coordinator/placement-drives";
    }
    
    @GetMapping("/placement-drives/delete/{id}")
    public String deletePlacementDrive(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        // Also delete associated applications
        List<Application> applications = applicationRepository.findByPlacementDriveId(id);
        applicationRepository.deleteAll(applications);
        
        driveRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Placement Drive deleted successfully!");
        return "redirect:/coordinator/placement-drives";
    }

    @GetMapping("/applications/{driveId}")
    public String viewApplications(@PathVariable Long driveId, Model model) {
        PlacementDrive drive = driveRepository.findById(driveId).orElse(null);
        if (drive != null) {
            List<Application> applications = applicationRepository.findByPlacementDriveId(driveId);
            model.addAttribute("drive", drive);
            model.addAttribute("applications", applications);
            model.addAttribute("statuses", ApplicationStatus.values());
            return "coordinator/applications";
        }
        return "redirect:/coordinator/placement-drives";
    }

    @PostMapping("/applications/update-status")
    public String updateApplicationStatus(@RequestParam("applicationId") Long applicationId, 
                                          @RequestParam("status") ApplicationStatus status, 
                                          @RequestParam("driveId") Long driveId,
                                          RedirectAttributes redirectAttributes) {
        applicationService.updateApplicationStatus(applicationId, status);
        redirectAttributes.addFlashAttribute("success", "Application status updated.");
        return "redirect:/coordinator/applications/" + driveId;
    }
}
