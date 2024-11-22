package org.example.phonedictionarydemo.controller;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import org.example.phonedictionarydemo.db.entities.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Route("contacts")
public class ContactView extends VerticalLayout {

    private static final String BASE_URL = "http://localhost:8080/contacts";

    private final RestTemplate restTemplate;
    private final Grid<Contact> grid = new Grid<>(Contact.class);

    private final TextField nameField = new TextField("Name");
    private final TextField phoneField = new TextField("Phone");
    private final TextField emailField = new TextField("Email");

    @Autowired
    public ContactView(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        initializeLayout();
        refreshContacts();
    }

    private void initializeLayout() {
        setSizeFull();

        HorizontalLayout mainLayout = new HorizontalLayout(grid, createRightLayout());
        mainLayout.setSizeFull();
        mainLayout.setFlexGrow(2, grid);
        mainLayout.setFlexGrow(1, mainLayout.getComponentAt(1));

        configureGrid();
        add(mainLayout);
    }

    private HorizontalLayout createRightLayout() {
        VerticalLayout formLayout = createContactForm();
        Button downloadButton = createDownloadButton();

        VerticalLayout rightLayout = new VerticalLayout(formLayout, downloadButton);
        rightLayout.setSpacing(true);
        rightLayout.setSizeFull();
        return new HorizontalLayout(grid, rightLayout);
    }

    private void configureGrid() {
        grid.setColumns("name", "phone", "email");
        grid.addComponentColumn(this::createActionButtons).setHeader("Actions");
        grid.getColumns().forEach(col -> col.setAutoWidth(true));
        grid.setHeight("80vh");
    }

    private HorizontalLayout createActionButtons(Contact contact) {
        Button editButton = new Button("Edit", event -> populateForm(contact));
        Button deleteButton = new Button("Delete", event -> handleDelete(contact));
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ERROR);

        return new HorizontalLayout(editButton, deleteButton);
    }

    private VerticalLayout createContactForm() {
        VerticalLayout formLayout = new VerticalLayout(nameField, phoneField, emailField);
        Button saveButton = new Button("Save", event -> handleSave());
        formLayout.add(saveButton);
        formLayout.setSpacing(true);
        formLayout.setPadding(true);
        return formLayout;
    }

    private Button createDownloadButton() {
        return new Button("Download Dictionary", event -> downloadContactsAsPdf());
    }

    private void refreshContacts() {
        try {
            ResponseEntity<List<Contact>> response = restTemplate.exchange(
                    BASE_URL, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {}
            );

            List<Contact> contacts = response.getBody();
            grid.setItems(contacts != null ? contacts : List.of());
        } catch (Exception e) {
            Notification.show("Error fetching contacts: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    private void clearForm() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        nameField.getElement().removeProperty("contactId");
    }

    private void handleSave() {
        try {
            String contactId = nameField.getElement().getProperty("contactId");
            Contact contact = new Contact(
                    nameField.getValue(),
                    phoneField.getValue(),
                    emailField.getValue()
            );

            if (contactId != null && !contactId.isBlank()) {
                restTemplate.put(BASE_URL + "/" + contactId, contact);
                Notification.show("Contact updated");
            } else {
                restTemplate.postForEntity(BASE_URL, contact, Contact.class);
                Notification.show("Contact saved");
            }

            refreshContacts();
            clearForm();
        } catch (Exception e) {
            Notification.show("Error saving contact: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    private void handleDelete(Contact contact) {
        try {
            restTemplate.delete(BASE_URL + "/" + contact.getId());
            Notification.show("Contact deleted");
            refreshContacts();
        } catch (Exception e) {
            Notification.show("Error deleting contact: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }

    private void populateForm(Contact contact) {
        nameField.setValue(contact.getName() != null ? contact.getName() : "");
        phoneField.setValue(contact.getPhone() != null ? contact.getPhone() : "");
        emailField.setValue(contact.getEmail() != null ? contact.getEmail() : "");
        nameField.getElement().setProperty("contactId", contact.getId());
    }

    private void downloadContactsAsPdf() {
        try {
            UI.getCurrent().getPage().setLocation(BASE_URL + "/download-pdf");
        } catch (Exception e) {
            Notification.show("Error generating PDF: " + e.getMessage(), 3000, Notification.Position.MIDDLE);
        }
    }
}
