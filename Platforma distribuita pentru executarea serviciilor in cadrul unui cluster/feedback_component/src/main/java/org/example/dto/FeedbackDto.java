package org.example.dto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FeedbackDto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column( length = 200, nullable = false)
    private String email;

    @Column( length = 5000, nullable = false)
    private String content;

    @Column( length = 200, nullable = false)
    private String subject;
}
