package com.dashbrod.adminsDashbord.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusDetailsDto {
    private Long id;
    private String ownerName;
    private boolean isWorking;
    private String driverName;
    private String routeName;
    private Long capacity;
    private Long currentCapacity;
    private double lat;
    private double lng;

}