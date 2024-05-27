package com.dashbrod.adminsDashbord.Repo;


import com.dashbrod.adminsDashbord.Model.Route;
import com.dashbrod.adminsDashbord.Model.Stop;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RouteRepository extends JpaRepository<Route,Long> {
    Optional<Route> findById (Long id);

    List<Route> findByStartName(String start);

    List<Route> findByEndName(String end);


    Route findRouteByStartNameAndEndName(String start ,String end);
    List<Route> findByStartNameAndStopPointsStopName(String start, String stop);

    List<Route> findByStartNameAndEndName(String intersectionPoint, String intersectionPoint1);
}
