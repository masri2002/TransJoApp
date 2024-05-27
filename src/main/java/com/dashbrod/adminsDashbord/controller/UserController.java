package com.dashbrod.adminsDashbord.controller;

import com.dashbrod.adminsDashbord.DTO.*;
import com.dashbrod.adminsDashbord.Model.*;
import com.dashbrod.adminsDashbord.Repo.*;
import com.dashbrod.adminsDashbord.service.AuthenticationService;
import com.dashbrod.adminsDashbord.service.RouteService;
import com.dashbrod.adminsDashbord.service.UserService;
import com.dashbrod.adminsDashbord.service.VerificationTokenService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import com.dashbrod.adminsDashbord.DTO.EmailDto;
import com.dashbrod.adminsDashbord.DTO.PasswordResetDto;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Repo.UserRepository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private  UserService userService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private UserReportRepository userReportRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private VerificationTokenService verificationTokenService;
   @Autowired
   private AuthenticationService Authen;
    @PostMapping("/register")
    public ResponseEntity<AuthnResponse> register(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(Authen.register(request));
    }
    @PostMapping("/login")
    public ResponseEntity<AuthnResponse> login(
            @RequestBody User request
    ) {
        return ResponseEntity.ok(Authen.authenticate(request));
    }
    @GetMapping("/details")
    public ResponseEntity<?> UserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        System.out.println(user.getEmail());
        return ResponseEntity.ok(userService.UserSettings(user));
    }


    @PostMapping("/reset-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody EmailDto emailDto) {
        Optional<User> userOptional = userRepository.findByEmail(emailDto.getEmail());
        if (!userOptional.isPresent() || userOptional.get().getRole()!=Role.PASSENGER) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        User user = userOptional.get();
         verificationTokenService.sendMail(user);
         return ResponseEntity.ok().body("Verification code sent to email.");
    }
    @PostMapping("/app-reset-password")
    public ResponseEntity<?> requestPasswordResetFromApp(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
      User user =userRepository.findByEmail(userEmail).orElseThrow();
       verificationTokenService.sendMail(user);
        return ResponseEntity.ok().body("Verification code sent to email.");
    }
    @PostMapping("/app-change-password")
    public ResponseEntity<?> changePasswordByApp(@RequestBody AppRestPassword passwordResetDto ,HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        if (!verificationTokenService.validateVerificationToken(passwordResetDto.getCode(), userEmail)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired verification code.");
        }
        session.setAttribute("code",passwordResetDto.getCode());
        return ResponseEntity.ok().body("Correct code.");
    }
@PostMapping("/app-change-password3")
public ResponseEntity<?> changePasswordByApp1(@RequestBody AppRestPassword passwordResetDto ,HttpSession session) {
    String code =  (String )session.getAttribute("code");
    passwordResetDto.setCode(code);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    String userEmail = authentication.getName();
    User user =userRepository.findByEmail(userEmail).orElseThrow();
     if(passwordEncoder.matches(passwordResetDto.getOldPassword(), user.getPassword())) {
        user.setPassword(passwordEncoder.encode(passwordResetDto.getNewPassword()));
        userRepository.save(user);
        verificationTokenService.deleteVerificationToken(user.getEmail());
        return ResponseEntity.ok().body("Password updated successfully.");

    }
    return ResponseEntity.badRequest().body("Your Old Password Not Match ");

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

    @GetMapping("/bus/{busId}/location")
    public ResponseEntity<?> getBusLocation(@PathVariable Long busId , HttpSession session) {
        Optional<Bus> busOptional = busRepository.findById(busId);
        if (!busOptional.isPresent()) {
            return ResponseEntity.notFound().build(); // Bus not found
        }
        Bus bus = busOptional.get();
        session.setAttribute("bus",bus);
        Map<String, Object> location = new HashMap<>();
        location.put("latitude", bus.getLat());
        location.put("longitude", bus.getLng());
        location.put("capacity",bus.getCapacity());
        location.put("current_capacity",bus.getCurrCapacity());
        return ResponseEntity.ok(location);
    }
    @PostMapping("/report")
    public ResponseEntity<?> reportViolation(HttpSession session, @RequestBody ReportMessage reportMessage) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user =userRepository.findByEmail(userEmail).orElseThrow();

        if (user == null) {
            return ResponseEntity.badRequest().body("You have to login first !");
        }
        ZonedDateTime zonedDateTimeInJordan = ZonedDateTime.now(ZoneId.of("Asia/Amman"));
        LocalDateTime localDateTimeInJordan = zonedDateTimeInJordan.toLocalDateTime();
        UserReport userReport = new UserReport();
        userReport.setMessage(reportMessage.getMessage());
        userReport.setUser(user);
        userReport.setDateTime(localDateTimeInJordan);
        userReportRepository.save(userReport);
        return ResponseEntity.ok().body("Report sent successfully");
    }

}