package com.udaan.leadmanagement.service;

import com.udaan.leadmanagement.DTO.KAMPerformanceDTO;
import com.udaan.leadmanagement.exception.InternalServerException;
import com.udaan.leadmanagement.exception.KAMNotFoundException;
import com.udaan.leadmanagement.exception.KAMPerformanceException;
import com.udaan.leadmanagement.exception.LeadNotFoundException;
import com.udaan.leadmanagement.model.KAM;
import com.udaan.leadmanagement.model.RestaurantLead;
import com.udaan.leadmanagement.repository.KAMRepository;
import com.udaan.leadmanagement.repository.RestaurantLeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KAMServiceTest {

    @Mock
    private KAMRepository kamRepository;

    @Mock
    private RestaurantLeadRepository restaurantLeadRepository;

    @InjectMocks
    private KAMService kamService;

    private KAM testKAM;
    private RestaurantLead testLead;
    private Object[] performanceMetrics;
    private KAMPerformanceDTO testPerformanceDTO;

    @BeforeEach
    void setUp() {
        testKAM = new KAM();
        testKAM.setId(1L);
        testKAM.setName("Test KAM");
        testKAM.setEmail("test@example.com");
        testKAM.setPhone("1234567890");
        testKAM.setLeads(new ArrayList<>());

        testLead = new RestaurantLead();
        testLead.setId(1L);

        performanceMetrics = new Object[]{
                1L, "Test KAM", "test@example.com", "1234567890", 10, 5, 50.0
        };

        testPerformanceDTO = new KAMPerformanceDTO();
        testPerformanceDTO.setKamId(1L);
        testPerformanceDTO.setKamName("Test KAM");
        testPerformanceDTO.setConversionRate(50.0);
    }

    @Test
    void addKAM_Success() {
        when(kamRepository.save(any(KAM.class))).thenReturn(testKAM);

        KAM result = kamService.addKAM(testKAM);

        assertNotNull(result);
        assertEquals(testKAM.getId(), result.getId());
        verify(kamRepository).save(testKAM);
    }

    @Test
    void addKAM_Failure_ThrowsException() {
        KAM kamWithNullId = new KAM();
        when(kamRepository.save(any(KAM.class))).thenReturn(kamWithNullId);

        assertThrows(InternalServerException.class, () -> kamService.addKAM(kamWithNullId));
    }

    @Test
    void getAllKAM_Success() {
        when(kamRepository.findAll()).thenReturn(Arrays.asList(testKAM));

        List<KAM> result = kamService.getAllKAM();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testKAM, result.get(0));
    }

    @Test
    void getAllKAM_EmptyList_ThrowsException() {
        when(kamRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(KAMNotFoundException.class, () -> kamService.getAllKAM());
    }

    @Test
    void assignLeadsToKAM_Success() {
        when(kamRepository.findById(1L)).thenReturn(Optional.of(testKAM));
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(kamRepository.save(any(KAM.class))).thenReturn(testKAM);
        when(restaurantLeadRepository.save(any(RestaurantLead.class))).thenReturn(testLead);

        KAM result = kamService.assignLeadsToKAM(1L, List.of(1L));

        assertNotNull(result);
        verify(kamRepository).save(any(KAM.class));
        verify(restaurantLeadRepository).save(any(RestaurantLead.class));
    }

    @Test
    void assignLeadsToKAM_KAMNotFound_ThrowsException() {
        when(kamRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(KAMNotFoundException.class,
                () -> kamService.assignLeadsToKAM(1L, List.of(1L)));
    }

    @Test
    void assignLeadsToKAM_LeadNotFound_ThrowsException() {
        when(kamRepository.findById(1L)).thenReturn(Optional.of(testKAM));
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class,
                () -> kamService.assignLeadsToKAM(1L, List.of(1L)));
    }

    @Test
    void getLeadsByKAMId_Success() {
        testKAM.getLeads().add(testLead);
        when(kamRepository.findById(1L)).thenReturn(Optional.of(testKAM));

        List<RestaurantLead> result = kamService.getLeadsByKAMId(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testLead, result.getFirst());
    }

    @Test
    void getLeadsByKAMId_KAMNotFound_ThrowsException() {
        when(kamRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(KAMNotFoundException.class, () -> kamService.getLeadsByKAMId(1L));
    }

    @Test
    void getLeadsByKAMId_NoLeads_ThrowsException() {
        when(kamRepository.findById(1L)).thenReturn(Optional.of(testKAM));

        assertThrows(LeadNotFoundException.class, () -> kamService.getLeadsByKAMId(1L));
    }

    @Test
    void getKAMPerformance_Success() {
        when(kamRepository.findKAMsPerformanceMetrics())
                .thenReturn(Collections.singletonList(performanceMetrics));

        KAMPerformanceDTO result = kamService.getKAMPerformance(1L);

        assertNotNull(result);
        assertEquals(1L, result.getKamId());
        assertEquals("Test KAM", result.getKamName());
    }

    @Test
    void getKAMPerformance_KAMNotFound_ThrowsException() {
        when(kamRepository.findKAMsPerformanceMetrics())
                .thenReturn(Collections.emptyList());

        assertThrows(KAMNotFoundException.class, () -> kamService.getKAMPerformance(1L));
    }

    @Test
    void getTopPerformingKAMs_Success() {
        when(kamRepository.findKAMsPerformanceMetrics())
                .thenReturn(Arrays.asList(
                        performanceMetrics,
                        new Object[]{2L, "Test KAM 2", "test2@example.com", "0987654321", 8, 3, 30.0}
                ));

        List<KAMPerformanceDTO> result = kamService.getTopPerformingKAMs(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.get(0).getConversionRate() > 40.0); // Above average
    }

    @Test
    void getTopPerformingKAMs_NoKAMs_ThrowsException() {
        when(kamRepository.findKAMsPerformanceMetrics()).thenReturn(Collections.emptyList());

        assertThrows(KAMPerformanceException.class, () -> kamService.getTopPerformingKAMs(1));
    }

    @Test
    void getUnderPerformingKAMs_Success() {
        when(kamRepository.findKAMsPerformanceMetrics())
                .thenReturn(Arrays.asList(
                        performanceMetrics,
                        new Object[]{2L, "Test KAM 2", "test2@example.com", "0987654321", 8, 3, 30.0}
                ));

        List<KAMPerformanceDTO> result = kamService.getUnderPerformingKAMs(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertTrue(result.getFirst().getConversionRate() < 40.0); // Below average
    }

    @Test
    void getUnderPerformingKAMs_NoKAMs_ThrowsException() {
        when(kamRepository.findKAMsPerformanceMetrics()).thenReturn(Collections.emptyList());

        assertThrows(KAMPerformanceException.class, () -> kamService.getUnderPerformingKAMs(1));
    }

    @Test
    void getUnderPerformingKAMs_NoUnderperformers_ThrowsException() {
        when(kamRepository.findKAMsPerformanceMetrics())
                .thenReturn(Collections.singletonList(performanceMetrics));

        assertThrows(KAMPerformanceException.class, () -> kamService.getUnderPerformingKAMs(1));
    }
}