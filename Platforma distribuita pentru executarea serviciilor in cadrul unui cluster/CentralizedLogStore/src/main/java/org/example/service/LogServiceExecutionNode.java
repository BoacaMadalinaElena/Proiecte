package org.example.service;

import lombok.AllArgsConstructor;
import org.example.dto.*;
import org.example.exception.InvalidFieldException;
import org.example.exceptions.InvalidParameter;
import org.example.repository.LogRepositoryExecutionNode;
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
public class LogServiceExecutionNode {
    private LogRepositoryExecutionNode logRepository;
    private ValidateParameter validateParameter;

    public void saveLogs(List<LogsMessageExecutionNode> logs) throws InvalidFieldException {
        for (LogsMessageExecutionNode logsMessage : logs) {

            if (!validateParameter.isValidIPAddress(logsMessage.getAddress())) {

                throw new InvalidFieldException("Camp pentru adresa IP este invalid","");
            }
            if (!validateParameter.isValidType(logsMessage.getType())) {
                throw new InvalidFieldException("Camp pentru tip  este invalid","");
            }
            if (!validateParameter.isValidUserId(logsMessage.getUserId()) && !Objects.equals(logsMessage.getUserId(), "GENERIC")) {
                throw new InvalidFieldException("Camp pentru user id  este invalid","");
            }
            if (!validateParameter.isValidDateTimeFormat(logsMessage.getLocalDateTime())) {
                throw new InvalidFieldException("Camp pentru data este invalid, el trebuie sa fie in formatul 'dd/MM/yyyy', 'dd/MM/yyyy HH' sau 'dd/MM/yyyy HH:mm'","");   }
            logRepository.save(logsMessage);
        }
    }

    public List<LogsMessageExecutionNode> getAll() {
        return this.logRepository.findAll();
    }

    public ResponseGetLogsExecutionNode getAll(int page, int size, String time, String address, String type, String userId) throws InvalidFieldException {
        if (size <= 0 || size > 100 || page < 0) {
            throw new InvalidFieldException("Dimensiunea pagini este invalidă!", "Dimensiunea pagini este invalidă!");
        }
        if (!validateParameter.isValidIPAddress(address) && !Objects.equals(address, "")) {
            throw new InvalidFieldException("Camp pentru adresa IP este invalid","");
        }
        if (!validateParameter.isValidType(type) && !Objects.equals(type, "")) {
            throw new InvalidFieldException("Camp pentru tip  este invalid","");
        }
        if (!validateParameter.isValidUserId(userId) && !Objects.equals(userId, "GENERIC") && !Objects.equals(userId, "")) {
            throw new InvalidFieldException("Camp pentru user id  este invalid","");
        }
        if (!validateParameter.isValidDateTimeFormat(time) && !Objects.equals(time, "") ) {
            throw new InvalidFieldException("Camp pentru data este invalid, el trebuie sa fie in formatul 'dd/MM/yyyy', 'dd/MM/yyyy HH' sau 'dd/MM/yyyy HH:mm'","");   }

        List<LogsMessageExecutionNode> full = this.logRepository.findAll();
        List<LogsMessageExecutionNode> messages = this.logRepository.findByCriteria(time,address,type,userId);
        messages.sort(new LogsMessageExecutionNodeComparator());
        // String time, String address, String type, String userId
      /*  for(LogsMessageExecutionNode logsMessageExecutionNode : full){
            if(logsMessageExecutionNode.getLocalDateTime().contains(time) &&
            logsMessageExecutionNode.getAddress().contains(address) &&
                    logsMessageExecutionNode.getType().contains(type) &&
                    logsMessageExecutionNode.getUserId().contains(userId)
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
        return new ResponseGetLogsExecutionNode(messages.subList(Math.min(page * size, messages.size()),
                Math.min((page + 1) * size, messages.size())) , next, prev);
    }

    public void deleteAll() {
        this.logRepository.deleteAll();
    }
}
