package com.udaan.leadmanagement.DTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class KAMPerformanceDTO {
    private Long kamId;
    private String kamName;
    private String kamEmail;
    private String kamPhone;
    private Integer totalLeads;
    private Integer convertedLeads;
    private Double conversionRate;
    private LocalDateTime lastUpdated;
}