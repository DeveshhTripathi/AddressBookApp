package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.ContactDTO;
import com.example.AddressBookApp.model.Contact;
import com.example.AddressBookApp.repository.ContactRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j  // Enables logging
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;

    @Autowired
    public ContactServiceImpl(ContactRepository contactRepository) {
        this.contactRepository = contactRepository;
    }

    @Override
    public List<ContactDTO> getAllContacts() {
        log.info("Fetching all contacts");
        return contactRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ContactDTO getContactById(int id) {
        log.info("Fetching contact with ID: {}", id);
        return contactRepository.findById(id)
                .map(this::convertToDTO)
                .orElseThrow(() -> {
                    log.error("Contact with ID {} not found", id);
                    return new RuntimeException("Contact not found!");
                });
    }

    @Override
    public ContactDTO addContact(ContactDTO contactDTO) {
        try {
            log.info("Adding new contact: {}", contactDTO.getName());
            Contact contact = convertToEntity(contactDTO);
            Contact savedContact = contactRepository.save(contact);
            return convertToDTO(savedContact);
        } catch (Exception e) {
            log.error("Error while adding contact: {}", e.getMessage());
            throw new RuntimeException("Failed to add contact. Please try again.");
        }
    }

    @Override
    public ContactDTO updateContact(int id, ContactDTO updatedContactDTO) {
        try {
            log.info("Updating contact with ID: {}", id);
            Contact existingContact = contactRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contact not found!"));

            existingContact.setName(updatedContactDTO.getName());
            existingContact.setPhone(updatedContactDTO.getPhone());
            existingContact.setEmail(updatedContactDTO.getEmail());
            existingContact.setAddress(updatedContactDTO.getAddress());

            Contact updatedContact = contactRepository.save(existingContact);
            return convertToDTO(updatedContact);
        } catch (RuntimeException e) {
            log.error("Contact with ID {} not found for update", id);
            throw e;
        } catch (Exception e) {
            log.error("Error while updating contact: {}", e.getMessage());
            throw new RuntimeException("Failed to update contact. Please try again.");
        }
    }

    @Override
    public void deleteContact(int id) {
        try {
            log.info("Deleting contact with ID: {}", id);
            Contact contact = contactRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Contact not found!"));
            contactRepository.delete(contact);
            log.info("Contact with ID {} deleted successfully", id);
        } catch (RuntimeException e) {
            log.error("Contact with ID {} not found for deletion", id);
            throw e;
        } catch (Exception e) {
            log.error("Error while deleting contact: {}", e.getMessage());
            throw new RuntimeException("Failed to delete contact. Please try again.");
        }
    }

    private ContactDTO convertToDTO(Contact contact) {
        return new ContactDTO(contact.getName(), contact.getPhone(), contact.getEmail(), contact.getAddress());
    }

    private Contact convertToEntity(ContactDTO contactDTO) {
        return new Contact(contactDTO.getName(), contactDTO.getPhone(), contactDTO.getEmail(), contactDTO.getAddress());
    }
}
