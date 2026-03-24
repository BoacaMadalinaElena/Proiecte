package org.example.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CodeRecordRequest {
    private String title;
    private String description;
    private String userId;
    private List<CodeMessage> codeMessageList;
    private String type;
    private String isPublic;
    private int typeRun;
}
