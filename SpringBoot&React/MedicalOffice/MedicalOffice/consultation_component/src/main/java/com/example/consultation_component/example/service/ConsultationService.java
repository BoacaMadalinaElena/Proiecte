package com.example.consultation_component.example.service;

import com.example.consultation_component.example.dto.ConsultationDto;
import com.example.consultation_component.example.dto.InvestigationDto;
import com.example.consultation_component.example.exceptions.NotAcceptableException;
import com.example.consultation_component.example.exceptions.NotFoundException;
import com.example.consultation_component.example.repository.ConsultationRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

@Service
public class ConsultationService {
    @Autowired
    private ConsultationRepository consultationRepository;
    @Autowired
    private ParameterValidationsService parameterValidationsService;
    // pentru a verifica cu celelalte cereri
    RestTemplate restTemplate = new RestTemplate();

    public List<ConsultationDto> findAll() {
        return consultationRepository.findAll();
    }

    public void deleteConsultationById(String id) throws NotFoundException {
        Optional<ConsultationDto> consultationDto = consultationRepository.findById(id);
        if (consultationDto.isPresent()) {
            consultationRepository.deleteById(id);
        } else {
            throw new NotFoundException("Consultation not found!");
        }
    }

    public void updateConsultationById(String header,String id, ConsultationDto consultationDto) throws NotAcceptableException {
        consultationDto.setId(id);
        String formattedData = null;
        try {
            if (consultationDto.getDate() == null) {
                throw new NotAcceptableException("Data nu poate fi null");
            }

            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = originalFormat.parse(consultationDto.getDate());

            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            formattedData = newFormat.format(date);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            throw new NotAcceptableException("Data nu este in formatul: yyyy-MM-dd HH:mm");
        }

        String apiUrl = "http://localhost:8081/api/pos/appointments/" + consultationDto.getId_patient() + "/" + consultationDto.getId_physician() + "/" + formattedData;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", header);

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();
            System.out.println(responseBody);

            for (int i = 0; i < consultationDto.getInvestigations().size(); i++) {
                if(consultationDto.getInvestigations().get(i).getName().length() > 100){
                    throw new NotAcceptableException("Numele din investigatie nu poate avea mai mult de 100 de caractere");
                }
                if(consultationDto.getInvestigations().get(i).getResult().length() > 100){
                    throw new NotAcceptableException("Rezultatul nu poate avea mai mult de 100 de caractere");
                }
                if (!parameterValidationsService.isValidName(consultationDto.getInvestigations().get(i).getName())) {
                    throw new NotAcceptableException("Numele din investigatie are un format invalid! El poate contine doar caractere a-z A-Z cifrele 0-9, caracterele '.', ',', '-'");
                }

                if (consultationDto.getInvestigations().get(i).getDays() != null && (consultationDto.getInvestigations().get(i).getDays() < 0 || consultationDto.getInvestigations().get(i).getDays() >= 30)) {
                    throw new NotAcceptableException("Numarul de zile trebuie sa fie mai mare sau egal ca 0 si mai mic sau egal decat 30.");
                }
                if (!parameterValidationsService.isValidResult(consultationDto.getInvestigations().get(i).getResult())) {
                    throw new NotAcceptableException("Rezultatul are un format invalid! El poate contine doar caractere a-z A-Z cifrele 0-9, caracterele '.', ',', '-'");
                }
            }
            consultationRepository.save(consultationDto);
        } catch (HttpClientErrorException httpClientErrorException) {
            throw new NotAcceptableException("Nu exista nici o programare cu acest id de pacient, acest id de medic si aceasta data! In concluzie nu se poate crea o consultatie!");
        }
    }

    public ConsultationDto createConsultation(String header,ConsultationDto consultationDto) throws NotAcceptableException {
        String formattedData = null;
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            if (consultationDto.getDate() == null) {
                throw new NotAcceptableException("Data nu poate fi null");
            }
            Date date = originalFormat.parse(consultationDto.getDate());

            SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

            formattedData = newFormat.format(date);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            throw new NotAcceptableException("Data nu este in formatul: yyyy-MM-dd HH:mm");
        }

        String apiUrl = "http://localhost:8081/api/pos/appointments/" + consultationDto.getId_patient() + "/" + consultationDto.getId_physician() + "/" + formattedData;

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", header);

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(apiUrl, HttpMethod.GET, entity, String.class);
            String responseBody = response.getBody();
            System.out.println(responseBody);

            // verificare ca nu am deja consultatie pentru programare
            List<ConsultationDto> consultationDtoList = consultationRepository.findAll();
            for (int i = 0; i < consultationDtoList.size(); i++) {
                if (Objects.equals(consultationDtoList.get(i).getId_patient(), consultationDto.getId_patient()) &&
                        Objects.equals(consultationDtoList.get(i).getId_physician(), consultationDto.getId_physician()) &&
                        Objects.equals(consultationDtoList.get(i).getDate(), consultationDto.getDate())) {
                    throw new NotAcceptableException("Exista deja o consultatie pentru programarea cu acest id de medic, id de pacient si data! Va rugam sa o actualizati pe paza id-ului");
                }
            }

            // setare id null pentru al autogenera
            for (int i = 0; i < consultationDto.getInvestigations().size(); i++) {
                consultationDto.getInvestigations().get(i).setId(new ObjectId());
            }
            for (int i = 0; i < consultationDto.getInvestigations().size(); i++) {
                if (!parameterValidationsService.isValidName(consultationDto.getInvestigations().get(i).getName())) {
                    throw new NotAcceptableException("Numele din investigatie are un format invalid! El poate contine doar caractere a-z A-Z cifrele 0-9, caracterele '.', ',', '-'");
                }
                if(consultationDto.getInvestigations().get(i).getName().length() > 100){
                    throw new NotAcceptableException("Numele din investigatie nu poate avea mai mult de 100 de caractere");
                }
                if(consultationDto.getInvestigations().get(i).getResult().length() > 100){
                    throw new NotAcceptableException("Rezultatul nu poate avea mai mult de 100 de caractere");
                }
                if (consultationDto.getInvestigations().get(i).getDays() != null && (consultationDto.getInvestigations().get(i).getDays() < 0 || consultationDto.getInvestigations().get(i).getDays() >= 30)) {
                    throw new NotAcceptableException("Numarul de zile trebuie sa fie mai mare sau egal ca 0 si mai mic sau egal decat 30.");
                }
                if (!parameterValidationsService.isValidResult(consultationDto.getInvestigations().get(i).getResult())) {
                    throw new NotAcceptableException("Rezultatul are un format invalid! El poate contine doar caractere a-z A-Z cifrele 0-9, caracterele '.', ',', '-'");
                }
            }
            return consultationRepository.insert(consultationDto);
        } catch (HttpClientErrorException httpClientErrorException) {
            System.out.println(httpClientErrorException.getMessage());
            throw new NotAcceptableException("Nu exista nici o programare cu acest id de pacient, acest id de medic si aceasta data! In concluzie nu se poate crea o consultatie!");
        }
    }

    public Optional<ConsultationDto> selectConsultationById(String id) {
        return consultationRepository.findById(id);
    }

    public void updateInvestigations(String id, List<InvestigationDto> list) throws NotAcceptableException {
        Optional<ConsultationDto> consultationDto = consultationRepository.findById(id);
        for (int i = 0; i < list.size(); i++) {
            if (!parameterValidationsService.isValidName(list.get(i).getName())) {
                throw new NotAcceptableException("Numele din investigatie are un format invalid! El poate contine doar caractere a-z A-Z cifrele 0-9, caracterele '.', ',', '-'");
            }

            if (list.get(i).getDays() != null && (list.get(i).getDays() < 0 || list.get(i).getDays() >= 30)) {
                throw new NotAcceptableException("Numarul de zile trebuie sa fie mai mare sau egal ca 0 si mai mic sau egal decat 30.");
            }
            if (!parameterValidationsService.isValidResult(list.get(i).getResult())) {
                throw new NotAcceptableException("Rezultatul are un format invalid! El poate contine doar caractere a-z A-Z cifrele 0-9, caracterele '.', ',', '-'");
            }
        }
        for (int i = 0; i < list.size(); i++) {
            list.get(i).setId(new ObjectId());
            if(list.get(i).getName().length() > 100){
                throw new NotAcceptableException("Numele din investigatie nu poate avea mai mult de 100 de caractere");
            }
            if(list.get(i).getResult() != null && list.get(i).getResult().length() > 100){
                throw new NotAcceptableException("Rezultatul nu poate avea mai mult de 100 de caractere");
            }
        }
        consultationDto.get().setInvestigations(list);
        consultationRepository.save(consultationDto.get());
    }

    public List<ConsultationDto> selectConsultationByParameters(Integer id_patient, Integer id_physician, String data) {
        List<ConsultationDto> consultationDtoList = consultationRepository.findAll();
        List<ConsultationDto> list = new ArrayList<>();
        for (int i = 0; i < consultationDtoList.size(); i++) {
            if (Objects.equals(consultationDtoList.get(i).getId_patient(), id_patient) &&
                    Objects.equals(consultationDtoList.get(i).getId_physician(), id_physician) &&
                    Objects.equals(consultationDtoList.get(i).getDate(), data)) {
                 list.add(consultationDtoList.get(i));
            }
        }
        return list;
    }
}
