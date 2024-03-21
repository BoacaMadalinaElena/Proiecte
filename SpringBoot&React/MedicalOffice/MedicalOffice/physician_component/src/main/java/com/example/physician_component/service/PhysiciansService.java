package com.example.physician_component.service;

import com.example.physician_component.dto.PhysiciansDTO;
import com.example.physician_component.exceptions.ConflictException;
import com.example.physician_component.exceptions.InvalidPageNumber;
import com.example.physician_component.exceptions.NotAcceptedException;
import com.example.physician_component.repository.PhysiciansRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class PhysiciansService {
    @Autowired
    private PhysiciansRepository physiciansRepository;

    public List<PhysiciansDTO> findAll() {
        return physiciansRepository.findAll();
    }

    public Optional<PhysiciansDTO> findById(Long id) {
        return physiciansRepository.findById(id);
    }

    public Optional<PhysiciansDTO> findByIdUser(Long id) {
        return physiciansRepository.selectPhysiciansByIdUser(id);
    }

    public PhysiciansDTO save(PhysiciansDTO physiciansDTO) throws NotAcceptedException, ConflictException {
        PhysiciansDTO response = null;
        physiciansDTO.setId_physician(0L);
        try {
            response = physiciansRepository.save(physiciansDTO);
        } catch (Exception exception) {
            if (exception.getMessage().contains("Column 'email' cannot be null")) {
                throw new NotAcceptedException("The email field cannot be null.");
            } else if (exception.getMessage().contains("Column 'telephone' cannot be null")) {
                throw new NotAcceptedException("The telephone field cannot be null.");
            } else if (exception.getMessage().contains("Column 'specialization' cannot be null")) {
                throw new NotAcceptedException("The specialization field cannot be null.");
            } else if (exception.getMessage().contains("physician_UN_Id_User")) {
                throw new ConflictException("There is already a doctor with this user ID.");
            } else if (exception.getMessage().contains("physician_UN_EMAIL")) {
                throw new ConflictException("There is already a doctor with this email.");
            } else if (exception.getMessage().contains("physician_CHECK_TELEPHONE")) {
                throw new NotAcceptedException("The phone number has an invalid format.");
            } else if (exception.getMessage().contains("FOREIGN KEY")) {
                throw new NotAcceptedException(
                        "There is no user with this ID for whom a doctor account has been created.");
            }  else if(exception.getMessage().contains("Data truncated for column 'specialization'")){
                throw new NotAcceptedException("Nu există această specializare");
            } else {
                exception.printStackTrace();
                throw new NotAcceptedException(exception.getMessage());
            }
        }

        return response;
    }

    public void deleteById(Long id) throws NotAcceptedException {
        try {
            physiciansRepository.deleteById(id);
        }catch (Exception ex){
            if(ex.getMessage().contains("foreign key")){
                throw new NotAcceptedException("Acest medic este referit in alte tabele. Nu poate fi sters");
            }
        }
    }

    public void update(Long id, PhysiciansDTO physicianDTO) throws NotAcceptedException, ConflictException {
        Optional<PhysiciansDTO> physician = physiciansRepository.findById(id);
        PhysiciansDTO physicianToUpdate = physician.get();
        physicianDTO.setId_physician(id);
        physicianToUpdate.setId_user(physicianDTO.getId_user());
        physicianToUpdate.setFirst_name(physicianDTO.getFirst_name());
        physicianToUpdate.setLast_name(physicianDTO.getLast_name());
        physicianToUpdate.setEmail(physicianDTO.getEmail());
        physicianToUpdate.setTelephone(physicianDTO.getTelephone());
        physicianToUpdate.setSpecialization(physicianDTO.getSpecialization());

        try {
            physiciansRepository.save(physicianToUpdate);
        } catch (Exception exception) {
            if (exception.getMessage().contains("Column 'email' cannot be null")) {
                throw new NotAcceptedException("The email field cannot be null.");
            } else if (exception.getMessage().contains("Column 'telephone' cannot be null")) {
                throw new NotAcceptedException("The telephone field cannot be null.");
            } else if (exception.getMessage().contains("Column 'specialization' cannot be null")) {
                throw new NotAcceptedException("The specialization field cannot be null.");
            } else if (exception.getMessage().contains("physician_UN_Id_User")) {
                throw new ConflictException("There is already a doctor with this user ID.");
            } else if (exception.getMessage().contains("physician_UN_EMAIL")) {
                throw new ConflictException("There is already a doctor with this email.");
            } else if (exception.getMessage().contains("physician_CHECK_TELEPHONE")) {
                throw new NotAcceptedException("The phone number has an invalid format.");
            } else if (exception.getMessage().contains("FOREIGN KEY")) {
                throw new NotAcceptedException(
                        "There is no user with this ID for whom a doctor account has been created.");
            } else if(exception.getMessage().contains("Data truncated for column 'specialization'")){
                throw new NotAcceptedException("Nu există această specializare");
            }  else {
                exception.printStackTrace();
                throw new NotAcceptedException(exception.getMessage());
            }
        }
    }

    public List<PhysiciansDTO> selectPhysiciansBySpecialization(String s) {
        return physiciansRepository.selectPhysiciansBySpecialization(s);
    }

    public List<PhysiciansDTO> findByName(String name) {
        List<PhysiciansDTO> list = physiciansRepository.findAll();
        return list.stream().filter(p -> p.getLast_name().contains(name)).toList();
    }

    public List<PhysiciansDTO> getAllPhysiciansPage(int page, int itemsPerPage) throws  InvalidPageNumber {
        if (itemsPerPage <= 0) {
            throw new IllegalArgumentException("Items per page should be a positive integer.");
        }

        List<PhysiciansDTO> allPatients = physiciansRepository.findAll();


        int totalPages = (allPatients.size() - 1) / itemsPerPage;
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allPatients.size());

        if (page < 0 || page > totalPages) {
            throw new InvalidPageNumber("Numar de pagina invalid! Indexul maxim de pagina este: " + totalPages + " pentru pagini cu: " + itemsPerPage + " itemi pe pagina!");
        }

        List<PhysiciansDTO> result = allPatients.subList(start, end);
        result.sort(Comparator.comparing(PhysiciansDTO::getId_physician));
        return result;
    }
}
