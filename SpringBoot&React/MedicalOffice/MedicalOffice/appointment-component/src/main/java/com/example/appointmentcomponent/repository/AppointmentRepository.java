package com.example.appointmentcomponent.repository;

import com.example.appointmentcomponent.dto.AppointmentDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface AppointmentRepository extends JpaRepository<AppointmentDTO,Long>  {
    @Query(value = "SELECT * FROM appointment WHERE id_patient = :id_patient AND id_physician = :id_physician AND date = :data LIMIT 1", nativeQuery = true)
    Optional<AppointmentDTO> selectAppointmentsByParameters(@Param("id_patient") Long idPatient, @Param("id_physician") Long idPhysician, @Param("data") String data);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM appointment WHERE id_patient = :id_patient AND id_physician = :id_physician AND date = :data", nativeQuery = true)
    void deleteAppointmentsByParameters(@Param("id_patient") Long idPatient, @Param("id_physician") Long idPhysician, @Param("data") String data);

    @Query(value = "SELECT * FROM appointment WHERE  id_physician = :id_physician ", nativeQuery = true)
    List<AppointmentDTO> selectAppointmentsByIdPhysician( @Param("id_physician") Long idPhysician);

    @Query(value = "SELECT * FROM appointment WHERE  id_patient = :id_patient ", nativeQuery = true)
    List<AppointmentDTO> selectAppointmentsByIdPatient( @Param("id_patient") Long id_patient);
}
