package com.dashbrod.adminsDashbord.service;

import com.dashbrod.adminsDashbord.DTO.PasswordResetDto;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Model.VerificationToken;
import com.dashbrod.adminsDashbord.Repo.UserRepository;
import com.dashbrod.adminsDashbord.Repo.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class VerificationTokenService {

    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private JavaMailSender mailSender;

    public String createOrUpdateVerificationToken(User user) {
        String code = generateRandomCode();
        VerificationToken verificationToken = verificationTokenRepository.findByUser(user)
                .orElse(new VerificationToken());
        verificationToken.setUser(user);
        verificationToken.setVerificationCode(code);
        verificationToken.setVerificationCodeExpiry(LocalDateTime.now().plusMinutes(30)); // Set expiry for 30 minutes ahead
        verificationTokenRepository.save(verificationToken);
        return code;
    }

    private String generateRandomCode() {
        int length = 6; // Length of the verification code
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random random = new Random();
        StringBuilder builder = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            builder.append(characters.charAt(random.nextInt(characters.length())));
        }
        return builder.toString();
    }

    public boolean validateVerificationToken(String code, String email) {
        Optional<VerificationToken> verificationToken = verificationTokenRepository.findByUserEmail(email);
        return verificationToken.isPresent() &&
                verificationToken.get().getVerificationCode().equals(code) &&
                verificationToken.get().getVerificationCodeExpiry().isAfter(LocalDateTime.now());
    }

    public void deleteVerificationToken(String email) {
        verificationTokenRepository.findByUserEmail(email)
                .ifPresent(token -> verificationTokenRepository.delete(token));
    }
    public void sendMail(User user){

        String code = createOrUpdateVerificationToken(user);
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject("TransJo ,Password Reset Request");
        mailMessage.setText("Hello "   + user.getFullName()+"\n"+"Your verification code is: " + code +"\n");
        mailSender.send(mailMessage);
    }
}
