package com.udaan.leadmanagement.repository;

import com.udaan.leadmanagement.model.KAM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KAMRepository extends JpaRepository<KAM, Long> {
    @Query("SELECT k, COUNT(r) as convertedLeads FROM KAM k " +
            "LEFT JOIN k.leads r " +
            "WHERE r.status = 'CONVERTED' " +
            "GROUP BY k.id " +
            "ORDER BY convertedLeads DESC")
    List<Object[]> findTopPerformingKAMs();

    @Query("SELECT k, COUNT(r) as convertedLeads FROM KAM k " +
            "LEFT JOIN k.leads r " +
            "WHERE r.status = 'CONVERTED' " +
            "GROUP BY k.id " +
            "ORDER BY convertedLeads ASC")
    List<Object[]> findUnderPerformingKAMs();
}

