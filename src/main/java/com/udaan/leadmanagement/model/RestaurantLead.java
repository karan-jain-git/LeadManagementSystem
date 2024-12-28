package com.udaan.leadmanagement.model;

import com.udaan.leadmanagement.enums.LeadStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantLead {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String address;

    @Enumerated(EnumType.STRING)
    private LeadStatus status;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Contact> contacts;

    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL)
    private List<Interaction> interactions;

    @ManyToOne
    @JoinColumn(name = "kam_id")
    private KAM assignedKam;

    private LocalDateTime nextCallDate;
    private Integer callFrequency; // in days
    private LocalDateTime lastCallDate;
}