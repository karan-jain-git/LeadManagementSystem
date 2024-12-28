package com.udaan.leadmanagement.controller;

import com.udaan.leadmanagement.enums.LeadStatus;
import com.udaan.leadmanagement.exception.ErrorResponse;
import com.udaan.leadmanagement.model.Interaction;
import com.udaan.leadmanagement.model.RestaurantLead;
import com.udaan.leadmanagement.service.RestaurantLeadManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leads")
@RequiredArgsConstructor
public class RestaurantLeadManagementController {
    private final RestaurantLeadManagementService leadService;

    @PostMapping
    public ResponseEntity<?> createLead(@RequestBody RestaurantLead lead) {
        try {
            RestaurantLead createdLead = leadService.createLead(lead);
            if (createdLead == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Failed to create lead"));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdLead);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error creating lead: " + e.getMessage()));
        }
    }

    @GetMapping()
    public ResponseEntity<?> getLeads() {
        try {
            List<RestaurantLead> leads = leadService.getAllLeads();
            if (leads == null || leads.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No leads found"));
            }
            return ResponseEntity.ok(leads);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving leads: " + e.getMessage()));
        }
    }

    @GetMapping("/today-calls")
    public ResponseEntity<?> getTodayCalls() {
        try {
            List<RestaurantLead> leads = leadService.getTodayCalls();
            if (leads == null || leads.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No calls due today"));
            }
            return ResponseEntity.ok(leads);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving today's calls: " + e.getMessage()));
        }
    }

    @PatchMapping("/{leadId}/status")
    public ResponseEntity<?> updateLeadStatus(
            @PathVariable Long leadId,
            @RequestParam LeadStatus status) {
        try {
            RestaurantLead updatedLead = leadService.updateLeadStatus(leadId, status);
            if (updatedLead == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Lead not found with id: " + leadId));
            }
            return ResponseEntity.ok(updatedLead);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error updating lead status: " + e.getMessage()));
        }
    }

    @PostMapping("/{leadId}/interactions")
    public ResponseEntity<?> recordInteraction(
            @PathVariable Long leadId,
            @RequestBody Interaction interaction) {
        try {
            Interaction recordedInteraction = leadService.recordInteraction(leadId, interaction);
            if (recordedInteraction == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Failed to record interaction"));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(recordedInteraction);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error recording interaction: " + e.getMessage()));
        }
    }

    @GetMapping("/{leadId}/interactions")
    public ResponseEntity<?> getInteractions(@PathVariable Long leadId) {
        try {
            List<Interaction> fetchedInteractions = leadService.getInteractionsByLeadId(leadId);
            if (fetchedInteractions == null || fetchedInteractions.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No interactions found for this lead"));
            }
            return ResponseEntity.ok(fetchedInteractions);
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving interactions: " + e.getMessage()));
        }
    }
}
