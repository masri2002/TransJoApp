package com.dashbrod.adminsDashbord.service;

import com.dashbrod.adminsDashbord.DTO.BusLocationDto;
import com.dashbrod.adminsDashbord.Model.Bus;
import com.dashbrod.adminsDashbord.Model.Role;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Repo.BusRepository;
import com.dashbrod.adminsDashbord.Repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class BusService {

    private final BusRepository busRepository;
 private UserRepository userRepository;
    @Autowired
    public BusService(BusRepository busRepository) {
        this.busRepository = busRepository;
    }
    public BusLocationDto getBusDetails(Long busId) {
        Bus bus = busRepository.findById(busId).orElseThrow(() -> new RuntimeException("Bus not found"));
        User driver = bus.getDriver();
        BusLocationDto dto = new BusLocationDto();
        dto.setLat(bus.getLat());
        dto.setLng(bus.getLng());
        return dto;
    }
    public List<User> returnDriverNotAssagintobus(){
        List<User> finddriver =userRepository.findByRole(Role.DRIVER);
        List<User> drivers=new ArrayList<>();
        for(User user : finddriver){
            if(busRepository.existsBusByDriver(user)){
                continue;
            }else {
                drivers.add(user);
            }
        }
    return drivers;
    }

}