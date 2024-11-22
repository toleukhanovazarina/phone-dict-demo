package org.example.phonedictionarydemo.service.impl;


import com.itextpdf.text.Document;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.AllArgsConstructor;
import org.example.phonedictionarydemo.db.entities.Contact;
import org.example.phonedictionarydemo.db.repositories.ContactRepository;
import org.example.phonedictionarydemo.requests.ContactRequest;
import org.example.phonedictionarydemo.service.ContactService;
import org.example.phonedictionarydemo.util.EntityUpdateUtil;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
@AllArgsConstructor
public class ContactServiceImpl implements ContactService {
    private final ContactRepository contactRepository;

    @Override
    public List<Contact> findAll() {
        return contactRepository.findAll();
    }

    @Override
    public Contact createContact(ContactRequest request) {
        Contact contact = new Contact();
        contact.setName(request.getName());
        contact.setEmail(request.getEmail());
        contact.setPhone(request.getPhone());
        return contactRepository.save(contact);
    }

    @Override
    public Contact updateContact(String id, ContactRequest request) {
        Contact existingContact = contactRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));

        EntityUpdateUtil.updateField(existingContact::setName, request.getName());
        EntityUpdateUtil.updateField(existingContact::setPhone, request.getPhone());
        EntityUpdateUtil.updateField(existingContact::setEmail, request.getEmail());

        return contactRepository.save(existingContact);

    }

    @Override
    public void deleteContact(String id) {
        contactRepository.deleteById(id);
    }

    @Override
    public byte[] generateAllContactsPdf(List<Contact> contacts) {
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Document document = new Document();
            PdfWriter.getInstance(document, out);
            document.open();

            document.add(new Paragraph("All Contacts"));
            document.add(new Paragraph(" "));

            for (Contact contact : contacts) {
                document.add(new Paragraph("Name: " + contact.getName()));
                document.add(new Paragraph("Phone: " + contact.getPhone()));
                document.add(new Paragraph("Email: " + contact.getEmail()));
                document.add(new Paragraph(" "));
            }

            document.close();
            return out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
