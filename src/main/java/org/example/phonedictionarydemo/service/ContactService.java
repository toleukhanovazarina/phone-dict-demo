package org.example.phonedictionarydemo.service;

import org.example.phonedictionarydemo.db.entities.Contact;
import org.example.phonedictionarydemo.requests.ContactRequest;

import java.util.List;


public interface ContactService {
    List<Contact> findAll();

    Contact createContact(ContactRequest request);

    Contact updateContact(String id, ContactRequest request);

    void deleteContact(String id);

    byte[] generateAllContactsPdf(List<Contact> contacts);
}
