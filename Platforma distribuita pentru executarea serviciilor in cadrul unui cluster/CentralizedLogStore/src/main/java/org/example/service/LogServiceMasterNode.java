package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.*;
import org.example.exception.InvalidFieldException;
import org.example.repository.LogRepositoryMasterNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LogServiceMasterNode {
    private LogRepositoryMasterNode logRepositoryMasterNode;
    private ValidateParameter validateParameter;
    public void saveLogs(List<LogsMessageMasterNode> logs) throws InvalidFieldException {
        for (LogsMessageMasterNode logsMessage : logs) {
            if (!validateParameter.isValidIPAddress(logsMessage.getAddress())) {
                throw new InvalidFieldException("Camp pentru adresa IP este invalid","");
            }
            if (!validateParameter.isValidType(logsMessage.getType())) {
                throw new InvalidFieldException("Camp pentru tip  este invalid","");
            }
            if (!validateParameter.isValidDateTimeFormat(logsMessage.getLocalDateTime())) {
                throw new InvalidFieldException("Camp pentru data este invalid, el trebuie sa fie in formatul 'dd/MM/yyyy', 'dd/MM/yyyy HH' sau 'dd/MM/yyyy HH:mm'","");
            }
            try {
                logRepositoryMasterNode.save(logsMessage);
            }catch (Exception ignored){

            }
        }
    }

    public List<LogsMessageMasterNode> getAll() {
        return this.logRepositoryMasterNode.findAll();
    }

    public ResponseGetLogsMasterNode getAll(int page, int size, String time, String address, String type) throws InvalidFieldException {
        if (size <= 0 || size > 100) {
            throw new InvalidFieldException("Dimensiunea pagini este invalidă!", "Dimensiunea pagini este invalidă!");
        }
        if (!validateParameter.isValidIPAddress(address) && !Objects.equals(address, "")) {
            throw new InvalidFieldException("Camp pentru adresa IP este invalid","");
        }
        if (!validateParameter.isValidType(type) && !Objects.equals(type, "")) {
            throw new InvalidFieldException("Camp pentru tip  este invalid","");
        }
        if (!validateParameter.isValidDateTimeFormat(time) && !Objects.equals(time, "")) {
            throw new InvalidFieldException("Camp pentru data este invalid, el trebuie sa fie in formatul 'dd/MM/yyyy', 'dd/MM/yyyy HH' sau 'dd/MM/yyyy HH:mm'","");   }
        Pageable pageable = PageRequest.of(page, size);
       // List<LogsMessageMasterNode> full = this.logRepositoryMasterNode.findAll();
        List<LogsMessageMasterNode> messages = this.logRepositoryMasterNode.findByCriteria(time,address,type);
        messages.sort(new LogsMessageMasterNodeComparator());
       /* // String time, String address, String type, String userId
        for(LogsMessageMasterNode logsMessageExecutionNode : full){
            if(logsMessageExecutionNode.getLocalDateTime().contains(time) &&
                    logsMessageExecutionNode.getAddress().contains(address) &&
                    logsMessageExecutionNode.getType().contains(type)

            ){
                messages.add(logsMessageExecutionNode);
            }
        }*/


        int totalPages = (int) Math.ceil((double) messages.size() / size);

        int next = page + 1;
        if (next >= totalPages) {
            next = totalPages - 1;
        }
        if(next < 0){
            next = 0;
        }
        int prev = page - 1;
        if (prev < 0) {
            prev = 0;
        }
        return new ResponseGetLogsMasterNode(messages.subList(Math.min(page * size, messages.size()),
                Math.min((page + 1) * size, messages.size())) , next, prev);}

    public void deleteAll(){
        this.logRepositoryMasterNode.deleteAll();
    }
}
