package com.example.physician_component.repository;

import com.example.physician_component.dto.PhysiciansDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public interface PhysiciansRepository extends JpaRepository<PhysiciansDTO, Long> {
    @Query(value = "SELECT * FROM physician WHERE specialization = :specialization ", nativeQuery = true)
    List<PhysiciansDTO> selectPhysiciansBySpecialization(@Param("specialization") String specialization);

    @Query(value = "SELECT * FROM physician WHERE id_user = :id_user ", nativeQuery = true)
    Optional<PhysiciansDTO> selectPhysiciansByIdUser(@Param("id_user") Long id_user);

}
