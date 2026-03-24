package org.example.dto;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CodeRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column( length = 100, nullable = false)
    private String title;

    @Column( length = 5000, nullable = false)
    private String description;

    private String userId;
    private String type;
    private String isPublic;
    private int typeRun;
}
