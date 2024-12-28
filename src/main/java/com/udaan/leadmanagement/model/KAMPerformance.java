package com.udaan.leadmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KAMPerformance {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    private KAM kam;

    private Integer totalLeads;
    private Integer convertedLeads;
    private Double conversionRate;
    private LocalDateTime lastUpdated;

    // Method to calculate conversion rate
    public void calculateConversionRate() {
        this.conversionRate = totalLeads > 0 ?
                (double) convertedLeads / totalLeads * 100 : 0.0;
    }
}
