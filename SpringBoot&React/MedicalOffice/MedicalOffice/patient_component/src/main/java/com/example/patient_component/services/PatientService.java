package com.example.patient_component.services;

import com.example.patient_component.dto.PatientDTO;
import com.example.patient_component.exceptions.ConflictException;
import com.example.patient_component.exceptions.InvalidPageNumber;
import com.example.patient_component.exceptions.NotAcceptableException;
import com.example.patient_component.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PatientService {
    @Autowired
    private PatientRepository patientRepository;


    public List<PatientDTO> findAll() {
        return patientRepository.findAll();
    }

    public Optional<PatientDTO> findById(Long id){
        return patientRepository.findById(id);
    }

    public Optional<PatientDTO> findByCnp(String cnp) {
        return patientRepository.findByCnp(cnp);
    }

    public void deactivateByCnp(String cnp) {
        patientRepository.deactivateByCnp(cnp);
    }

    public boolean updateOrAdd(String cnp, PatientDTO patientDTO) throws NotAcceptableException, ConflictException {
        LocalDate now = LocalDate.now();
        String expectedFormat = "yyyy-MM-dd";
        LocalDate birthDate;
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
            LocalDate.parse(patientDTO.getBirthDay(), formatter);
            birthDate = LocalDate.parse(patientDTO.getBirthDay());
        } catch (Exception ex) {
            throw new IllegalArgumentException();
        }
        long diff = birthDate.until(now, ChronoUnit.YEARS);

        if (diff < 18) {
            throw new NotAcceptableException("Invalid date! The patient is underage.");
        }
        Optional<PatientDTO> patient = patientRepository.findByCnp(cnp);
        if (patient.isPresent()) {
            PatientDTO patientToUpdate = patient.get();

            patientToUpdate.setCnp(patientDTO.getCnp());
            patientToUpdate.setUser_id(patientDTO.getUser_id());
            patientToUpdate.setFirstName(patientDTO.getFirstName());
            patientToUpdate.setLastName(patientDTO.getLastName());
            patientToUpdate.setEmail(patientDTO.getEmail());
            patientToUpdate.setTelephone(patientDTO.getTelephone());
            patientToUpdate.setBirthDay(patientDTO.getBirthDay());
            patientToUpdate.setIs_active(patientDTO.getIs_active());
            try {
                patientRepository.save(patientToUpdate);
            } catch (Exception ex) {
                if (ex.getMessage().contains("patient_CHECK_Telephone")) {
                    throw new NotAcceptableException("Telephone number in invalid!");
                } else if (ex.getMessage().contains("patient_CHECK_CNP")) {
                    throw new NotAcceptableException("CNP is invalid!");
                } else if (ex.getMessage().contains("FOREIGN KEY")) {
                    throw new NotAcceptableException("There is no user with these credentials.");
                } else if (ex.getMessage().contains("patient_Unique_Email")) {
                    throw new ConflictException("There is already a patient with this email.");
                } else if (ex.getMessage().contains("patient_id_user_UN")) {
                    throw new ConflictException("There is already a patient with this user id.");
                } else {
                    throw new NotAcceptableException(ex.getMessage());
                }
            }
            return true;
        } else {
            try {
                patientRepository.save(patientDTO);
            } catch (Exception ex) {
                if (ex.getMessage().contains("patient_CHECK_Telephone")) {
                    throw new NotAcceptableException("Telephone number in invalid!");
                } else if (ex.getMessage().contains("patient_CHECK_CNP")) {
                    throw new NotAcceptableException("CNP is invalid!");
                } else if (ex.getMessage().contains("FOREIGN KEY")) {
                    throw new NotAcceptableException("There is no user with these credentials.");
                } else if (ex.getMessage().contains("patient_Unique_Email")) {
                    throw new ConflictException("There is already a patient with this email.");
                } else if (ex.getMessage().contains("patient_id_user_UN")) {
                    throw new ConflictException("There is already a patient with this user id.");
                } else {
                    throw new NotAcceptableException(ex.getMessage());
                }
            }
            return false;
        }
    }

    public List<PatientDTO> getAllPatientsByName(String name) {
        List<PatientDTO> list = patientRepository.findAll();
        return list.stream().filter(p -> p.getFirstName().contains(name)).toList();
    }

    public List<PatientDTO> getAllPatientsByBirthDate(LocalDate birthDate) {
        List<PatientDTO> list = patientRepository.findAll();

        return list.stream()
                .filter(p -> {
                    String expectedFormat = "yyyy-MM-dd";
                    LocalDate birthDateInP;
                    try {
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
                        LocalDate.parse(p.getBirthDay(), formatter);
                        birthDateInP = LocalDate.parse(p.getBirthDay());
                    } catch (Exception ex) {
                        throw new IllegalArgumentException();
                    }


                    LocalDate patientBirthDate = birthDateInP;
                    if (patientBirthDate != null) {
                        return birthDate.equals(patientBirthDate);
                    }
                    return false;
                })
                .collect(Collectors.toList());
    }

    public List<PatientDTO> findByIsActiveTrue() {
        return patientRepository.findByIsActiveTrue();
    }

    public List<PatientDTO> getAllPatientsPage(int page, int itemsPerPage) throws InvalidPageNumber {
        if (itemsPerPage <= 0) {
            throw new IllegalArgumentException("Items per page should be a positive integer.");
        }

        List<PatientDTO> allPatients = patientRepository.findAll();


        int totalPages = (allPatients.size() - 1) / itemsPerPage;
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allPatients.size());

        if (page < 0 || page > totalPages) {
            throw new InvalidPageNumber("Numar de pagina invalid! Indexul maxim de pagina este: " + totalPages + " pentru pagini cu: " + itemsPerPage + " itemi pe pagina!");
        }

        List<PatientDTO> result = allPatients.subList(start, end);
        result.sort(Comparator.comparing(PatientDTO::getCnp));
        return result;
    }


}
