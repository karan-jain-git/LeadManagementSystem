package com.udaan.leadmanagement.controller;

import com.udaan.leadmanagement.exception.ErrorResponse;
import com.udaan.leadmanagement.model.KAM;
import com.udaan.leadmanagement.model.KAMPerformance;
import com.udaan.leadmanagement.model.RestaurantLead;
import com.udaan.leadmanagement.service.KAMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kams")
@RequiredArgsConstructor
public class KAMController {
    private final KAMService kamService;

    @PostMapping()
    public ResponseEntity<?> addKAM(@RequestBody KAM kam) {
        try {
            KAM createdKam = kamService.addKAM(kam);
            if (createdKam == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ErrorResponse("Failed to create kam"));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(createdKam);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error creating kam: " + e.getMessage()));
        }
    }

    @GetMapping()
    public ResponseEntity<?> getAllKAMs() {
        try {
            List<KAM> kamList = kamService.getAllKAM();
            if (kamList == null || kamList.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No KAMs found"));
            }
            return ResponseEntity.ok(kamList);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving KAMs: " + e.getMessage()));
        }
    }

    @PostMapping("/{kamId}/leads")
    public ResponseEntity<?> addLeadUnderKAM(@PathVariable int kamId, @RequestBody List<Long> leadIds) {
        try {
            KAM kam = kamService.assignLeadsToKAM(kamId, leadIds);
            if (kam == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Not able to assign leads to kam"));
            }
            return ResponseEntity.ok(kam);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error assigning leads to this kam: " + e.getMessage()));
        }
    }

    @GetMapping("/{kamId}/leads")
    public ResponseEntity<?> getLeadsByKamId(@PathVariable("kamId") long kamId) {
        try {
            List<RestaurantLead> leads = kamService.getLeadsByKAMId(kamId);
            if (leads == null || leads.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No leads under this KAM"));
            }
            return ResponseEntity.ok(leads);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving Leads: " + e.getMessage()));
        }
    }

    @GetMapping("/top-performing")
    public ResponseEntity<?> getTopPerformingKAMs() {
        try {
            List<KAMPerformance> topPerformingKAMs = kamService.getTopPerformingKAMs();
            if (topPerformingKAMs == null || topPerformingKAMs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No top-performing KAMs found"));
            }
            return ResponseEntity.ok(topPerformingKAMs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving top-performing KAMs: " + e.getMessage()));
        }
    }

    @GetMapping("/under-performing")
    public ResponseEntity<?> getUnderPerformingKAMs() {
        try {
            List<KAMPerformance> underPerformingKAMs = kamService.getUnderPerformingKAMs();
            if (underPerformingKAMs == null || underPerformingKAMs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No under-performing KAMs found"));
            }
            return ResponseEntity.ok(underPerformingKAMs);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving under-performing KAMs: " + e.getMessage()));
        }
    }
}
