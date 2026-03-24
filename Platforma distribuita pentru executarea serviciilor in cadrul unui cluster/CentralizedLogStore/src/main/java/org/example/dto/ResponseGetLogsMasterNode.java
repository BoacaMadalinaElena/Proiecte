package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ResponseGetLogsMasterNode {
    private List<LogsMessageMasterNode> content;
    private int nextPage;
    private int prevPage;
}
