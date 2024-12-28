package com.udaan.leadmanagement.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// Entity: KAM (Key Account Manager)
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KAM {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phone;

    @OneToMany(mappedBy = "kam", cascade = CascadeType.ALL)
    private List<RestaurantLead> leads;
}
