package com.udaan.leadmanagement.service;

import com.udaan.leadmanagement.enums.InteractionType;
import com.udaan.leadmanagement.enums.LeadStatus;
import com.udaan.leadmanagement.exception.InteractionNotFoundException;
import com.udaan.leadmanagement.exception.LeadNotFoundException;
import com.udaan.leadmanagement.model.Interaction;
import com.udaan.leadmanagement.model.RestaurantLead;
import com.udaan.leadmanagement.repository.InteractionRepository;
import com.udaan.leadmanagement.repository.RestaurantLeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RestaurantLeadManagementService {
    private final RestaurantLeadRepository restaurantLeadRepository;
    private final InteractionRepository interactionRepository;

    public RestaurantLead createLead(RestaurantLead lead) {
        lead.setStatus(LeadStatus.NEW);
        lead.setNextCallDate(calculateNextCallDate(lead.getCallFrequency(), LocalDateTime.now()));
        return restaurantLeadRepository.save(lead);
    }

    public List<RestaurantLead> getAllLeads() {
        List<RestaurantLead> leads = restaurantLeadRepository.findAll();
        if(leads.isEmpty()) {
            throw (new LeadNotFoundException("No leads found"));
        }
        return leads;
    }

    public List<RestaurantLead> getTodayCalls() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = startOfDay.plusDays(1).minusSeconds(1);

        List<LeadStatus> activeStatuses = Arrays.asList(
                LeadStatus.NEW,
                LeadStatus.IN_PROGRESS,
                LeadStatus.FOLLOW_UP
        );

        return restaurantLeadRepository.findTodayCalls(
                startOfDay,
                endOfDay,
                activeStatuses);
    }

    public RestaurantLead updateLeadStatus(Long leadId, LeadStatus newStatus) {
        RestaurantLead lead = restaurantLeadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead not found with id: " + leadId));
        lead.setStatus(newStatus);
        return restaurantLeadRepository.save(lead);
    }

    public Interaction recordInteraction(Long leadId, Interaction interaction) {
        RestaurantLead lead = restaurantLeadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead not found with id: " + leadId));
        interaction.setRestaurantLead(lead);
        interaction.setInteractionDate(LocalDateTime.now());
        lead.setStatus(LeadStatus.IN_PROGRESS);

        if (interaction.getType() == InteractionType.CALL) {
            lead.setLastCallDate(LocalDateTime.now());
            lead.setNextCallDate(calculateNextCallDate(lead.getCallFrequency(), LocalDateTime.now()));
            restaurantLeadRepository.save(lead);
        }

        return interactionRepository.save(interaction);
    }

    public List<Interaction> getInteractionsByLeadId(Long leadId) {
         restaurantLeadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead not found with id: " + leadId));

        List<Interaction> fetchedInteractions = interactionRepository.findAllByRestaurantLeadId(leadId);
        if (fetchedInteractions == null || fetchedInteractions.isEmpty()) {
            throw new InteractionNotFoundException("No Interactions found for LeadId: " + leadId);
        }
        return fetchedInteractions;
    }

    private LocalDateTime calculateNextCallDate(Integer frequencyInDays, LocalDateTime lastCallDate) {
        return lastCallDate.plusDays(frequencyInDays);
    }
}
