package com.collegeplacementconnect.service;

import com.collegeplacementconnect.dto.UserRegistrationDto;
import com.collegeplacementconnect.entity.Role;
import com.collegeplacementconnect.entity.Student;
import com.collegeplacementconnect.entity.User;
import com.collegeplacementconnect.repository.StudentRepository;
import com.collegeplacementconnect.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void registerStudent(UserRegistrationDto registrationDto) {
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }
        
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(Role.STUDENT);
        
        Student student = new Student();
        student.setUser(user);
        
        studentRepository.save(student);
    }
}
