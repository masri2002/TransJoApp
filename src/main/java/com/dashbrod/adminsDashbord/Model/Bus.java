package com.dashbrod.adminsDashbord.Model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "buses")
public class Bus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ownerName", nullable = false)
    @NotBlank(message = "Owner name is required")
    private String ownerName;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "driver_id")
    private User driver;

    @ManyToOne
    @JoinColumn(name = "route_id")
    @NotNull(message = "Route must be specified")
    private Route route;

    @Column(name = "working")
    private boolean isWorking;

    @Column(name = "capacity")
    @NotNull(message = "Capacity must be specified")
    @Positive(message = "Capacity must be a positive number")
    private Long capacity;

    @Column(name = "currentCapacity")
    @NotNull(message = "Current capacity must be specified")
    @PositiveOrZero(message = "Current capacity must not be negative")
    private Long currCapacity;

    @DecimalMin(value = "-90.0", message = "Latitude must be greater than or equal to -90")
    @DecimalMax(value = "90.0", message = "Latitude must be less than or equal to 90")
    private double lat;

    @DecimalMin(value = "-180.0", message = "Longitude must be greater than or equal to -180")
    @DecimalMax(value = "180.0", message = "Longitude must be less than or equal to 180")
    private double lng;
}
