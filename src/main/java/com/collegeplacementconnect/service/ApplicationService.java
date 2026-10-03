package com.collegeplacementconnect.service;

import com.collegeplacementconnect.entity.Application;
import com.collegeplacementconnect.entity.ApplicationStatus;
import com.collegeplacementconnect.entity.PlacementDrive;
import com.collegeplacementconnect.entity.Student;
import com.collegeplacementconnect.exception.DuplicateApplicationException;
import com.collegeplacementconnect.exception.NotEligibleException;
import com.collegeplacementconnect.repository.ApplicationRepository;
import com.collegeplacementconnect.repository.PlacementDriveRepository;
import com.collegeplacementconnect.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ApplicationService {
    @Autowired
    private ApplicationRepository applicationRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private PlacementDriveRepository driveRepository;
    
    @Autowired
    private EligibilityService eligibilityService;

    @Transactional
    public void applyForDrive(String email, Long driveId) {
        Student student = studentRepository.findByUserEmail(email)
                .orElseThrow(() -> new RuntimeException("Student not found"));
                
        PlacementDrive drive = driveRepository.findById(driveId)
                .orElseThrow(() -> new RuntimeException("Drive not found"));
                
        if (applicationRepository.existsByStudentIdAndPlacementDriveId(student.getId(), drive.getId())) {
            throw new DuplicateApplicationException("You have already applied for this drive.");
        }
        
        EligibilityService.EligibilityResult result = eligibilityService.checkEligibility(student, drive);
        if (!result.isEligible()) {
            throw new NotEligibleException("Not eligible: " + result.getReason());
        }
        
        Application application = new Application();
        application.setStudent(student);
        application.setPlacementDrive(drive);
        
        applicationRepository.save(application);
    }
    
    @Transactional
    public void updateApplicationStatus(Long applicationId, ApplicationStatus status) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        application.setStatus(status);
        applicationRepository.save(application);
    }
}
