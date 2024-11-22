package org.example.phonedictionarydemo.requests;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContactRequest {
    private String name;
    private String phone;
    private String email;
}
