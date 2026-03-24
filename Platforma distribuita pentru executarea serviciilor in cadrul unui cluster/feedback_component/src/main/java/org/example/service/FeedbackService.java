package org.example.service;



import lombok.AllArgsConstructor;
import org.example.dto.FeedbackDto;
import org.example.dto.FeedbackResponseDto;
import org.example.exception.InvalidFieldException;
import org.example.exception.NotFoundException;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

import org.example.repository.FeedbackRepository;
@Service
@AllArgsConstructor
public class FeedbackService {
    FeedbackRepository feedbackRepository;

    public void addFeedback(FeedbackDto feedbackDto) throws InvalidFieldException {
        if (feedbackDto.getSubject().length() < 5 || feedbackDto.getContent().length() < 10 || feedbackDto.getSubject().length() > 100 || feedbackDto.getContent().length() > 1000) {
            throw new InvalidFieldException("Lungimea subiectului trebuie să fie între 5 și 100 de caractere, iar a conținutului între 10 și 1000",
                    "The subject length must be between 5 and 100 characters, and the content length between 10 and 1000.");
        }
        this.feedbackRepository.save(feedbackDto);
        EmailService.send("spammadalinaboaca@gmail.com",
                "Feedback from: " + feedbackDto.getEmail(),
                "<h3>ClusterServExe Page Received New Feedback</h3>"
                        + "<br><p><strong>Subject:</strong></p><p>" + feedbackDto.getSubject() + "</p>"
                        + "<br><p><strong>Content:</strong></p><p>" + feedbackDto.getContent() + "</p>",
                "en"
        );
    }

    public FeedbackResponseDto getFeedback(int page, int size, String email) throws InvalidFieldException {
        if (size < 1 || size > 100) {
            throw new InvalidFieldException("Dimensiunea pagini trebuie să fie între unu și o sută!", "The page size must be between one and one hundred!");
        }
        List<FeedbackDto> list = this.feedbackRepository.findAll();
        List<FeedbackDto> listShortCode = new ArrayList<>();
        for (FeedbackDto feedbackDto : list) {
            if (feedbackDto.getEmail().toLowerCase().contains(email.toLowerCase())) {
                listShortCode.add(feedbackDto);
            }
        }
        int totalPages = (int) Math.ceil((double) listShortCode.size() / size);

        int next = page + 1;
        if (next >= totalPages ) {
            next = totalPages - 1;
        }
        if(next < 0){
            next = 0;
        }

        int prev = page - 1;
        if (prev < 0) {
            prev = 0;
        }
        return new FeedbackResponseDto(listShortCode.subList(Math.min(page * size, listShortCode.size()),
                Math.min((page + 1) * size, listShortCode.size())), next, prev);
    }

    public void delete(String id) throws NotFoundException {
        if(this.feedbackRepository.findById(id).isPresent()){
            this.feedbackRepository.deleteById(id);
        }else{
            throw new NotFoundException("Nu există feedback-ul!","Feedback not found!");
        }
    }
}
