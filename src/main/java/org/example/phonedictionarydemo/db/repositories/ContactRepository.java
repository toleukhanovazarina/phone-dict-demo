package org.example.phonedictionarydemo.db.repositories;

import org.example.phonedictionarydemo.db.entities.Contact;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends MongoRepository<Contact, String> {
}
