package org.example.dto;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "code_message")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class CodeMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "file_name", length = 200, nullable = false)
    private String fileName;

    @Column(name = "content_string", length = 65535, nullable = false)
    private String contentString;

    @Column(name = "is_start_up")
    private boolean isStartUp;

    @Column(name = "is_code_class")
    private boolean isCodeClass;

    // 10 mb
    @Lob
    @Column(name = "content_bytes", length = 10*1048576)
    private byte[] contentBytes;

    @Column(name = "code_record", length = 100, nullable = false)
    private String codeRecord;
}
