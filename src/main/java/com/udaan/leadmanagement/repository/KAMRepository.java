package com.udaan.leadmanagement.repository;

import com.udaan.leadmanagement.model.KAM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface KAMRepository extends JpaRepository<KAM, Long> {
    @Query("SELECT k.id as kamId, " +
            "k.name as kamName, " +
            "k.email as kamEmail, " +
            "k.phone as kamPhone, " +
            "COUNT(r) as totalLeads, " +
            "SUM(CASE WHEN r.status = com.udaan.leadmanagement.enums.LeadStatus.CONVERTED THEN 1 ELSE 0 END) as convertedLeads, " +
            "CASE WHEN COUNT(r) > 0 THEN " +
            "    CAST(SUM(CASE WHEN r.status = com.udaan.leadmanagement.enums.LeadStatus.CONVERTED THEN 1 ELSE 0 END) AS DOUBLE) / COUNT(r) * 100 " +
            "ELSE 0 END as conversionRate " +
            "FROM KAM k " +
            "LEFT JOIN k.leads r " +
            "GROUP BY k.id, k.name, k.email, k.phone")
    List<Object[]> findKAMsPerformanceMetrics();
}

