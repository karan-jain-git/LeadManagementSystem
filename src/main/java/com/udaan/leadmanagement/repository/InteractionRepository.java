package com.udaan.leadmanagement.repository;

import com.udaan.leadmanagement.model.Interaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionRepository extends JpaRepository<Interaction, Long> {
    List<Interaction> findAllByRestaurantLeadId(Long id);
}