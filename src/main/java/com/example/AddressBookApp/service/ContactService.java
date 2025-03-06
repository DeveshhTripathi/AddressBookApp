package com.example.AddressBookApp.service;

import com.example.AddressBookApp.dto.ContactDTO;

import java.util.List;

public interface ContactService {
    List<ContactDTO> getAllContacts();
    ContactDTO getContactById(int id);
    ContactDTO addContact(ContactDTO contactDTO);
    ContactDTO updateContact(int id, ContactDTO updatedContactDTO);
    void deleteContact(int id);
}
