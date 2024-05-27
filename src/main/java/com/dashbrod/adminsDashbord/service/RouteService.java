package com.dashbrod.adminsDashbord.service;

import com.dashbrod.adminsDashbord.DTO.RouteDto;
import com.dashbrod.adminsDashbord.Model.Bus;
import com.dashbrod.adminsDashbord.Model.Route;
import com.dashbrod.adminsDashbord.Model.Stop;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Repo.RouteRepository;
import com.dashbrod.adminsDashbord.Repo.StopRepository;
import com.dashbrod.adminsDashbord.Repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.sql.Time;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class RouteService {
    @Autowired
    private RouteRepository routeRepository;
    @Autowired
    private StopRepository stopRepository;
    @Autowired
    private UserRepository userRepository;
    public List<RouteDto> getAllRoutes() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        List<Route> allRoutes = routeRepository.findAll();
        User user = userRepository.findByEmail(userEmail).orElseThrow();
        return allRoutes.stream()
                .map(route -> {
                    RouteDto routeDto = new RouteDto();
                    routeDto.setId(route.getId());
                    routeDto.setStartName(route.getStartName());
                    routeDto.setEndName(route.getEndName());
                    routeDto.setFare(route.getFare());
                    routeDto.setStopPoints(route.getStopPoints());
                    routeDto.setFav(user.getFavoriteRoutes().contains(route));
                    return routeDto;
                })
                .collect(Collectors.toList());
    }
    public RouteDto finRouteDtoby(Long id){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userEmail = authentication.getName();
        User user=userRepository.findByEmail(userEmail).orElseThrow();
        Route route =routeRepository.findById(id).orElseThrow();
        RouteDto routeDto=new RouteDto();
        routeDto.setId(route.getId());
        routeDto.setStartName(route.getStartName());
        routeDto.setEndName(route.getEndName());
        routeDto.setFare(route.getFare());
        routeDto.setStopPoints(route.getStopPoints());
        routeDto.setFav(user.getFavoriteRoutes().contains(route));
  return routeDto;
    }
    public Route findRouteByStartAndEnd(String start ,String end){
        Route route =routeRepository.findRouteByStartNameAndEndName(start,end);
        return route;
    }
    public Optional<Route> findRouteById(Long id) {
        return routeRepository.findById(id);
    }
 public  List <Route> ReturnRouteByStartAndPoint(String Start , String point){
        List<Route> Routes =routeRepository.findByStartNameAndStopPointsStopName(Start,point);

        return Routes;
 }
    public List<Route> findRoutesWithIntersectionPoints(String start, String end) { // amman to ar-mtah
        List<Route> startingRoutes = routeRepository.findByStartName(start);
        List<Route> endingRoutes = routeRepository.findByEndName(end);
        Set<Route> routesWithIntersection = new HashSet<>(); // Change to Set
    // amman  zarqa
        for (Route startRoute : startingRoutes) {
            for (Route endRoute : endingRoutes) {
                if (startRoute.getEndName().equalsIgnoreCase(endRoute.getStartName()) && endRoute.getEndName().equalsIgnoreCase(end)) {
                    routesWithIntersection.add(startRoute);
                    routesWithIntersection.add(endRoute);
                }

                List<Stop> startStops = stopRepository.findByRoute(startRoute);
                List<Stop> endStops = stopRepository.findByRoute(endRoute);
//// amman - jser - zarqa     amman sh       irbit --ma--jsr -sahaba
                for (Stop stop1 : startStops) {
                    for (Stop stop2 : endStops) {
                        if (stop1.getStopName().equalsIgnoreCase(stop2.getStopName())) {
                            routesWithIntersection.add(startRoute);
                            routesWithIntersection.add(endRoute);
                            break;
                        }
                    }
                    if (!routesWithIntersection.isEmpty()) {
                        break;
                    }
                }
            }
        }

        return new ArrayList<>(routesWithIntersection);
    }
    public boolean addFavoriteRoute(User user, Long routeId) {
        Optional<Route> routeOptional = routeRepository.findById(routeId);
        if (routeOptional.isPresent()) {
            user.getFavoriteRoutes().add(routeOptional.get());
            userRepository.save(user);
            return true;
        }
        return false;
    }
    public List<Route> getFavoriteRoutes(User user) {
        return user.getFavoriteRoutes();
    }

    public double getETA(Bus bus, double lat, double lng) {
        double earthRadius = 3958.75;
        double dLat = Math.toRadians(bus.getLat() - lat);
        double dLng = Math.toRadians(bus.getLng() - lng);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat)) * Math.cos(Math.toRadians(bus.getLat())) *
                        Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;
        double speed = 30;
        double timeInHours = dist / speed;
        double timeInMinutes = timeInHours * 60;

        return timeInMinutes;
    }

    public boolean removeFavoriteRoute(User user, Long routeId) {
        Route route = routeRepository.findById(routeId).orElse(null);
        if (route == null) {
            return false;
        }

        List<Route> favoriteRoutes = user.getFavoriteRoutes();
        boolean removed = favoriteRoutes.removeIf(r -> r.getId().equals(routeId));
        if (removed) {
            userRepository.save(user);
        }
        return removed;
    }
    @Transactional
    public boolean deleteFavoriteRouteById(Long userId, Long routeId) {
        // Find the user by ID
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            throw new IllegalArgumentException("User not found");
        }

        // Remove the route with the specified ID from the user's favorite routes list
        user.getFavoriteRoutes().removeIf(route -> route.getId().equals(routeId));

        // Save the updated user entity
        userRepository.save(user);
        return true;
    }
}
