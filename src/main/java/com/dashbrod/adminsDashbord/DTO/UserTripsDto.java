package com.dashbrod.adminsDashbord.DTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserTripsDto {
    private Long id;
    private String from;
    private String to;
    private String driverName;
    private LocalDateTime dateTime;
    private  String userName;
}
