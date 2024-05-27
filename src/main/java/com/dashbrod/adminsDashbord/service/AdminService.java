package com.dashbrod.adminsDashbord.service;

import com.dashbrod.adminsDashbord.Model.Role;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Repo.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private UserReportRepository userReportRepository;

public  int GetPassengerCount(){
    List<User> users=userRepository.findByRole(Role.PASSENGER);
    return (users.isEmpty())? 0 : users.size();
}
    public  int GetDriverCount(){
        List<User> users=userRepository.findByRole(Role.DRIVER);
        return (users.isEmpty())? 0 : users.size();
    }
    public  int GetRouteCount(){
    return routeRepository.findAll().size();
    }
    public  int GetBusesCount(){
        return busRepository.findAll().size();
    }
    @Transactional
    public void deletePassengerById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        if (user.getRole() != Role.PASSENGER) {
            throw new RuntimeException("User is not a passenger!!");
        }
        userReportRepository.deleteByUser(user);

        userRepository.deleteById(id);
    }
    public Map<String ,Integer> counts(){
    Map<String ,Integer> counts= new HashMap();
    counts.put("passenger",GetPassengerCount());
    counts.put("drivers",GetDriverCount());
    counts.put("routes",GetRouteCount());
    counts.put("buses",GetBusesCount());
    counts.get("passenger");
    return  counts;
    }
}
