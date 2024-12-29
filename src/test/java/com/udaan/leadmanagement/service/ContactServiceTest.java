package com.udaan.leadmanagement.service;

import com.udaan.leadmanagement.exception.LeadNotFoundException;
import com.udaan.leadmanagement.model.Contact;
import com.udaan.leadmanagement.model.RestaurantLead;
import com.udaan.leadmanagement.repository.ContactRepository;
import com.udaan.leadmanagement.repository.RestaurantLeadRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ContactServiceTest {

    @Mock
    private ContactRepository contactRepository;

    @Mock
    private RestaurantLeadRepository leadRepository;

    @InjectMocks
    private ContactService contactService;

    private RestaurantLead testLead;
    private Contact testContact;

    @BeforeEach
    void setUp() {
        testLead = new RestaurantLead();
        testLead.setId(1L);

        testContact = new Contact();
        testContact.setId(1L);
        testContact.setName("John Doe");
        testContact.setEmail("john@example.com");
        testContact.setPhone("+1234567890");
        testContact.setRole("Manager");
        testContact.setRestaurantLead(testLead);
    }

    @Test
    void addContact_FirstContact_SetAsPrimary() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(contactRepository.countByRestaurantLeadId(1L)).thenReturn(0);
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);
        when(leadRepository.save(any(RestaurantLead.class))).thenReturn(testLead);

        Contact result = contactService.addContact(1L, testContact);

        assertTrue(result.isPrimaryContact());
        verify(contactRepository).save(testContact);
        verify(leadRepository).save(testLead);
    }

    @Test
    void addContact_NotFirstContact_NotPrimary() {
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));
        when(contactRepository.countByRestaurantLeadId(1L)).thenReturn(1);
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);
        when(leadRepository.save(any(RestaurantLead.class))).thenReturn(testLead);

        Contact result = contactService.addContact(1L, testContact);

        assertFalse(result.isPrimaryContact());
    }

    @Test
    void addContact_LeadNotFound_ThrowsException() {
        when(leadRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(LeadNotFoundException.class, () -> contactService.addContact(1L, testContact));
    }

    @Test
    void addContact_InvalidContactDetails_ThrowsException() {
        testContact.setEmail("invalid-email");
        when(leadRepository.findById(1L)).thenReturn(Optional.of(testLead));

        assertThrows(IllegalArgumentException.class, () -> contactService.addContact(1L, testContact));
    }

    @Test
    void getContactsByLeadId_Success() {
        when(leadRepository.existsById(1L)).thenReturn(true);
        when(contactRepository.findByRestaurantLeadId(1L)).thenReturn(Arrays.asList(testContact));

        List<Contact> results = contactService.getContactsByLeadId(1L);

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals(testContact, results.getFirst());
    }

    @Test
    void getContactsByLeadId_LeadNotFound_ThrowsException() {
        when(leadRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> contactService.getContactsByLeadId(1L));
    }

    @Test
    void updateContact_Success() {
        Contact updatedContact = new Contact();
        updatedContact.setName("Jane Doe");
        updatedContact.setEmail("jane@example.com");
        updatedContact.setPhone("+9876543210");
        updatedContact.setRole("Director");

        when(contactRepository.findById(1L)).thenReturn(Optional.of(testContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);

        Contact result = contactService.updateContact(1L, updatedContact);

        assertEquals(updatedContact.getName(), result.getName());
        assertEquals(updatedContact.getEmail(), result.getEmail());
        verify(contactRepository).save(any(Contact.class));
    }

    @Test
    void updateContact_ContactNotFound_ThrowsException() {
        when(contactRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> contactService.updateContact(1L, testContact));
    }

    @Test
    void deleteContact_PrimaryContact_AssignsNewPrimary() {
        testContact.setPrimaryContact(true);
        Contact secondContact = new Contact();
        secondContact.setId(2L);

        when(contactRepository.findById(1L)).thenReturn(Optional.of(testContact));
        when(contactRepository.findByRestaurantLeadIdAndIdNot(1L, 1L))
                .thenReturn(Arrays.asList(secondContact));

        contactService.deleteContact(1L);

        verify(contactRepository).save(secondContact);
        verify(contactRepository).delete(testContact);
        assertTrue(secondContact.isPrimaryContact());
    }

    @Test
    void deleteContact_NotPrimaryContact_JustDeletes() {
        when(contactRepository.findById(1L)).thenReturn(Optional.of(testContact));

        contactService.deleteContact(1L);

        verify(contactRepository).delete(testContact);
        verify(contactRepository, never()).save(any(Contact.class));
    }

    @Test
    void getPrimaryContact_Success() {
        when(contactRepository.findByRestaurantLeadIdAndIsPrimaryContactTrue(1L))
                .thenReturn(Optional.of(testContact));

        Contact result = contactService.getPrimaryContact(1L);

        assertNotNull(result);
        assertEquals(testContact, result);
    }

    @Test
    void getPrimaryContact_NotFound_ThrowsException() {
        when(contactRepository.findByRestaurantLeadIdAndIsPrimaryContactTrue(1L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> contactService.getPrimaryContact(1L));
    }

    @Test
    void setPrimaryContact_Success() {
        when(leadRepository.existsById(1L)).thenReturn(true);
        when(contactRepository.findById(1L)).thenReturn(Optional.of(testContact));
        when(contactRepository.save(any(Contact.class))).thenReturn(testContact);

        Contact result = contactService.setPrimaryContact(1L, 1L);

        assertTrue(result.isPrimaryContact());
        verify(contactRepository).resetPrimaryContactsForLead(1L);
    }

    @Test
    void setPrimaryContact_ContactNotBelongingToLead_ThrowsException() {
        testContact.setRestaurantLead(new RestaurantLead());
        testContact.getRestaurantLead().setId(2L);

        when(leadRepository.existsById(1L)).thenReturn(true);
        when(contactRepository.findById(1L)).thenReturn(Optional.of(testContact));

        assertThrows(IllegalArgumentException.class,
                () -> contactService.setPrimaryContact(1L, 1L));
    }

    @Test
    void getContactsByRole_Success() {
        when(leadRepository.existsById(1L)).thenReturn(true);
        when(contactRepository.findByRestaurantLeadIdAndRole(1L, "Manager"))
                .thenReturn(Arrays.asList(testContact));

        List<Contact> results = contactService.getContactsByRole(1L, "Manager");

        assertFalse(results.isEmpty());
        assertEquals(1, results.size());
        assertEquals("Manager", results.get(0).getRole());
    }

    @Test
    void getContactsByRole_LeadNotFound_ThrowsException() {
        when(leadRepository.existsById(1L)).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> contactService.getContactsByRole(1L, "Manager"));
    }

    @Test
    void validateContactDetails_InvalidName_ThrowsException() {
        testContact.setName("");
        assertThrows(IllegalArgumentException.class,
                () -> contactService.addContact(1L, testContact));
    }

    @Test
    void validateContactDetails_InvalidEmail_ThrowsException() {
        testContact.setEmail("invalid-email");
        assertThrows(IllegalArgumentException.class,
                () -> contactService.addContact(1L, testContact));
    }

    @Test
    void validateContactDetails_InvalidPhone_ThrowsException() {
        testContact.setPhone("123");
        assertThrows(IllegalArgumentException.class,
                () -> contactService.addContact(1L, testContact));
    }

    @Test
    void validateContactDetails_InvalidRole_ThrowsException() {
        testContact.setRole("");
        assertThrows(IllegalArgumentException.class,
                () -> contactService.addContact(1L, testContact));
    }
}