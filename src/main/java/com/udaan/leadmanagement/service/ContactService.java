package com.udaan.leadmanagement.service;

import com.udaan.leadmanagement.exception.ContactNotFoundException;
import com.udaan.leadmanagement.exception.LeadNotFoundException;
import com.udaan.leadmanagement.model.Contact;
import com.udaan.leadmanagement.model.RestaurantLead;
import com.udaan.leadmanagement.repository.ContactRepository;
import com.udaan.leadmanagement.repository.RestaurantLeadRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;
    private final RestaurantLeadRepository leadRepository;

    @Transactional
    public Contact addContact(Long leadId, Contact contact) {
        RestaurantLead lead = leadRepository.findById(leadId)
                .orElseThrow(() -> new LeadNotFoundException("Lead not found with id: " + leadId));

        validateContactDetails(contact);
        contact.setRestaurantLead(lead);

        // If this is the first contact, make it primary
        if (contactRepository.countByRestaurantLeadId(leadId) == 0) {
            contact.setPrimaryContact(true);
        }

        Contact savedContact = contactRepository.save(contact);
        lead.getContacts().add(savedContact);
        leadRepository.save(lead);

        return savedContact;
    }

    public List<Contact> getContactsByLeadId(Long leadId) {
        if (!leadRepository.existsById(leadId)) {
            throw new EntityNotFoundException("Lead not found with ID: " + leadId);
        }
        return contactRepository.findByRestaurantLeadId(leadId);
    }

    @Transactional
    public Contact updateContact(Long contactId, Contact updatedContact) {
        Contact existingContact = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with ID: " + contactId));

        validateContactDetails(updatedContact);

        existingContact.setName(updatedContact.getName());
        existingContact.setRole(updatedContact.getRole());
        existingContact.setEmail(updatedContact.getEmail());
        existingContact.setPhone(updatedContact.getPhone());

        return contactRepository.save(existingContact);
    }

    @Transactional
    public void deleteContact(Long contactId) {
        Contact contact = contactRepository.findById(contactId)
                .orElseThrow(() -> new EntityNotFoundException("Contact not found with ID: " + contactId));

        // If this is a primary contact, assign primary to another contact if available
        if (contact.isPrimaryContact()) {
            List<Contact> otherContacts = contactRepository.findByRestaurantLeadIdAndIdNot(
                    contact.getRestaurantLead().getId(),
                    contactId);

            if (!otherContacts.isEmpty()) {
                Contact newPrimaryContact = otherContacts.getFirst();
                newPrimaryContact.setPrimaryContact(true);
                contactRepository.save(newPrimaryContact);
            }
        }

        contactRepository.delete(contact);
    }

    public Contact getPrimaryContact(Long leadId) {
        return contactRepository.findByRestaurantLeadIdAndIsPrimaryContactTrue(leadId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Primary contact not found for lead ID: " + leadId));
    }

    @Transactional
    public Contact setPrimaryContact(Long leadId, Long contactId) {
        if (!leadRepository.existsById(leadId)) {
            throw new LeadNotFoundException("Lead not found with ID: " + leadId);
        }

        Contact newPrimaryContact = contactRepository.findById(contactId)
                .orElseThrow(() -> new ContactNotFoundException("Contact not found with ID: " + contactId));

        if (!newPrimaryContact.getRestaurantLead().getId().equals(leadId)) {
            throw new IllegalArgumentException("Contact does not belong to the specified lead");
        }

        // Reset all primary contacts for this lead
        contactRepository.resetPrimaryContactsForLead(leadId);

        // Set new primary contact
        newPrimaryContact.setPrimaryContact(true);
        return contactRepository.save(newPrimaryContact);
    }

    public List<Contact> getContactsByRole(Long leadId, String role) {
        if (!leadRepository.existsById(leadId)) {
            throw new EntityNotFoundException("Lead not found with ID: " + leadId);
        }
        return contactRepository.findByRestaurantLeadIdAndRole(leadId, role);
    }

    private void validateContactDetails(Contact contact) {
        if (contact.getName() == null || contact.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact name cannot be empty");
        }

        if (contact.getEmail() == null || !contact.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new IllegalArgumentException("Invalid email format");
        }

        if (contact.getPhone() == null || !contact.getPhone().matches("^\\+?[0-9]{10,15}$")) {
            throw new IllegalArgumentException("Invalid phone number format");
        }

        if (contact.getRole() == null || contact.getRole().trim().isEmpty()) {
            throw new IllegalArgumentException("Contact role cannot be empty");
        }
    }
}