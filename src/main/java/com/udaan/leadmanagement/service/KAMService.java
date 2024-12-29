package com.udaan.leadmanagement.service;

import com.udaan.leadmanagement.exception.InternalServerException;
import com.udaan.leadmanagement.exception.KAMNotFoundException;
import com.udaan.leadmanagement.exception.KAMPerformanceException;
import com.udaan.leadmanagement.exception.LeadNotFoundException;
import com.udaan.leadmanagement.model.KAM;
import com.udaan.leadmanagement.DTO.KAMPerformanceDTO;
import com.udaan.leadmanagement.model.RestaurantLead;
import com.udaan.leadmanagement.repository.KAMRepository;
import com.udaan.leadmanagement.repository.RestaurantLeadRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
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

    public KAMPerformanceDTO getKAMPerformance(Long kamId) {
        List<KAMPerformanceDTO> allPerformance = getAllKAMPerformance();

        return allPerformance.stream()
                .filter(kam -> kam.getKamId().equals(kamId))
                .findFirst()
                .orElseThrow(() -> new KAMNotFoundException("KAM not found with id: " + kamId));
    }

    public List<KAMPerformanceDTO> getTopPerformingKAMs(int count) {
        List<KAMPerformanceDTO> allKAMs = getAllKAMPerformance();
        if (allKAMs.isEmpty()) {
            throw new KAMPerformanceException("No KAMs found");
        }

        double avgConversionRate = calculateAverageConversionRate(allKAMs);

        List<KAMPerformanceDTO> topPerformers = allKAMs.stream()
                .filter(kam -> kam.getConversionRate() > avgConversionRate)
                .sorted(Comparator.comparing(KAMPerformanceDTO::getConversionRate).reversed())
                .collect(Collectors.toList());

        if (topPerformers.isEmpty()) {
            throw new KAMPerformanceException("No top-performing KAMs found above average conversion rate");
        }

        return topPerformers.subList(0, Math.min(count, topPerformers.size()));
    }

    public List<KAMPerformanceDTO> getUnderPerformingKAMs(int count) {
        List<KAMPerformanceDTO> allKAMs = getAllKAMPerformance();
        if (allKAMs.isEmpty()) {
            throw new KAMPerformanceException("No KAMs found");
        }

        double avgConversionRate = calculateAverageConversionRate(allKAMs);

        List<KAMPerformanceDTO> underPerformers = allKAMs.stream()
                .filter(kam -> kam.getConversionRate() < avgConversionRate)
                .sorted(Comparator.comparing(KAMPerformanceDTO::getConversionRate))
                .collect(Collectors.toList());

        if (underPerformers.isEmpty()) {
            throw new KAMPerformanceException("No under-performing KAMs found below average conversion rate");
        }

        return underPerformers.subList(0, Math.min(count, underPerformers.size()));
    }

    private double calculateAverageConversionRate(List<KAMPerformanceDTO> kams) {
        return kams.stream()
                .mapToDouble(KAMPerformanceDTO::getConversionRate)
                .average()
                .orElse(0.0);
    }

    private List<KAMPerformanceDTO> getAllKAMPerformance() {
        return kamRepository.findKAMsPerformanceMetrics()
                .stream()
                .map(this::mapToKAMPerformanceDTO)
                .collect(Collectors.toList());
    }


    private KAMPerformanceDTO mapToKAMPerformanceDTO(Object[] result) {
        KAMPerformanceDTO dto = new KAMPerformanceDTO();
        dto.setKamId((Long) result[0]);
        dto.setKamName((String) result[1]);
        dto.setKamEmail((String) result[2]);
        dto.setKamPhone((String) result[3]);
        dto.setTotalLeads(((Number) result[4]).intValue());
        dto.setConvertedLeads(((Number) result[5]).intValue());
        dto.setConversionRate(((Number) result[6]).doubleValue());
        dto.setLastUpdated(LocalDateTime.now());
        return dto;
    }
}