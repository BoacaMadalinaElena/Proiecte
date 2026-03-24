package org.example.service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.example.dto.CodeRecordRequest;
import org.example.exception.InvalidFieldException;
import org.example.repository.CodeMessageRepository;
import org.example.repository.CodeRecordRepository;
import org.springframework.stereotype.Service;
import org.example.dto.*;
import java.util.*;

@Service
@AllArgsConstructor
public class CodeService {
    private CodeMessageRepository codeMessageRepository;
    private CodeRecordRepository codeRecordRepository;
    private ParameterValidateService parameterValidateService;

    public void insert(CodeRecordRequest codeRecordRequest) throws InvalidFieldException {
        if (codeRecordRequest.getTitle().length() >= 100 || codeRecordRequest.getTitle().length() < 5) {
            throw new InvalidFieldException("Lungimea titlului trebuie să fie între 5 și 100 de caractere!", "The length of the title must be between 5 and 100 characters!");
        }
        if (codeRecordRequest.getDescription().length() >= 500) {
            throw new InvalidFieldException("Descrierea poate avea maxim 1000 de caractere!", "The length of the title must be between 5 and 100 characters!");
        }
        if (!parameterValidateService.isValidUUID(codeRecordRequest.getUserId())) {
            throw new InvalidFieldException("Id-ul utilizatorului nu este unul valid!", "The user ID is not valid!");
        }

        int sizeInMiB = 10;
        long bytesCount = sizeInMiB * 1024 * 1024;
        long cnt = 0;
        for(CodeMessage codeRecord : codeRecordRequest.getCodeMessageList()){
            cnt += codeRecord.getContentBytes().length;
            if(codeRecord.getContentBytes().length > bytesCount){
                throw new InvalidFieldException("Fiecare fișier încărcat trebuie să aibă maxim 10MiB.", "Each uploaded file must be a maximum of 10 MiB.");
            }
            if(cnt > 50*1024*1024){
                throw new InvalidFieldException("Toate fișierele încărcate trebuie să ocupe maxim 50MiB.", "All uploaded files must occupy a maximum of 50 MiB.");
            }
        }


        CodeRecord codeRecord = new CodeRecord(null, codeRecordRequest.getTitle(), codeRecordRequest.getDescription(), codeRecordRequest.getUserId(), codeRecordRequest.getType(), codeRecordRequest.getIsPublic(),codeRecordRequest.getTypeRun());
        CodeRecord codeRecordSaved = this.codeRecordRepository.save(codeRecord);
        for (CodeMessage codeMessage : codeRecordRequest.getCodeMessageList()) {
            codeMessage.setCodeRecord(codeRecordSaved.getId());
            this.codeMessageRepository.save(codeMessage);
        }
    }

    public ResponseGetCode getAllCodes(int page, int size, String title) throws InvalidFieldException {
        if (size < 1 || size > 100) {
            throw new InvalidFieldException("Dimensiunea pagini trebuie să fie între unu și o sută!", "The page size must be between one and one hundred!");
        }
        List<CodeRecord> list = this.codeRecordRepository.findAll();
        List<ShortCode> listShortCode = new ArrayList<>();
        for (CodeRecord codeRecord : list) {
            if (codeRecord.getTitle().contains(title)) {
                listShortCode.add(new ShortCode(codeRecord.getId(), codeRecord.getUserId(), codeRecord.getTitle(), codeRecord.getDescription()));
            }
        }

        int totalPages = (int) Math.ceil((double) listShortCode.size() / size);

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
        return new ResponseGetCode(listShortCode.subList(Math.min(page * size, listShortCode.size()),
                Math.min((page + 1) * size, listShortCode.size())) , next, prev);
    }

    public CodeRecordRequest getById(String id) throws InvalidFieldException {
        if (!parameterValidateService.isValidUUID(id)) {
            throw new InvalidFieldException("Id-ul nu este un identificator valid", "The ID is not a valid identifier.");
        }
        Optional<CodeRecord> codeRecord = this.codeRecordRepository.findById(id);
        if (codeRecord.isPresent()) {
            List<CodeMessage> listCodeMessage;
            if (Objects.equals(codeRecord.get().getIsPublic(), "true")) {
                listCodeMessage = this.codeMessageRepository.findByCodeRecord(id);
            } else {
                listCodeMessage = null;
            }
            return new CodeRecordRequest(codeRecord.get().getTitle(), codeRecord.get().getDescription(), codeRecord.get().getUserId(), listCodeMessage, codeRecord.get().getType(), codeRecord.get().getIsPublic(),codeRecord.get().getTypeRun());
        } else {
            return null;
        }
    }

    public CodeRecordRequest getByIdIntern(String id) throws InvalidFieldException {
        if (!parameterValidateService.isValidUUID(id)) {
            throw new InvalidFieldException("Id-ul nu este un identificator valid", "The ID is not a valid identifier.");
        }
        Optional<CodeRecord> codeRecord = this.codeRecordRepository.findById(id);
        if (codeRecord.isPresent()) {
            List<CodeMessage> listCodeMessage;
            listCodeMessage = this.codeMessageRepository.findByCodeRecord(id);
            return new CodeRecordRequest(codeRecord.get().getTitle(), codeRecord.get().getDescription(), codeRecord.get().getUserId(), listCodeMessage, codeRecord.get().getType(), codeRecord.get().getIsPublic(),codeRecord.get().getTypeRun());
        } else {
            return null;
        }
    }

    @Transactional
    public void deleteById(String id) throws InvalidFieldException {
        if (!parameterValidateService.isValidUUID(id)) {
            throw new InvalidFieldException("Id-ul nu este un identificator valid", "The ID is not a valid identifier.");
        }

        this.codeMessageRepository.deleteByCodeRecord(id);
        this.codeRecordRepository.deleteById(id);
    }
}
