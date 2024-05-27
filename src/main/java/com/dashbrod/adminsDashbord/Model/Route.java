package com.dashbrod.adminsDashbord.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "start_point", nullable = false)
    @NotBlank(message = "Start point name is required")
    private String startName;

    @Column(name = "end_point", nullable = false)
    @NotBlank(message = "End point name is required")
    private String endName;

    @Column(nullable = false)
    @Positive(message = "Fare must be positive")
    private double fare;

    @OneToMany(mappedBy = "route")
    @JsonManagedReference
    private List<Stop> stopPoints;


}
