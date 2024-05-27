package com.dashbrod.adminsDashbord.controller;

import com.dashbrod.adminsDashbord.Model.*;
import com.dashbrod.adminsDashbord.Repo.*;
import com.dashbrod.adminsDashbord.service.AdminService;
import com.dashbrod.adminsDashbord.service.BusService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminsController {
    @Autowired
    private UserRepository repo;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RouteRepository repository;
    @Autowired
    private StopRepository Stoprepo;
    @Autowired
    private UserReportRepository userReportRepository;
    @Autowired
    private VerificationTokenRepository verificationTokenRepository;
    @Autowired
    private AdminService adminService;

    @Autowired
    private BusService busService;


    @GetMapping("")
    public String ShowIndexPage() {
        return "Home/index";
    }

    @GetMapping("/home")
    public String AfterLogin(Model model, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
            String name = ((UserDetails) authentication.getPrincipal()).getUsername();
            model.addAttribute("name", name);
        }
        List<UserReport> userReports = userReportRepository.findAll();
        model.addAttribute("userReports", userReports);
        model.addAttribute("Count", adminService.counts());
        return "Home/HomePage";
    }

    @GetMapping("/users")
    public String getAllusers(Model model) {
        model.addAttribute("Count",adminService.counts());
        List<User> users = repo.findByRole(Role.PASSENGER);
        model.addAttribute("users", users);
        return "Users/Users";
    }

    @GetMapping("/modify/{userId}")
    public String modifyUser(@PathVariable Long userId, Model model) {
        Optional<User> optionalUser = repo.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRole(Role.PASSENGER);
            user.setEmail(user.getEmail());
            model.addAttribute("user", user);
        } else {

        }
        return "Users/moduser";
    }
    @GetMapping("/delete-user/{id}")
    public String deletePassengerById(@PathVariable Long id){
        Optional<User> user = repo.findById(id);

        userReportRepository.deleteByUser(user.get());
        repo.deleteById(id);
        verificationTokenRepository.deleteById(id);
        return "redirect:/admin/users";
    }
    @PostMapping("/updateUser")
    public String updateUser(@ModelAttribute User user) {
        User existingUser = repo.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + user.getId()));
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(existingUser);
        return "redirect:/admin/users";
    }
    @GetMapping("/routes")
    public String showRoutePage(Model model){
        model.addAttribute("Count",adminService.counts());
        List<Route> getAllRoutes =repository.findAll();
        model.addAttribute("routes", getAllRoutes);
        return "route/routes";
    }
    @GetMapping("/routes/add")
    public String showAddStopPointPage(Model model) {
        Route route = new Route();
        model.addAttribute("routes", route);
        return "route/addroute";
    }
    @PostMapping("/routes/add")
    public String addRoute(@ModelAttribute("routes") Route route, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "route/addroute";
        }
        Route savedRoute = repository.save(route);
        if (route.getStopPoints() != null) {
            for (Stop stop : route.getStopPoints()) {
                stop.setRoute(savedRoute);
                Stoprepo.save(stop);
            }
        }

        return "redirect:/admin/routes";
    }
    @GetMapping("/drivers")
    public String showDriverPage(Model model){

        model.addAttribute("Count",adminService.counts());
        List<User> getAllDrivers = repo.findByRole(Role.DRIVER);
        model.addAttribute("drivers",getAllDrivers);
        return "Driver/driver";
    }
    @GetMapping("/modify-driver/{userId}")
    public String modifyDriver(@PathVariable Long userId, Model model) {
        Optional<User> optionalUser = repo.findById(userId);
        if (optionalUser.isPresent()) {
            User user = optionalUser.get();
            user.setRole(Role.DRIVER);
            user.setEmail(user.getEmail());

            model.addAttribute("user", user);
        } else {

        }
        return "Driver/moddriver";
    }
    @GetMapping("/delete-driver/{userId}")
    public String deleteDriverById(@PathVariable("userId") Long userId) {
        Optional<User> user = repo.findById(userId);
        if (user.isPresent()) {
            Optional<Bus> bus = busRepository.findByDriver(user.get());
            bus.ifPresent(bus1 -> {
                bus1.setDriver(null);
                busRepository.save(bus1);
            });
            repo.deleteById(userId);
        }
        return "redirect:/admin/drivers";
    }

    @PostMapping("/updateDriver")
    public String updateDriver(@ModelAttribute User user) {
        User existingUser = repo.findById(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid user Id:" + user.getId()));
        existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(existingUser);
        return "redirect:/admin/drivers";
    }

    @GetMapping("/drivers/add")
    public String showAddDriverForm(Model model) {
        model.addAttribute("user", new User());
        return "Driver/adddriver";
    }
    @PostMapping("/drivers/added")
    public String addDriver(User user, RedirectAttributes redirectAttributes) {
        Optional<User> existingUser = repo.findByEmail(user.getEmail());
        if (existingUser.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "There is already a driver registered with the email " + user.getEmail());
            return "redirect:/admin/drivers/add";
        }
        Optional<User> existingUserByUsername = repo.findByEmail(user.getEmail());
        if (existingUserByUsername.isPresent()) {
            redirectAttributes.addFlashAttribute("error", "This email Already Exists  " + user.getEmail());
            return "redirect:/admin/drivers/add";
        }
        user.setRole(Role.DRIVER);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        repo.save(user);
        redirectAttributes.addFlashAttribute("success", "Driver added successfully!");
        return "redirect:/admin/drivers";
    }

    @GetMapping("/buses")
public String ShowBusesPage(Model model){
        model.addAttribute("Count",adminService.counts());
    List<Bus> findAllBuses =busRepository.findAll();
  model.addAttribute("buses",findAllBuses);
  return "Bus/buses";
}
    @GetMapping("/buses/add")
    public String showAddBusForm(Model model) {
        List<User> drivers = repo.findDriversNotAssignedToBus();
        List<Route> routes = repository.findAll();
        model.addAttribute("drivers", drivers);
        model.addAttribute("routes", routes);
        model.addAttribute("bus", new Bus());
        return "Bus/addbus";
    }

    @PostMapping("/buses/add")
    public String addBus(@ModelAttribute Bus bus, Model model) {
        System.out.println("addBus method is called");
        User driver = repo.findById(bus.getDriver().getId()).orElse(null);
        bus.setDriver(driver);
        Route route = repository.findById(bus.getRoute().getId()).orElse(null);
        bus.setRoute(route);
        bus.setLat(0);
        bus.setLng(0);
        bus.setCurrCapacity(0L);
        bus.setWorking(false);
        bus.setCurrCapacity(bus.getCapacity());
        busRepository.save(bus);

        return "redirect:/admin/buses/add";
    }
}