package com.dashbrod.adminsDashbord;

import com.dashbrod.adminsDashbord.service.AdminService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class bcryot {

    public static void main(String [] args) {
        BCryptPasswordEncoder passwordEncoder= new BCryptPasswordEncoder();
        AdminService adminService= new AdminService();
        System.out.println(passwordEncoder.encode("Calr123@"));
        System.out.println(passwordEncoder.encode("Ma123456"));
        System.out.println(passwordEncoder.encode("123456"));
        System.out.println(adminService.GetPassengerCount());
    }
}
