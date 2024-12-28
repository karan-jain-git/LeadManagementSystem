package com.udaan.leadmanagement.repository;

import com.udaan.leadmanagement.enums.LeadStatus;
import com.udaan.leadmanagement.model.RestaurantLead;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RestaurantLeadRepository extends JpaRepository<RestaurantLead, Long> {
    @Query("SELECT r FROM RestaurantLead r WHERE r.nextCallDate BETWEEN :startOfDay AND :endOfDay " +
            "AND r.status IN :statuses")
    List<RestaurantLead> findTodayCalls(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            @Param("statuses") List<LeadStatus> statuses);
}
