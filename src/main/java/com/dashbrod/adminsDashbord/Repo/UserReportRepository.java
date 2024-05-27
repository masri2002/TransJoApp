package com.dashbrod.adminsDashbord.Repo;


import com.dashbrod.adminsDashbord.Model.Role;
import com.dashbrod.adminsDashbord.Model.User;
import com.dashbrod.adminsDashbord.Model.UserReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface UserReportRepository extends JpaRepository<UserReport,Long> {
    void deleteByUser(User user);

}
