package com.example.httpnode.dto;

import com.example.httpnode.dto.enums.RoleType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "username", name = "UK_USERNAME"),
                @UniqueConstraint(columnNames = "email", name = "UK_EMAIL")
        }
)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class UserDto {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private String id;

    @Column(name = "username", length = 100, nullable = false, unique = true)
    private String username;

    @Column(name = "firstName", length = 100, nullable = false, unique = false)
    private String firstName;

    @Column(name = "lastName", length = 100, nullable = false, unique = false)
    private String lastName;

    @Column(name = "email", length = 100, nullable = false, unique = true)
    private String email;

    @Column(name = "description", length = 500, nullable = false, unique = false)
    private String description;

    @Column(name = "password", length = 100, nullable = false)
    private String password;

    @Column(name = "role", length = 1,nullable = false)
    private RoleType role;

    @Column(name = "language", length = 7,nullable = false)
    String language;
}
