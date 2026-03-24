package org.example.web_socket_management.dto;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class IpPublicAddressDto {
    private String id;
    private String ip;
    private int port;
}
