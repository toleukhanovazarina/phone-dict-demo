package org.example.phonedictionarydemo.db.entities;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "contacts")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Contact {

    @Id
    String id;
    String name;
    String phone;
    String email;

    public Contact(String name, String phone, String email) {
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
}
