package com.collegeplacementconnect.service;

import com.collegeplacementconnect.entity.PlacementDrive;
import com.collegeplacementconnect.entity.Student;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.Arrays;

@Service
public class EligibilityService {
    
    public EligibilityResult checkEligibility(Student student, PlacementDrive drive) {
        if (student.getBranch() == null || student.getCgpa() == null || 
            student.getBacklogs() == null || student.getGraduationYear() == null) {
            return new EligibilityResult(false, "Student profile is incomplete.");
        }
        
        if (drive.getApplicationDeadline().isBefore(LocalDate.now())) {
            return new EligibilityResult(false, "Application deadline has passed.");
        }
        
        if (drive.getRequiredGraduationYear() != null && !drive.getRequiredGraduationYear().equals(student.getGraduationYear())) {
            return new EligibilityResult(false, "Graduation year does not match.");
        }
        
        if (drive.getMinCgpa() != null && student.getCgpa() < drive.getMinCgpa()) {
            return new EligibilityResult(false, "CGPA is below the required minimum.");
        }
        
        if (drive.getMaxBacklogs() != null && student.getBacklogs() > drive.getMaxBacklogs()) {
            return new EligibilityResult(false, "Number of backlogs exceeds the allowed maximum.");
        }
        
        if (drive.getEligibleBranches() != null && !drive.getEligibleBranches().isEmpty()) {
            boolean branchMatch = Arrays.stream(drive.getEligibleBranches().split(","))
                    .map(String::trim)
                    .anyMatch(b -> b.equalsIgnoreCase(student.getBranch()));
            if (!branchMatch) {
                return new EligibilityResult(false, "Branch is not eligible.");
            }
        }
        
        return new EligibilityResult(true, "ELIGIBLE");
    }

    public static class EligibilityResult {
        private final boolean eligible;
        private final String reason;

        public EligibilityResult(boolean eligible, String reason) {
            this.eligible = eligible;
            this.reason = reason;
        }

        public boolean isEligible() { return eligible; }
        public String getReason() { return reason; }
    }
}
