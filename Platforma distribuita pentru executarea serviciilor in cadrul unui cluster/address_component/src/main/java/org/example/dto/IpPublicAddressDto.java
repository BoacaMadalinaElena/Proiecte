package org.example.dto;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ipPublicAddress",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"ip", "port"}, name = "UK_IP_PORT")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class IpPublicAddressDto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "ip", length = 100, nullable = false)
    private String ip;

    @Column(name = "port", length = 100, nullable = false)
    private int port;

    @Column(name = "dateTime")
    private LocalDateTime dateTime;
}
