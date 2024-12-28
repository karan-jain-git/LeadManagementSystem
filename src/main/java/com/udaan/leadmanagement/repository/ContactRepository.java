package com.udaan.leadmanagement.repository;

import com.udaan.leadmanagement.model.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByRestaurantLeadId(Long leadId);

    Optional<Contact> findByRestaurantLeadIdAndIsPrimaryContactTrue(Long leadId);

    @Modifying
    @Query("UPDATE Contact c SET c.isPrimaryContact = false WHERE c.restaurantLead.id = :leadId")
    void resetPrimaryContactsForLead(@Param("leadId") Long leadId);

    List<Contact> findByRestaurantLeadIdAndRole(Long leadId, String role);

    List<Contact> findByRestaurantLeadIdAndIdNot(Long leadId, Long contactId);

    int countByRestaurantLeadId(Long leadId);
}