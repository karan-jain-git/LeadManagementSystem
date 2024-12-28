package com.udaan.leadmanagement.controller;

import com.udaan.leadmanagement.exception.ErrorResponse;
import com.udaan.leadmanagement.model.Contact;
import com.udaan.leadmanagement.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping("/lead/{leadId}")
    public ResponseEntity<?> addContact(
            @PathVariable Long leadId,
            @RequestBody Contact contact) {
        try {
            Contact addedContact = contactService.addContact(leadId, contact);
            if (addedContact == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Failed to add contact"));
            }
            return ResponseEntity.status(HttpStatus.CREATED).body(addedContact);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error adding contact: " + e.getMessage()));
        }
    }

    @GetMapping("/lead/{leadId}")
    public ResponseEntity<?> getContactsByLead(@PathVariable Long leadId) {
        try {
            List<Contact> contacts = contactService.getContactsByLeadId(leadId);
            if (contacts == null || contacts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No contacts found for lead id: " + leadId));
            }
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving contacts: " + e.getMessage()));
        }
    }

    @GetMapping("/lead/{leadId}/role/{role}")
    public ResponseEntity<?> getContactsByRole(
            @PathVariable Long leadId,
            @PathVariable String role) {
        try {
            List<Contact> contacts = contactService.getContactsByRole(leadId, role);
            if (contacts == null || contacts.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(new ErrorResponse("No contacts found for role: " + role));
            }
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving contacts by role: " + e.getMessage()));
        }
    }

    @PutMapping("/{contactId}")
    public ResponseEntity<?> updateContact(
            @PathVariable Long contactId,
            @RequestBody Contact contact) {
        try {
            Contact updatedContact = contactService.updateContact(contactId, contact);
            if (updatedContact == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Contact not found with id: " + contactId));
            }
            return ResponseEntity.ok(updatedContact);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error updating contact: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{contactId}")
    public ResponseEntity<?> deleteContact(@PathVariable Long contactId) {
        try {
            contactService.deleteContact(contactId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error deleting contact: " + e.getMessage()));
        }
    }

    @GetMapping("/lead/{leadId}/primary")
    public ResponseEntity<?> getPrimaryContact(@PathVariable Long leadId) {
        try {
            Contact primaryContact = contactService.getPrimaryContact(leadId);
            if (primaryContact == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ErrorResponse("Primary contact not found for lead id: " + leadId));
            }
            return ResponseEntity.ok(primaryContact);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error retrieving primary contact: " + e.getMessage()));
        }
    }

    @PutMapping("/lead/{leadId}/primary/{contactId}")
    public ResponseEntity<?> setPrimaryContact(
            @PathVariable Long leadId,
            @PathVariable Long contactId) {
        try {
            Contact primaryContact = contactService.setPrimaryContact(leadId, contactId);
            if (primaryContact == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ErrorResponse("Failed to set primary contact"));
            }
            return ResponseEntity.ok(primaryContact);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Error setting primary contact: " + e.getMessage()));
        }
    }
}
