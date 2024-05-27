package com.dashbrod.adminsDashbord.Confugeration;
import com.dashbrod.adminsDashbord.Model.Bus;
import com.dashbrod.adminsDashbord.Model.Role;
import com.dashbrod.adminsDashbord.Model.Token;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Repo.BusRepository;
import com.dashbrod.adminsDashbord.Repo.TokenRepo;
import com.dashbrod.adminsDashbord.Repo.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;

@Configuration
public class CustomLogoutHandler implements LogoutHandler {


    private UserRepository userRepository;


    private BusRepository busRepository;


    private TokenRepo tokenRepository;

    public CustomLogoutHandler(UserRepository userRepository, BusRepository busRepository, TokenRepo tokenRepository) {
        this.userRepository = userRepository;
        this.busRepository = busRepository;
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return;
        }

        String token = authHeader.substring(7);
        Token storedToken = tokenRepository.findByAccessToken(token).orElse(null);

        // If the token exists, set it as logged out
        if (storedToken != null) {
            storedToken.setLoggedOut(true);
            tokenRepository.save(storedToken);
        }
    }

}

