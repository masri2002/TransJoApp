package com.dashbrod.adminsDashbord.controller;

import com.dashbrod.adminsDashbord.Confugeration.CustomLogoutHandler;
import com.dashbrod.adminsDashbord.DTO.*;
import com.dashbrod.adminsDashbord.Model.AuthnResponse;
import com.dashbrod.adminsDashbord.Model.Bus;
import com.dashbrod.adminsDashbord.Model.Role;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Repo.BusRepository;
import com.dashbrod.adminsDashbord.Repo.UserRepository;
import com.dashbrod.adminsDashbord.service.AuthenticationService;
import com.dashbrod.adminsDashbord.service.VerificationTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/driver")
public class DriverController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private VerificationTokenService verificationTokenService;
    @Autowired
    private AuthenticationService Authen;
    @Autowired
    private CustomLogoutHandler customLogoutHandler;
    @PostMapping("/login")
    public ResponseEntity<AuthnResponse> login(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(Authen.authenticate(request));
    }
    @GetMapping("/getDriver")
    public ResponseEntity<UserSettingsDto> getDriver(HttpSession session , UserSettingsDto settingsDto){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        settingsDto.setEmail(user.getEmail());
        settingsDto.setName(user.getFullName());
        return ResponseEntity.ok(settingsDto);
    }
    @PostMapping("/toggleWorkingStatus")
    public ResponseEntity<?> toggleWorkingStatus(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User driver = userRepository.findByEmail(userEmail).orElseThrow();
        Optional<Bus> busOptional = busRepository.findByDriver(driver);

        if (!busOptional.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Bus for the logged-in driver not found.");
        }

        Bus bus = busOptional.get();
        bus.setWorking(!bus.isWorking());
        busRepository.save(bus);

        return ResponseEntity.ok().body("Bus working status toggled successfully.");
    }
    @PostMapping("add-pas")
    public  ResponseEntity<?> addPassengerToBus(HttpSession session){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Optional<Bus> busOptional =busRepository.findByDriver(user);
        Bus bus =busOptional.get();
        if(bus==null || bus.isWorking()==false){
            return ResponseEntity.badRequest().body("You are Not Assagin to Bus Or You NotWorking now");
        }
        if (bus.getCurrCapacity()==bus.getCapacity()){
            return ResponseEntity.badRequest().body("Your bus Full You cant add new Passenger");
        }
        bus.setCurrCapacity(bus.getCurrCapacity()+1L);
        busRepository.save(bus);
        return ResponseEntity.ok().body(bus.getCurrCapacity());
    }
    @PostMapping("drop-pas")
    public  ResponseEntity<?> dropPassengerToBus(HttpSession session){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Optional<Bus> busOptional =busRepository.findByDriver(user);
        Bus bus =busOptional.get();
        if(bus==null){
            return ResponseEntity.badRequest().body("You are Not Assagin to Bus");
        }
        if (bus.getCurrCapacity()==0 || bus.isWorking()==false){
            return ResponseEntity.badRequest().body("Your bus haven't any passenger");
        }
        bus.setCurrCapacity(bus.getCurrCapacity()-1L);
        busRepository.save(bus);
        return ResponseEntity.ok().body(bus.getCurrCapacity());
    }
    @PostMapping("rest-pas")
    public  ResponseEntity<?> restPassengerToBus( ){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Optional<Bus> busOptional =busRepository.findByDriver(user);
        Bus bus =busOptional.get();
        if(bus==null || bus.isWorking()==false){
            return ResponseEntity.badRequest().body("You are Not Assagin to Bus");
        }
        if (bus.getCurrCapacity()==0){
            return ResponseEntity.badRequest().body("Your bus haven't any passenger");
        }
        bus.setCurrCapacity(0L);
        busRepository.save(bus);
        return ResponseEntity.ok().body(bus.getCurrCapacity());
    }
    @PostMapping("/updateBusLocation")
    public ResponseEntity<?> updateBusLocation(HttpSession session, @RequestBody BusLocationDto busLocationDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        Optional<Bus> busOptional = busRepository.findByDriver(user);
        if (!busOptional.isPresent()) {
            return ResponseEntity.badRequest().body("No bus assigned to this driver.");
        }

        Bus bus = busOptional.get();
        bus.setLat(busLocationDto.getLat());
        bus.setLng(busLocationDto.getLng());

        busRepository.save(bus);

        return ResponseEntity.ok().body("Bus location updated successfully.");
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody EmailDto emailDto) {
        Optional<User> userOptional = userRepository.findByEmail(emailDto.getEmail());
        if (!userOptional.isPresent() || userOptional.get().getRole()!=Role.DRIVER) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Driver not found.");
        }
        User user = userOptional.get();
        String code = verificationTokenService.createOrUpdateVerificationToken(user);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("TransJo ,Password Reset Request");
        mailMessage.setText("Hello "   + user.getFullName()+"\n"+"Your verification code is: " + code +"\n");
        mailSender.send(mailMessage);

        return ResponseEntity.ok().body("Verification code sent to email.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody PasswordResetDto passwordResetDto) {
        if (!verificationTokenService.validateVerificationToken(passwordResetDto.getCode(), passwordResetDto.getEmail())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification code.");
        }

        Optional<User> userOptional = userRepository.findByEmail(passwordResetDto.getEmail());
        if (!userOptional.isPresent()) {
            return ResponseEntity.badRequest().body("User not found.");
        }

        User user = userOptional.get();
        user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
        userRepository.save(user);


        verificationTokenService.deleteVerificationToken(passwordResetDto.getEmail());

        return ResponseEntity.ok().body("Password updated successfully.");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user =userRepository.findByEmail(userEmail).orElseThrow();
        Bus bus=busRepository.findByDriver(user).orElseThrow();
        bus.setWorking(false);
        busRepository.save(bus);
        if (authentication != null) {
            customLogoutHandler.logout(request, response, authentication);
            return ResponseEntity.ok().body("Logged out successfully.");
        } else {
            return ResponseEntity.badRequest().body("No user is currently logged in.");
        }
    }


}
