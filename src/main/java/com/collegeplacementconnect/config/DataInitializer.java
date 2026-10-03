package com.collegeplacementconnect.config;

import com.collegeplacementconnect.entity.*;
import com.collegeplacementconnect.repository.PlacementDriveRepository;
import com.collegeplacementconnect.repository.StudentRepository;
import com.collegeplacementconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private PlacementDriveRepository driveRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (!userRepository.existsByEmail("coordinator@college.com")) {
            User coord = new User();
            coord.setName("Demo Coordinator");
            coord.setEmail("coordinator@college.com");
            coord.setPassword(passwordEncoder.encode("Coordinator@123"));
            coord.setRole(Role.PLACEMENT_COORDINATOR);
            userRepository.save(coord);
        }

        if (!userRepository.existsByEmail("student@college.com")) {
            User user = new User();
            user.setName("Demo Student");
            user.setEmail("student@college.com");
            user.setPassword(passwordEncoder.encode("Student@123"));
            user.setRole(Role.STUDENT);

            Student student = new Student();
            student.setUser(user);
            student.setBranch("Computer Science");
            student.setGraduationYear(2024);
            student.setCgpa(8.5);
            student.setBacklogs(0);
            student.setSkills("Java, Spring Boot, MySQL");
            
            studentRepository.save(student);
        }
        
        if (driveRepository.count() == 0) {
            PlacementDrive infosys = new PlacementDrive();
            infosys.setCompanyName("Infosys");
            infosys.setJobRole("System Engineer");
            infosys.setDescription("Looking for freshers for SE role.");
            infosys.setCtc("3.6 LPA");
            infosys.setLocation("Bangalore");
            infosys.setEligibleBranches("Computer Science, Information Technology, Electronics");
            infosys.setMinCgpa(6.5);
            infosys.setMaxBacklogs(0);
            infosys.setRequiredGraduationYear(2024);
            infosys.setApplicationDeadline(LocalDate.now().plusDays(10));
            infosys.setSelectionProcess("Aptitude Test -> Technical Interview -> HR Interview");
            driveRepository.save(infosys);
            
            PlacementDrive tcs = new PlacementDrive();
            tcs.setCompanyName("TCS");
            tcs.setJobRole("Digital Cadre");
            tcs.setDescription("Hiring top talent for digital roles.");
            tcs.setCtc("7.0 LPA");
            tcs.setLocation("Pan India");
            tcs.setEligibleBranches("Computer Science, Information Technology");
            tcs.setMinCgpa(7.5);
            tcs.setMaxBacklogs(0);
            tcs.setRequiredGraduationYear(2024);
            tcs.setApplicationDeadline(LocalDate.now().plusDays(15));
            tcs.setSelectionProcess("TCS NQT -> Technical Interview -> HR Interview");
            driveRepository.save(tcs);
            
            PlacementDrive wipro = new PlacementDrive();
            wipro.setCompanyName("Wipro");
            wipro.setJobRole("Project Engineer");
            wipro.setDescription("Elite hiring for product development.");
            wipro.setCtc("6.5 LPA");
            wipro.setLocation("Hyderabad");
            wipro.setEligibleBranches("Computer Science");
            wipro.setMinCgpa(8.0);
            wipro.setMaxBacklogs(0);
            wipro.setRequiredGraduationYear(2024);
            wipro.setApplicationDeadline(LocalDate.now().minusDays(1)); // expired
            wipro.setSelectionProcess("Coding Round -> Interview");
            driveRepository.save(wipro);
        }
    }
}
