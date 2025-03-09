package com.example.AddressBookApp.controller;

import com.example.AddressBookApp.dto.ContactDTO;
import com.example.AddressBookApp.model.Contact;
import com.example.AddressBookApp.repository.ContactRepository;
import com.example.AddressBookApp.service.ContactService;
import com.example.AddressBookApp.service.ContactServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@RestController
@RequestMapping("/contacts")
public class ContactController {

    ContactServiceImpl contactService;

    @Autowired
    public ContactController(ContactServiceImpl contactService) {
        this.contactService = contactService;
    }

    @GetMapping("/all")
    public List<ContactDTO> getAllContacts() {
        return contactService.getAllContacts();
    }

    @GetMapping("/get/{id}")
    public ContactDTO getContactById(@PathVariable int id) {
        return contactService.getContactById(id);
    }

    @PostMapping("/add")
    public ResponseEntity<?> addContact(@RequestBody ContactDTO contactDTO) {
        try {
            // Manual validation
            if (contactDTO.getName() == null || contactDTO.getName().trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Name cannot be empty"));
            }

            ContactDTO savedContact = contactService.addContact(contactDTO);
            return ResponseEntity.ok(savedContact);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateContact(@PathVariable int id, @Valid @RequestBody ContactDTO updatedContact) {
        try {
            ContactDTO updated = contactService.updateContact(id, updatedContact);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {  // Handling not found exception
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", e.getMessage()));
        } catch (Exception e) {  // Generic error handling
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Something went wrong"));
        }
    }


    @DeleteMapping("/delete/{id}")
    public void deleteContact(@PathVariable int id) {
        contactService.deleteContact(id);
    }
}