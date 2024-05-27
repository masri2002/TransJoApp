package com.dashbrod.adminsDashbord.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Date and time are required")
    private LocalDateTime dateTime;

    @Column(name = "message")
    @NotBlank(message = "Message cannot be blank")
    @Size(max = 500, message = "Message must not exceed 500 characters")
    private String message;

    @ManyToOne
    @NotNull(message = "User must be specified")
    private User user;
}
