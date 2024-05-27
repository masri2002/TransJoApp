package com.dashbrod.adminsDashbord.Repo;


import com.dashbrod.adminsDashbord.Model.Route;
import com.dashbrod.adminsDashbord.Model.Stop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StopRepository extends JpaRepository<Stop,Long> {
    List<Stop> findByRoute(Route route);
    @Query("SELECT s FROM Stop s GROUP BY s.id, s.latitude, s.longitude, s.route")
    List<Stop> findDistinctByStopName();
    Stop findByStopName(String name);
}
