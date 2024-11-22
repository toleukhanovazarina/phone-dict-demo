package org.example.phonedictionarydemo.controller;


import lombok.AllArgsConstructor;
import org.example.phonedictionarydemo.db.entities.Contact;
import org.example.phonedictionarydemo.requests.ContactRequest;
import org.example.phonedictionarydemo.service.ContactService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/contacts")
public class    ContactController {
    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        List<Contact> contacts = contactService.findAll();
        return ResponseEntity.ok(contacts);
    }

    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody ContactRequest request) {
        Contact createdContact = contactService.createContact(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable String id, @RequestBody ContactRequest request) {
        try {
            Contact contact = contactService.updateContact(id, request);
            return ResponseEntity.ok(contact);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable String id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        }
    }

    @GetMapping("/download-pdf")
    public ResponseEntity<byte[]> downloadAllContactsPdf() {
        List<Contact> contacts = contactService.findAll();

        if (contacts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No contacts available");
        }

        byte[] pdfContent = contactService.generateAllContactsPdf(contacts);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=contacts.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }

}
