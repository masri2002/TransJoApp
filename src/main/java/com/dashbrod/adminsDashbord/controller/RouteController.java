package com.dashbrod.adminsDashbord.controller;

import com.dashbrod.adminsDashbord.DTO.BusDetailsDto;
import com.dashbrod.adminsDashbord.DTO.RouteDto;
import com.dashbrod.adminsDashbord.DTO.ShearchRouteDto;
import com.dashbrod.adminsDashbord.DTO.userLocationDto;
import com.dashbrod.adminsDashbord.Model.Bus;

import com.dashbrod.adminsDashbord.Model.Route;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Repo.BusRepository;
import com.dashbrod.adminsDashbord.Repo.RouteRepository;
import com.dashbrod.adminsDashbord.Repo.UserRepository;
import com.dashbrod.adminsDashbord.service.RouteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/route")
public class RouteController {

    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private RouteService routeService;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/routes")
    public ResponseEntity<?> getAllRoutesDetails()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();

        return ResponseEntity.ok(routeService.getAllRoutes());
    }
    @GetMapping("/searchRoute")
    public ResponseEntity<?> searchRoutes(@RequestBody ShearchRouteDto routeDto) {
        String start =routeDto.getStart();
        String end=routeDto.getEnd();
        List<Route> directRoutes = routeRepository.findByStartNameAndEndName(start,end);
        List<Route> directRoutes2 =routeRepository.findByStartNameAndEndName(end,start);
        if (!directRoutes.isEmpty()) {
            return new ResponseEntity<>(directRoutes, HttpStatus.OK);
        }else if(!directRoutes2.isEmpty()){
            return new ResponseEntity<>(directRoutes2, HttpStatus.OK);
        }else {
            List<Route> routes=routeService.ReturnRouteByStartAndPoint(start,end);
            List<Route>routes1=routeService.ReturnRouteByStartAndPoint(end,start);
            if(!routes.isEmpty()){
                return new ResponseEntity<>(routes, HttpStatus.OK);
            } else if (!routes1.isEmpty()) {
                return new ResponseEntity<>(routes1, HttpStatus.OK);
            }
        }
        List<Route> routesWithIntersection = routeService.findRoutesWithIntersectionPoints(start,end);
        List<Route> routesWithIntersection1 = routeService.findRoutesWithIntersectionPoints(end,start);
        if (!routesWithIntersection.isEmpty()) {
            return new ResponseEntity<>(routesWithIntersection, HttpStatus.OK);
        } else if (!routesWithIntersection1.isEmpty()) {
            return new ResponseEntity<>(routesWithIntersection1, HttpStatus.OK);
        }
        return ResponseEntity.badRequest().body("There is No Any Route");
    }
    @GetMapping("/{id}")
    public ResponseEntity<?> getRouteById2(@PathVariable Long id, HttpSession session) {
               Optional<Route> routeOptional=routeRepository.findById(id);
        if (!routeOptional.isPresent()) {
            return ResponseEntity.badRequest().body("No Route With id "+id);
        }
        Map<String, Object> responses = new HashMap<>();
        List<Bus> buses = busRepository.findByRouteAndWorking(routeOptional.get(), true);
        List<BusDetailsDto> busDtos = buses.stream()
                .map(bus -> new BusDetailsDto(
                        bus.getId(),
                        bus.getOwnerName(),
                        bus.isWorking(),
                        bus.getDriver().getFullName(),
                        bus.getRoute().getStartName() + " to " + bus.getRoute().getEndName(), // Ensure there's a space around "to"
                        bus.getCapacity(),
                        bus.getCurrCapacity(),
                        bus.getLat(),
                        bus.getLng()))
                .collect(Collectors.toList());
        responses.put("route", routeOptional.get());
        responses.put("buses", busDtos);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/routes/{id}")
    public ResponseEntity<?> getRouteById(@PathVariable Long id, HttpSession session) {
        RouteDto route = routeService.finRouteDtoby(id);
        Route route1=routeService.findRouteById(id).orElseThrow();
        if (route==null) {
            return ResponseEntity.badRequest().body("No Route With id "+id);
        }
        Map<String, Object> responses = new HashMap<>();
        List<Bus> buses = busRepository.findByRouteAndWorking(route1, true);
        List<BusDetailsDto> busDtos = buses.stream()
                .map(bus -> new BusDetailsDto(
                        bus.getId(),
                        bus.getOwnerName(),
                        bus.isWorking(),
                        bus.getDriver().getFullName(),
                        bus.getRoute().getStartName() + " to " + bus.getRoute().getEndName(), // Ensure there's a space around "to"
                        bus.getCapacity(),
                        bus.getCurrCapacity(),
                        bus.getLat(),
                        bus.getLng()))
                .collect(Collectors.toList());
        responses.put("route", route);
        responses.put("buses", busDtos);
        return ResponseEntity.ok(responses);
    }
    @GetMapping("/routes/name")
    public ResponseEntity<?> getRouteByStartAndEnd(@RequestBody ShearchRouteDto routeDto, HttpSession session) {
        Route route = routeService.findRouteByStartAndEnd(routeDto.getStart(),routeDto.getEnd());
        Route route2=routeService.findRouteByStartAndEnd(routeDto.getEnd(),routeDto.getStart());


        if (route!=null) {
            Map<String, Object> responses = new HashMap<>();
            List<Bus> buses = busRepository.findByRouteAndWorking(route, true);
            List<BusDetailsDto> busDtos = buses.stream()
                    .map(bus -> new BusDetailsDto(
                            bus.getId(),
                            bus.getOwnerName(),
                            bus.isWorking(),
                            bus.getDriver().getFullName(),
                            bus.getRoute().getStartName() + " to " + bus.getRoute().getEndName(),
                            bus.getCapacity(),
                            bus.getCurrCapacity(),
                            bus.getLat(),
                            bus.getLng()))
                    .collect(Collectors.toList());
            responses.put("route", route);
            responses.put("buses", busDtos);
            return ResponseEntity.ok(responses);
        } else if (route2!=null) {
            Map<String, Object> responses = new HashMap<>();
            List<Bus> buses = busRepository.findByRouteAndWorking(route ==null ? route2 : route, true);
            List<BusDetailsDto> busDtos = buses.stream()
                    .map(bus -> new BusDetailsDto(
                            bus.getId(),
                            bus.getOwnerName(),
                            bus.isWorking(),
                            bus.getDriver().getFullName(),
                            bus.getRoute().getStartName() + " to " + bus.getRoute().getEndName(),
                            bus.getCapacity(),
                            bus.getCurrCapacity(),
                            bus.getLat(),
                            bus.getLng()))
                    .collect(Collectors.toList());
            responses.put("route", route2);
            responses.put("buses", busDtos);
            return ResponseEntity.ok(responses);
        }


        return ResponseEntity.badRequest().body("No Route from "+routeDto.getStart() +" To "+routeDto.getEnd() +" Search For Intersection points");
    }
    @PostMapping("/FavoriteRoute/{routeId}")
    public ResponseEntity<?> addFavoriteRoute(@PathVariable Long routeId, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        boolean added = routeService.addFavoriteRoute(user, routeId);
        if (added) {
            return ResponseEntity.ok("Route added to favorites");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid route ID");
        }
    }
    @DeleteMapping("/FavoriteRoute/{routeId}")
    public ResponseEntity<?> removeFavoriteRoute(@PathVariable Long routeId, HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();

        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        boolean removed = routeService.deleteFavoriteRouteById(user.getId(), routeId);

        if (removed) {
            return ResponseEntity.ok("Route removed from favorites");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid route ID");
        }
    }
    @GetMapping("/favoriteRoutes")
    public ResponseEntity<?> getFavoriteRoutes(HttpSession session) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not logged in");
        }

        List<Route> favoriteRoutes = routeService.getFavoriteRoutes(user);
        return ResponseEntity.ok(favoriteRoutes);
    }
    @GetMapping("/bus/{id}")
    public  ResponseEntity<?> getETA(@PathVariable Long id , @RequestBody userLocationDto userLocationDto){
        Bus bus =busRepository.findById(id).orElseThrow();
        double time=routeService.getETA(bus ,userLocationDto.getLat(),userLocationDto.getLng());
        return ResponseEntity.ok("the ETA For  this bus is"+ time);
    }
}

