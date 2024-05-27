package com.dashbrod.adminsDashbord.Repo;

import com.dashbrod.adminsDashbord.Model.Bus;
import com.dashbrod.adminsDashbord.Model.Route;
import com.dashbrod.adminsDashbord.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {
    @Query("SELECT b FROM Bus b WHERE b.route = :route AND b.isWorking = :working")
    List<Bus> findByRouteAndWorking(@Param("route") Route route, @Param("working") boolean working);
    Optional <Bus> findByDriver(User user);
  boolean existsBusByDriver(User user);
}
