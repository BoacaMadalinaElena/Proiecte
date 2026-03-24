package org.example.dto;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FeedbackResponseDto {
    private List<FeedbackDto> list;
    private int next;
    private int prev;
}
