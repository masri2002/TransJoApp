package com.dashbrod.adminsDashbord.service;

import com.dashbrod.adminsDashbord.DTO.LoginDetailsDto;
import com.dashbrod.adminsDashbord.DTO.LoginDto;
import com.dashbrod.adminsDashbord.DTO.UserDetailsDto;
import com.dashbrod.adminsDashbord.DTO.UserSettingsDto;
import com.dashbrod.adminsDashbord.Model.AuthnResponse;
import com.dashbrod.adminsDashbord.Model.Role;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Model.VerificationToken;
import com.dashbrod.adminsDashbord.Repo.UserRepository;
import com.dashbrod.adminsDashbord.Repo.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private AuthenticationManager authenticationManager;

//    public AuthnResponse registerNewUser(UserDetailsDto registrationDto) {
//        User user = new User();
//        user.setFullName(registrationDto.getName());
//        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
//        user.setEmail(registrationDto.getEmail());
//        user.setRole(Role.PASSENGER);
//        userRepository.save(user);
////        String token = jwtService.generateToken(user);
////        return new AuthnResponse(token);
//    }
//    public AuthnResponse Authen (LoginDto loginDto){
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(
//                        loginDto.getEmail(),
//                        loginDto.getPassword()
//                )
//        );
//        User user =userRepository.findByEmail(loginDto.getEmail()).orElseThrow();
////        String token=jwtService.generateToken(user);
//         return  new AuthnResponse(token);
//    }
    public Boolean login(String username, String password){
        Optional<User> userOptional = userRepository.findByEmail(username);
        if(userOptional.isPresent() && passwordEncoder.matches(password, userOptional.get().getPassword())) {

            return true;
        } else {
            return false;
        }
    }
    public UserSettingsDto UserSettings (User user){
        UserSettingsDto settingsDto =new UserSettingsDto();
        settingsDto.setName(user.getFullName());
        settingsDto.setEmail(user.getEmail());
        return  settingsDto;
    }
public LoginDetailsDto loginDetailsDto (User user ,String passWord){
        LoginDetailsDto loginDetailsDt =new LoginDetailsDto();
        loginDetailsDt.setEmail(user.getEmail());
        loginDetailsDt.setPassword(passWord);
       return  loginDetailsDt;
}
}
