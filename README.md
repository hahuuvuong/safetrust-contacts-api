# Spring Boot Contact Management API

A simple Spring Boot RESTful API for managing contacts.

## Table of Contents
- [Introduction](#introduction)
- [Features](#features)
- [Technologies](#technologies)
- [Usage](#usage)
- [Endpoints](#endpoints)
- [License](#license)

## Introduction
The Spring Boot Contact Management API provides a platform to manage contacts. It allows users to perform CRUD operations (Create, Read, Update, Delete) on contacts, including retrieving all contacts, getting a contact by ID, creating a new contact, updating an existing contact, and deleting a contact.

## Features
- Get all contacts
- Get a contact by ID
- Create a new contact
- Update an existing contact
- Delete a contact

## Technologies
The following technologies are used in this project:
- Java
- Spring Boot
- Spring Data JPA
- Hibernate
- Swagger
- Mockito
- H2 Database (for development environment)
- Gradle

## Usage
Once the application is up and running, you can use tools like cURL or Postman to interact with the API endpoints. See the [Endpoints](#endpoints) section for detailed information on the available endpoints and their usage.

## Endpoints
The following endpoints are available:

- GET /contacts: Get all contacts
- GET /contacts/{id}: Get a contact by ID
- POST /contacts: Create a new contact
- PUT /contacts/{id}: Update an existing contact
- DELETE /contacts/{id}: Delete a contact

For detailed information on the request and response formats, please refer to the API documentation.

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for more details.
