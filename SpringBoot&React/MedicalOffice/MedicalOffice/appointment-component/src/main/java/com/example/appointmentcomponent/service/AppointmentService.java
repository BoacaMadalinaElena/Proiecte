package com.example.appointmentcomponent.service;

import com.example.appointmentcomponent.dto.AppointmentDTO;
import com.example.appointmentcomponent.dto.type.TypeSelectDate;
import com.example.appointmentcomponent.exceptions.NotAcceptableException;
import com.example.appointmentcomponent.exceptions.NotFoundException;
import com.example.appointmentcomponent.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;
    @Autowired
    private ParameterValidationsService parameterValidationsService;

    public List<AppointmentDTO> findAll() {
        return appointmentRepository.findAll();
    }

    public Optional<AppointmentDTO> selectAppointmentsByParameters(Long id_patient, Long id_physician, String  data) {
        return appointmentRepository.selectAppointmentsByParameters(id_patient, id_physician, data);
    }

    public void deleteAppointmentsByParameters(Long id_patient, Long id_physician, String data) throws NotFoundException {
        Optional<AppointmentDTO> appointmentDTO = appointmentRepository.selectAppointmentsByParameters(id_patient, id_physician, data);
        if (appointmentDTO.isPresent()) {
            appointmentRepository.deleteAppointmentsByParameters(id_patient, id_physician, data);
        } else {
            throw new NotFoundException("Appointment not found!");
        }
    }

    public void updateAppointmentsByParameters(Long id_patient, Long id_physician, String data, AppointmentDTO appointmentDTO) throws NotAcceptableException {
        try {
            appointmentRepository.save(appointmentDTO);
        }catch (Exception ex){
            if(ex.getMessage().contains("status")){
                throw new NotAcceptableException("Statusul poate fi doar honored, cancelled, not presented sau null.");
            }
        }
    }

    public AppointmentDTO createAppointmentsByParameters(AppointmentDTO appointmentDTO) throws  NotAcceptableException {
        try {
            return appointmentRepository.save(appointmentDTO);
        } catch (Exception ex) {
            if (ex.getMessage().contains("appointment_UN_Patient_Date")) {
                throw new NotAcceptableException("This patient already has an appointment at the specified date and time.");
            } else if (ex.getMessage().contains("appointment_UN_ID_PHYSICIAN_DATE")) {
                throw new NotAcceptableException("This doctor already has an appointment at the specified date and time.");
            } else if (ex.getMessage().contains("appointment_Patient_FK")) {
                throw new NotAcceptableException("There is no patient with the specified ID.");
            } else if (ex.getMessage().contains("appointment_FK")) {
                throw new NotAcceptableException("There is no doctor with the specified ID.");
            }else if(ex.getMessage().contains("status")){
                throw new NotAcceptableException("Statusul poate fi doar honored, cancelled, not presented sau null.");
            }
            else {
                throw ex;
            }
        }
    }

    public List<AppointmentDTO> selectAppointmentsByIdPhysician(Long id) {
        return appointmentRepository.selectAppointmentsByIdPhysician(id);
    }

    public List<AppointmentDTO> selectAppointmentsByIdPatient(Long id) {
        return appointmentRepository.selectAppointmentsByIdPatient(id);
    }

    public List<AppointmentDTO> selectAppointmentsByIdPatientAndDate(Long id, TypeSelectDate typeSelectDate, String value) throws NotAcceptableException {
        List<AppointmentDTO> list = appointmentRepository.selectAppointmentsByIdPatient(id);
        List<AppointmentDTO> result = new ArrayList<>();
        switch (typeSelectDate) {
            case MONTH -> {
                try {
                    int a = Integer.parseInt(value);
                    if (a <= 0 || a > 12) {
                        throw new NotAcceptableException("Campul value trebuie sa fie o luna(numar de la 1 la 12)!");
                    }
                } catch (Exception ex) {
                    throw new NotAcceptableException("Campul value trebuie sa fie o luna(numar de la 1 la 12)!");
                }
                for (AppointmentDTO a : list
                ) {
                    String expectedFormat = "yyyy-MM-dd HH:mm:ss";
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
                    LocalDateTime date = LocalDateTime.parse(a.getDate(), formatter);
                    if (date.getMonth().getValue() == Integer.parseInt(value)) {
                        result.add(a);
                    }
                }
            }

            case DAY -> {
                try {
                    int a = Integer.parseInt(value);
                    if (a <= 0 || a > 31) {
                        throw new NotAcceptableException("Campul value trebuie sa fie o zi din luna valida in general intre 1 si 31!");
                    }
                } catch (Exception ex) {
                    throw new NotAcceptableException("Campul value trebuie sa fie o zi din luna valida in general intre 1 si 31!");
                }
                for (AppointmentDTO a : list
                ) {
                    String expectedFormat = "yyyy-MM-dd HH:mm:ss";
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
                    LocalDateTime date = LocalDateTime.parse(a.getDate(), formatter);
                    if (date.getDayOfMonth() == Integer.parseInt(value) && date.getMonth() == LocalDate.now().getMonth()) {
                        result.add(a);
                    }
                }
            }
            case FULL -> {
                LocalDateTime localDateTime;
                try {
                    localDateTime = parameterValidationsService.isValidDate(value);
                } catch (Exception ex) {
                    throw new NotAcceptableException("Data trebuie sa fie in formatul yyyy-MM-dd HH:mm!");
                }
                for (AppointmentDTO a : list
                ) {
                    if (a.getDate().toString().replace("T", " ").contains(value)) {
                        result.add(a);
                    } else {
                        System.out.println(a.getDate() + " " + value);
                    }
                }
            }
        }
        return result;
    }

    public List<AppointmentDTO> getAllAppointmentsPage(int page, int itemsPerPage) throws NotAcceptableException {
        if (itemsPerPage <= 0 || itemsPerPage > 100) {
            throw new NotAcceptableException("Numarul de itemi pe pagina trebuie sa fie intre 1 si 100");
        }
        List<AppointmentDTO> allPatients = appointmentRepository.findAll();

        int totalPages = (allPatients.size() - 1) / itemsPerPage;
        int start = page * itemsPerPage;
        int end = Math.min(start + itemsPerPage, allPatients.size());

        if (page < 0 || page > totalPages) {
            throw new NotAcceptableException("Numar de pagina invalid! Indexul maxim de pagina este: " + totalPages + " pentru pagini cu: " + itemsPerPage + " itemi pe pagina!");
        }

        List<AppointmentDTO> result = allPatients.subList(start, end);
        result.sort(Comparator.comparing(AppointmentDTO::getDate));
        return result;
    }

    public List<AppointmentDTO> selectAppointmentsByIdPhysiciansAndDate(Long id, TypeSelectDate typeSelectDate, String value) throws NotAcceptableException {
        List<AppointmentDTO> list = appointmentRepository.selectAppointmentsByIdPhysician(id);
        List<AppointmentDTO> result = new ArrayList<>();
        switch (typeSelectDate) {
            case MONTH -> {
                try {
                    int a = Integer.parseInt(value);
                    if (a <= 0 || a > 12) {
                        throw new NotAcceptableException("Campul value trebuie sa fie o luna(numar de la 1 la 12)!");
                    }
                } catch (Exception ex) {
                    throw new NotAcceptableException("Campul value trebuie sa fie o luna(numar de la 1 la 12)!");
                }
                for (AppointmentDTO a : list
                ) {
                    String expectedFormat = "yyyy-MM-dd HH:mm:ss";
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
                    LocalDateTime parsedDateTime = LocalDateTime.parse(a.getDate(), formatter);
                    if (parsedDateTime.getMonth().getValue() == Integer.parseInt(value)) {
                        result.add(a);
                    }
                }
            }

            case DAY -> {
                try {
                    int a = Integer.parseInt(value);
                    if (a <= 0 || a > 31) {
                        throw new NotAcceptableException("Campul value trebuie sa fie o zi din luna valida in general intre 1 si 31!");
                    }
                } catch (Exception ex) {
                    throw new NotAcceptableException("Campul value trebuie sa fie o zi din luna valida in general intre 1 si 31!");
                }
                for (AppointmentDTO a : list
                ) {
                    String expectedFormat = "yyyy-MM-dd HH:mm:ss";
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern(expectedFormat);
                    LocalDateTime parsedDateTime = LocalDateTime.parse(a.getDate(), formatter);

                    if (parsedDateTime.getDayOfMonth() == Integer.parseInt(value) && parsedDateTime.getMonth() == LocalDate.now().getMonth()) {
                        result.add(a);
                    }
                }
            }
            case FULL -> {
                LocalDateTime localDateTime;
                try {
                    localDateTime = parameterValidationsService.isValidDate(value);
                } catch (Exception ex) {
                    throw new NotAcceptableException("Data trebuie sa fie in formatul yyyy-MM-dd HH:mm!");
                }
                for (AppointmentDTO a : list
                ) {
                    if (a.getDate().toString().replace("T", " ").contains(value)) {
                        result.add(a);
                    } else {
                        System.out.println(a.getDate() + " " + value);
                    }
                }
            }
        }
        return result;
    }

    public List<AppointmentDTO> selectAppointmentsByIdPhysiciansAndStatus(Long id, String status) {
        List<AppointmentDTO> list = appointmentRepository.selectAppointmentsByIdPhysician(id);
        List<AppointmentDTO> result = new ArrayList<>();
        for (AppointmentDTO a : list
        ) {
            try {
                if (a.getStatus().equals(status))
                    result.add(a);
            } catch (NullPointerException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return result;
    }

    public List<AppointmentDTO> selectAppointmentsByIdPatientAndStatus(Long id, String status) {
        List<AppointmentDTO> list = appointmentRepository.selectAppointmentsByIdPatient(id);
        List<AppointmentDTO> result = new ArrayList<>();
        for (AppointmentDTO a : list
        ) {
            try {
                if (a.getStatus().equals(status))
                    result.add(a);
            } catch (NullPointerException nullPointerException) {
                System.out.println(nullPointerException.getMessage());
            }
        }
        return result;
    }
}
