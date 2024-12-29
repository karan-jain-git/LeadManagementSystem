package com.udaan.leadmanagement.service;

import com.udaan.leadmanagement.enums.InteractionType;
import com.udaan.leadmanagement.enums.LeadStatus;
import com.udaan.leadmanagement.exception.InteractionNotFoundException;
import com.udaan.leadmanagement.exception.LeadNotFoundException;
import com.udaan.leadmanagement.model.Interaction;
import com.udaan.leadmanagement.model.RestaurantLead;
import com.udaan.leadmanagement.repository.InteractionRepository;
import com.udaan.leadmanagement.repository.RestaurantLeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RestaurantLeadManagementServiceTest {

    @Mock
    private RestaurantLeadRepository restaurantLeadRepository;

    @Mock
    private InteractionRepository interactionRepository;

    @InjectMocks
    private RestaurantLeadManagementService service;

    private RestaurantLead testLead;
    private Interaction testInteraction;

    @BeforeEach
    void setUp() {
        testLead = new RestaurantLead();
        testLead.setId(1L);
        testLead.setCallFrequency(7);
        testLead.setStatus(LeadStatus.NEW);

        testInteraction = new Interaction();
        testInteraction.setId(1L);
        testInteraction.setType(InteractionType.CALL);
        testInteraction.setRestaurantLead(testLead);
    }

    @Test
    void createLead_ShouldSetStatusAndNextCallDate() {
        when(restaurantLeadRepository.save(any(RestaurantLead.class))).thenReturn(testLead);

        RestaurantLead result = service.createLead(testLead);

        assertEquals(LeadStatus.NEW, result.getStatus());
        assertNotNull(result.getNextCallDate());
        verify(restaurantLeadRepository).save(testLead);
    }

    @Test
    void getAllLeads_ShouldReturnLeadsList() {
        List<RestaurantLead> leads = Arrays.asList(testLead);
        when(restaurantLeadRepository.findAll()).thenReturn(leads);

        List<RestaurantLead> result = service.getAllLeads();

        assertEquals(1, result.size());
        assertEquals(testLead, result.get(0));
    }

    @Test
    void getAllLeads_WhenNoLeads_ShouldThrowException() {
        when(restaurantLeadRepository.findAll()).thenReturn(Collections.emptyList());

        assertThrows(LeadNotFoundException.class, () -> service.getAllLeads());
    }

    @Test
    void getTodayCalls_ShouldReturnLeadsForToday() {
        List<RestaurantLead> expectedLeads = Arrays.asList(testLead);
        when(restaurantLeadRepository.findTodayCalls(any(), any(), any())).thenReturn(expectedLeads);

        List<RestaurantLead> result = service.getTodayCalls();

        assertEquals(expectedLeads, result);
        verify(restaurantLeadRepository).findTodayCalls(
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                anyList()
        );
    }

    @Test
    void updateLeadStatus_ShouldUpdateStatus() {
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(restaurantLeadRepository.save(any(RestaurantLead.class))).thenReturn(testLead);

        RestaurantLead result = service.updateLeadStatus(1L, LeadStatus.IN_PROGRESS);

        assertEquals(LeadStatus.IN_PROGRESS, result.getStatus());
        verify(restaurantLeadRepository).save(testLead);
    }

    @Test
    void updateLeadStatus_WhenLeadNotFound_ShouldThrowException() {
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class, () -> service.updateLeadStatus(1L, LeadStatus.IN_PROGRESS));
    }

    @Test
    void recordInteraction_ForCallType_ShouldUpdateLeadAndSaveInteraction() {
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(restaurantLeadRepository.save(any(RestaurantLead.class))).thenReturn(testLead);
        when(interactionRepository.save(any(Interaction.class))).thenReturn(testInteraction);

        Interaction result = service.recordInteraction(1L, testInteraction);

        assertNotNull(result);
        assertEquals(LeadStatus.IN_PROGRESS, testLead.getStatus());
        assertNotNull(testLead.getLastCallDate());
        assertNotNull(testLead.getNextCallDate());
        verify(restaurantLeadRepository).save(testLead);
        verify(interactionRepository).save(testInteraction);
    }

    @Test
    void recordInteraction_WhenLeadNotFound_ShouldThrowException() {
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class, () -> service.recordInteraction(1L, testInteraction));
    }

    @Test
    void recordInteraction_ForNonCallType_ShouldNotUpdateCallDates() {
        testInteraction.setType(InteractionType.EMAIL);
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(restaurantLeadRepository.save(any(RestaurantLead.class))).thenReturn(testLead);
        when(interactionRepository.save(any(Interaction.class))).thenReturn(testInteraction);

        service.recordInteraction(1L, testInteraction);

        assertNull(testLead.getLastCallDate());
        verify(interactionRepository).save(testInteraction);
    }

    @Test
    void getInteractionsByLeadId_ShouldReturnInteractions() {
        List<Interaction> interactions = Arrays.asList(testInteraction);
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(interactionRepository.findAllByRestaurantLeadId(1L)).thenReturn(interactions);

        List<Interaction> result = service.getInteractionsByLeadId(1L);

        assertEquals(1, result.size());
        assertEquals(testInteraction, result.get(0));
    }

    @Test
    void getInteractionsByLeadId_WhenLeadNotFound_ShouldThrowException() {
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class, () -> service.getInteractionsByLeadId(1L));
    }

    @Test
    void getInteractionsByLeadId_WhenNoInteractions_ShouldThrowException() {
        when(restaurantLeadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(interactionRepository.findAllByRestaurantLeadId(1L)).thenReturn(Collections.emptyList());

        assertThrows(InteractionNotFoundException.class, () -> service.getInteractionsByLeadId(1L));
    }
}