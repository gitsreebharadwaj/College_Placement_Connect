package com.collegeplacementconnect.repository;

import com.collegeplacementconnect.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    List<Application> findByStudentId(Long studentId);
    List<Application> findByPlacementDriveId(Long driveId);
    boolean existsByStudentIdAndPlacementDriveId(Long studentId, Long driveId);
}
