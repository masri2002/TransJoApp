package com.dashbrod.adminsDashbord.Repo;


import com.dashbrod.adminsDashbord.Model.Role;
import com.dashbrod.adminsDashbord.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.*;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    List<User> findByRole(Role role);
    @Query("SELECT u FROM User u WHERE u.role = 'DRIVER' AND u.id NOT IN (SELECT b.driver.id FROM Bus b WHERE b.driver IS NOT NULL)")
    List<User> findDriversNotAssignedToBus();


    Optional<User> findByEmail(String email);
}
