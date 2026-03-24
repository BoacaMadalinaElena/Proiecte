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
public class ResponseGetCode {
    private List<ShortCode> listShortCode;
    private int nextPage;
    private int prevPage;
}
