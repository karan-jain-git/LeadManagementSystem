package com.udaan.leadmanagement.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String role;
    private String email;
    private String phone;
    private boolean isPrimaryContact;

    @ManyToOne
    @JoinColumn(name = "restaurant_lead_id")
    @JsonBackReference
    private RestaurantLead restaurantLead;
}

