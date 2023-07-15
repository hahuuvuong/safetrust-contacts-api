package com.safetrust.demo;

import com.safetrust.demo.controller.ContactController;
import com.safetrust.demo.exception.CustomizedResponseEntityExceptionHandler;
import com.safetrust.demo.model.Contact;
import com.safetrust.demo.repository.ContactRepository;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;x
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
public class ContactControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ContactRepository contactRepository;

    @InjectMocks
    private ContactController contactController;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(contactController)
                .setControllerAdvice(new CustomizedResponseEntityExceptionHandler())
                .build();
    }

    @Test
    public void testGetAllContacts_DefaultParameters() {
        // Prepare test data
        List<Contact> contactList = new ArrayList<Contact>();
        contactList.add(createValidContact(1L, "John Doe", "john.doe@example.com", "1234567890", "123 Main St"));
        contactList.add(createValidContact(2L, "Jane Smith", "jane.smith@example.com", "9876543210", "456 Elm St"));

        Pageable pageable = PageRequest.of(0, 10);
        Page<Contact> pageContacts = new PageImpl<>(contactList, pageable, contactList.size());

        // Mock the contactRepository behavior
        when(contactRepository.findAll(pageable)).thenReturn(pageContacts);

        // Perform the GET request
        ResponseEntity<Page<Contact>> response = contactController.getAllContacts(0, 10);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(contactList.size(), response.getBody().getContent().size());
        // Add more assertions based on your specific requirements
    }

    @Test
    public void testGetContactById_WithExistingId_ReturnsContact() {
        // Mock the contactRepository behavior
        Contact contact = createValidContact(1L, "John Doe", "john.doe@example.com", "1234567890", "123 Main St");
        when(contactRepository.findById(1L)).thenReturn(Optional.of(contact));

        // Invoke the getContactById method
        ResponseEntity<Contact> response = contactController.getContactById(1L);

        // Verify the response
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(contact, response.getBody());
    }

    @Test
    public void testGetContactById_WithNonExistingId_ReturnsNotFound() {
        // Mock the contactRepository behavior
        when(contactRepository.findById(2L)).thenReturn(Optional.empty());

        // Invoke the getContactById method with a non-existing id
        ResponseEntity<Contact> response = contactController.getContactById(2L);

        // Verify the response
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
    @Test
    public void testCreateContact_ValidContact_ReturnsCreatedContact() {
        // Create a valid contact
        Contact contact = new Contact();
        contact.setName("John Doe");
        contact.setEmailAddress("john@example.com");
        contact.setTelephoneNumber("+84123456789");
        contact.setPostalAddress("123 Main St");

        // Mock the contactRepository behavior
        Contact savedContact = new Contact();
        savedContact.setId(1L);
        savedContact.setName(contact.getName());
        savedContact.setEmailAddress(contact.getEmailAddress());
        savedContact.setTelephoneNumber(contact.getTelephoneNumber());
        savedContact.setPostalAddress(contact.getPostalAddress());
        when(contactRepository.save(any(Contact.class))).thenReturn(savedContact);

        // Invoke the createContact method
        ResponseEntity<Contact> response = contactController.createContact(contact);

        // Verify the response
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(savedContact, response.getBody());
    }

    @Test
    public void testCreateContact_WithInvalidContact_ReturnsBadRequest() throws IOException {
        // Create a new contact
        Contact contact = new Contact();
        // Set the required contact properties

        // Convert the contact object to JSON
        String contactJson = "{\"name\":\"" + contact.getName() + "\",\"emailAddress\":\"" + contact.getEmailAddress() + "\",\"telephoneNumber\":\"" + contact.getTelephoneNumber() + "\",\"postalAddress\":\"" + contact.getPostalAddress() + "\"}";

        // Create an HttpClient instance
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create an HttpPost request with the contact data
        HttpPost request = new HttpPost("http://localhost:8080/contacts");
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(contactJson, ContentType.APPLICATION_JSON));

        // Send the request and get the response
        CloseableHttpResponse response = httpClient.execute(request);

        // Get the response status code
        int statusCode = response.getStatusLine().getStatusCode();

        // Optionally, assert the response status code and validate the created contact details

        // Assert the response status code
        assertEquals(HttpStatus.BAD_REQUEST.value(), statusCode);

        response.close();
        httpClient.close();
    }

    @Test
    public void testUpdateContact() throws IOException {
        // Create a new contact
        Contact updatedContact = createValidContact(1L, "John Doe", "john.doe@example.com", "0987654321", "123 Main St");
        // Set the updated contact properties

        // Convert the updated contact object to JSON
        String updatedContactJson = "{\"name\":\"" + updatedContact.getName() + "\",\"emailAddress\":\"" + updatedContact.getEmailAddress() + "\",\"telephoneNumber\":\"" + updatedContact.getTelephoneNumber() + "\",\"postalAddress\":\"" + updatedContact.getPostalAddress() + "\"}";

        // Create an HttpClient instance
        CloseableHttpClient httpClient = HttpClients.createDefault();

        // Create an HttpPut request with the updated contact data
        String url = "http://localhost:8080/contacts/{id}";
        String id = "1"; // Replace with the actual contact ID
        url = url.replace("{id}", id);
        HttpPut request = new HttpPut(url);
        request.setHeader("Content-Type", "application/json");
        request.setEntity(new StringEntity(updatedContactJson, ContentType.APPLICATION_JSON));

        // Send the request and get the response
        HttpResponse response = httpClient.execute(request);

        // Get the response status code
        int statusCode = response.getStatusLine().getStatusCode();

        // Read the response body
        HttpEntity responseEntity = response.getEntity();
        String responseBody = EntityUtils.toString(responseEntity);

        // Optionally, assert the response status code and validate the updated contact details

        // Assert the response status code
        assertEquals(HttpStatus.OK.value(), statusCode);

        // Close the HttpClient
        httpClient.close();
    }
    @Test
    public void testDeleteContact_ExistingContact_Success() {
        // Create a dummy contact ID for testing
        Long contactId = 1L;

        // Create an Optional containing a dummy contact
        Contact contact = new Contact();
        contact.setId(contactId);
        Optional<Contact> optionalContact = Optional.of(contact);

        // Mock the contactRepository.findById() method to return the Optional
        Mockito.when(contactRepository.findById(contactId)).thenReturn(optionalContact);

        // Perform the delete operation
        ResponseEntity<Void> responseEntity = contactController.deleteContact(contactId);

        // Verify the behavior and assertions
        assertEquals(HttpStatus.NO_CONTENT, responseEntity.getStatusCode());
        Mockito.verify(contactRepository, Mockito.times(1)).delete(contact);
    }

    @Test
    public void testDeleteContact_NonExistingContact_NotFound() {
        // Create a dummy contact ID for testing
        Long contactId = 1L;

        // Create an empty Optional
        Optional<Contact> optionalContact = Optional.empty();

        // Mock the contactRepository.findById() method to return the Optional
        Mockito.when(contactRepository.findById(contactId)).thenReturn(optionalContact);

        // Perform the delete operation
        ResponseEntity<Void> responseEntity = contactController.deleteContact(contactId);

        // Verify the behavior and assertions
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());
        Mockito.verify(contactRepository, Mockito.never()).delete(Mockito.any(Contact.class));
    }
    private Contact createValidContact(Long id, String name, String email, String telephone, String address) {
        Contact contact = new Contact();
        contact.setId(id);
        contact.setName(name);
        contact.setEmailAddress(email);
        contact.setTelephoneNumber(telephone);
        contact.setPostalAddress(address);
        return contact;
    }
}