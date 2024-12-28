package com.udaan.leadmanagement.model;

import com.udaan.leadmanagement.enums.InteractionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Entity: Interaction
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private InteractionType type;

    private LocalDateTime interactionDate;
    private String notes;

    @ManyToOne
    @JoinColumn(name = "restaurant_lead_id")
    private RestaurantLead restaurantLead;
}
