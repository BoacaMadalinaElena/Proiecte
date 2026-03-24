package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.*;
import org.example.exception.InvalidFieldException;
import org.example.repository.LogRepositoryHttpNode;
import org.example.repository.LogRepositoryMasterNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
@AllArgsConstructor
public class LogServiceHttpNode {
    private LogRepositoryHttpNode logRepositoryHttpNode;
    private ValidateParameter validateParameter;

    public void saveLogs(List<LogsMessageHttpNode> logs) throws InvalidFieldException {
        for (LogsMessageHttpNode logsMessage : logs) {

            if (!validateParameter.isValidIPAddress(logsMessage.getAddress())) {
                throw new InvalidFieldException("Camp pentru adresa IP este invalid","");
            }
            if (!validateParameter.isValidType(logsMessage.getType())) {
                throw new InvalidFieldException("Camp pentru tip  este invalid","");
            }
            if (!validateParameter.isValidDateTimeFormat(logsMessage.getLocalDateTime())) {
                throw new InvalidFieldException("Camp pentru data este invalid, el trebuie sa fie in formatul 'dd/MM/yyyy', 'dd/MM/yyyy HH' sau 'dd/MM/yyyy HH:mm'","");   }
            logRepositoryHttpNode.save(logsMessage);
        }
    }

    public List<LogsMessageHttpNode> getAll() {
        return this.logRepositoryHttpNode.findAll();
    }

    public ResponseGetLogsHttpNode getAll(int page, int size, String time, String address, String type,String typeNode) throws InvalidFieldException {
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
            throw new InvalidFieldException("Camp pentru data este invalid, el trebuie sa fie in formatul 'dd/MM/yyyy', 'dd/MM/yyyy HH' sau 'dd/MM/yyyy HH:mm'","");     }

        //List<LogsMessageHttpNode> full = this.logRepositoryHttpNode.findAll();
        List<LogsMessageHttpNode> messages = this.logRepositoryHttpNode.findByCriteria(time,address,type,typeNode);
        messages.sort(new LogsMessageHttpNodeComparator());
        // String time, String address, String type, String userId
      /*  for(LogsMessageHttpNode logsMessageExecutionNode : full){
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
        return new ResponseGetLogsHttpNode(messages.subList(Math.min(page * size, messages.size()),
                Math.min((page + 1) * size, messages.size())) , next, prev);
    }

    public void deleteAll(){
        this.logRepositoryHttpNode.deleteAll();
    }
}
