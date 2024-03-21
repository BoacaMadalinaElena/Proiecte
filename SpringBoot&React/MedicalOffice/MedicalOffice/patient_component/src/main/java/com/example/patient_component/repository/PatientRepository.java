package com.example.patient_component.repository;

import com.example.patient_component.dto.PatientDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface PatientRepository extends JpaRepository<PatientDTO,Long> {
    @Modifying
    @Transactional
    @Query("UPDATE PatientDTO p SET p.is_active = false WHERE p.cnp = :cnp")
    void deactivateByCnp(@Param("cnp") String cnp);

    Optional<PatientDTO> findByCnp(String cnp);

    @Query("SELECT p FROM PatientDTO p WHERE p.is_active = true")
    List<PatientDTO> findByIsActiveTrue();

    @Query("SELECT p FROM PatientDTO p WHERE p.user_id = :id")
    Optional<PatientDTO> findById(@Param("id") Long id);
}
