package com.udaan.leadmanagement.service;

import com.udaan.leadmanagement.exception.InternalServerException;
import com.udaan.leadmanagement.exception.KAMNotFoundException;
import com.udaan.leadmanagement.exception.KAMPerformanceException;
import com.udaan.leadmanagement.exception.LeadNotFoundException;
import com.udaan.leadmanagement.model.KAM;
import com.udaan.leadmanagement.model.KAMPerformance;
import com.udaan.leadmanagement.model.RestaurantLead;
import com.udaan.leadmanagement.repository.KAMRepository;
import com.udaan.leadmanagement.repository.RestaurantLeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KAMService {
    private final KAMRepository kamRepository;
    private final RestaurantLeadRepository restaurantLeadRepository;

    public KAM addKAM(KAM kam) {
        KAM addedKAM = kamRepository.save(kam);
        if (addedKAM.getId() == null) {
            throw (new InternalServerException("Internal Server Error, not able to save kam"));
        }
        return addedKAM;
    }

    public List<KAM> getAllKAM() {
        List<KAM> kamList = kamRepository.findAll();
        if (kamList.isEmpty()) {
            throw (new KAMNotFoundException("KAM not found"));
        }
        return kamList;
    }

    public KAM assignLeadsToKAM(long kamId, List<Long> restaurantLeadIds) {
        KAM kam = kamRepository.findById(kamId)
                .orElseThrow(() -> new KAMNotFoundException("KAM not found with id: " + kamId));

        List<RestaurantLead> leads = restaurantLeadIds.stream()
                .map(id -> restaurantLeadRepository.findById(id)
                        .orElseThrow(() -> new LeadNotFoundException("Lead not found with id: " + id)))
                .toList();

        leads.forEach(lead -> {
            lead.setAssignedKam(kam);
            restaurantLeadRepository.save(lead);
        });

        kam.getLeads().addAll(leads);
        return kamRepository.save(kam);
    }


    public List<RestaurantLead> getLeadsByKAMId(long kamId) {
        KAM kam = kamRepository.findById(kamId)
                .orElseThrow(() -> new KAMNotFoundException("KAM not found with id: " + kamId));
        List<RestaurantLead> leads = kam.getLeads();
        if (leads == null ||leads.isEmpty()) {
            throw (new LeadNotFoundException("Lead not found under KAM id " + kamId));
        }
        return leads;
    }

    public List<KAMPerformance> getTopPerformingKAMs() {
        List<Object[]> results = kamRepository.findTopPerformingKAMs();
        if (results == null || results.isEmpty()) {
            throw new KAMPerformanceException("No top-performing KAMs found.");
        }
        return convertToKAMPerformance(results);
    }

    public List<KAMPerformance> getUnderPerformingKAMs() {
        List<Object[]> results = kamRepository.findUnderPerformingKAMs();
        if (results == null || results.isEmpty()) {
            throw new KAMPerformanceException("No under-performing KAMs found.");
        }
        return convertToKAMPerformance(results);
    }

    private List<KAMPerformance> convertToKAMPerformance(List<Object[]> results) {
        return results.stream()
                .map(result -> {
                    KAMPerformance performance = new KAMPerformance();
                    performance.setKam((KAM) result[0]);
                    int convertedLeads = ((Long) result[1]).intValue();
                    performance.setConvertedLeads(convertedLeads);
                    performance.setTotalLeads(convertedLeads); // Since query only returns converted leads
                    performance.setLastUpdated(LocalDateTime.now());
                    performance.calculateConversionRate();
                    return performance;
                })
                .collect(Collectors.toList());
    }


}
