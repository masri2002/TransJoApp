package com.dashbrod.adminsDashbord.Repo;

import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {
    Optional<VerificationToken> findByVerificationCode(String verificationCode);
    Optional<VerificationToken> findByUserEmail(String email);

    Optional<VerificationToken> findByUser(User user);
}