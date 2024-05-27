package com.dashbrod.adminsDashbord.DTO;

import com.dashbrod.adminsDashbord.Model.Stop;

import lombok.Data;

import java.util.List;

@Data
public class RouteDto {

    private Long id;


    private String startName;


    private String endName;


    private double fare;


    private List<Stop> stopPoints;

    private boolean isFav;
}
