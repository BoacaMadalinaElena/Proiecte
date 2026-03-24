package org.example.controller;


import org.example.exception.InvalidFieldException;
import org.example.exception.NotFoundException;
import org.example.other.CustomPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

import org.example.service.FeedbackService;
import org.example.dto.*;

@RestController
@RequestMapping("/api/cluster/feedback")
public class FeedbackController {
    @Autowired
    private FeedbackService feedbackService;

    @PostMapping
    public ResponseEntity<?> sendFeedback(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole,@RequestHeader("X-Email") String email, @RequestBody FeedbackDto feedbackDto) {
        try {
            feedbackDto.setEmail(email);
            CustomPrinter.printInfo("Cerere de trimitere de feedback de la: " + userId);
            this.feedbackService.addFeedback(feedbackDto);
            CustomPrinter.printInfo("Cerere de trimitere de feedback pentru: " + userId + " a fost facuta cu succes!");
            return new ResponseEntity<>(HttpStatus.OK);
        }  catch (
                InvalidFieldException e) {
            CustomPrinter.printErr(e.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(e.getErrorRo(), e.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @GetMapping
    public ResponseEntity<?> getFeedback(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole, @RequestParam(required = false, defaultValue = "0") Integer page,
                                         @RequestParam(required = false, defaultValue = "6") Integer size,
                                         @RequestParam(required = false, defaultValue = "") String email
    ) {

        CustomPrinter.printInfo("Cerere noua cu de la: " + userId);
        try {
            if (!Objects.equals(userRole, "admin")) {
                return new ResponseEntity<>(new ErrorDto("Doar adminul poate face aceasta operatie!", "Only the admin can perform this operation!"), HttpStatus.FORBIDDEN);
            }
            FeedbackResponseDto responseGetCode = this.feedbackService.getFeedback(page, size, email);
            CustomPrinter.printSuccess("The feedback of the " + userId + " user was made successfully.");
            return new ResponseEntity<>(responseGetCode, HttpStatus.OK);
        }  catch (InvalidFieldException invalidFieldException) {
            CustomPrinter.printErr("Cererea de cautare cod a generat eroarea: " + invalidFieldException.getErrorRo());
            return new ResponseEntity<>(new ErrorDto(invalidFieldException.getErrorRo(), invalidFieldException.getErrorEng()), HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@RequestHeader("X-User-Id") String userId, @RequestHeader("X-User-Role") String userRole, @PathVariable String id) {
        try {
            if (!Objects.equals(userRole, "admin")) {
                return new ResponseEntity<>(new ErrorDto("Doar adminul poate face aceasta operatie!", "Only the admin can perform this operation!"), HttpStatus.FORBIDDEN);
            }
            CustomPrinter.printInfo("Utilizatorul: " + userId + " a solicitat stergerea feedback-ului cu id-ul: " + id);
            this.feedbackService.delete(id);
            CustomPrinter.printInfo("Utilizatorul: " + userId + " a realizat cu succes stergerea feedback-ului cu id-ul: " + id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NotFoundException notFoundException) {
            return new ResponseEntity<>(new ErrorDto(notFoundException.getErrorRo(), notFoundException.getErrorEng()), HttpStatus.NOT_FOUND);
        }
    }
}
