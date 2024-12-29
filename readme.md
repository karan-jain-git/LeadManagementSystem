# Lead Management System

A comprehensive system for managing restaurant leads, tracking interactions, and monitoring Key Account Manager (KAM) performance.

## Project Overview

The Lead Management System is designed to streamline the process of managing restaurant leads and their interactions with Key Account Managers. The system provides features for:

- Managing restaurant leads and their lifecycle
- Tracking customer interactions and follow-ups
- Managing contact information for restaurant personnel
- Monitoring KAM performance and effectiveness
- Automating follow-up scheduling and reminders

## System Requirements

- Java 21 or higher
- Spring Boot 3.x
- PostgreSQL 13 or higher
- Maven 3.8+
- Lombok plugin for your IDE

## Installation Instructions

1. Unzip the LeadManagement.zip file:


2. Configure database properties in `application.properties`:
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/lead_management
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Build the project:
```bash
mvn clean install
```

## Running Instructions

1. Start the application:
```bash
mvn spring-boot:run
```

2. Access the API at:
```
http://localhost:8080/api
```

## Test Execution Guide

Run the test suite using:
```bash
mvn test
```

To run specific test classes:
```bash
mvn test -Dtest=RestaurantLeadManagementServiceTest
```

## API Documentation

### Lead Management Endpoints

#### Create Lead
```http
POST /api/leads
Content-Type: application/json

{
    "name": "Restaurant Name",
    "address": "Location",
    "callFrequency": 7
}
```

#### Get All Leads
```http
GET /api/leads
```

#### Get Today's Calls
```http
GET /api/leads/today-calls
```

#### Update Lead Status
```http
PATCH /api/leads/{leadId}/status?status=IN_PROGRESS
```

### Contact Management Endpoints

#### Add Contact
```http
POST /api/contacts/lead/{leadId}
Content-Type: application/json

{
    "name": "Contact Name",
    "role": "Manager",
    "email": "contact@email.com",
    "phone": "+1234567890"
}
```

#### Get Contacts by Lead
```http
GET /api/contacts/lead/{leadId}
```

### KAM Management Endpoints

#### Add KAM
```http
POST /api/kams
Content-Type: application/json

{
    "name": "KAM Name",
    "email": "kam@email.com",
    "phone": "+1234567890"
}
```

#### Get KAM Performance
```http
GET /api/kams/{kamId}/performance
```

## Sample Usage Examples

### Recording an Interaction

```java
RestaurantLeadManagementService leadService = context.getBean(RestaurantLeadManagementService.class);

Interaction interaction = new Interaction();
interaction.setType(InteractionType.CALL);
interaction.setNotes("Discussed menu integration");
interaction.setInteractionDate(LocalDateTime.now());

leadService.recordInteraction(leadId, interaction);
```

### Assigning Leads to KAM

```java
KAMService kamService = context.getBean(KAMService.class);

List<Long> leadIds = Arrays.asList(1L, 2L, 3L);
KAM kam = kamService.assignLeadsToKAM(kamId, leadIds);
```

### Getting KAM Performance Metrics

```java
KAMService kamService = context.getBean(KAMService.class);

// Get top performing KAMs
List<KAMPerformanceDTO> topKAMs = kamService.getTopPerformingKAMs(3);

// Get underperforming KAMs
List<KAMPerformanceDTO> underperformingKAMs = kamService.getUnderPerformingKAMs(3);
```

## Data Models

### RestaurantLead
- Basic information about the restaurant lead
- Current status in the sales pipeline
- Assigned KAM
- Call scheduling information

### Contact
- Contact details for restaurant personnel
- Primary contact designation
- Role information

### Interaction
- Type of interaction (CALL, EMAIL, MEETING, ORDER_PLACED)
- Interaction date and notes
- Associated lead information

### KAM (Key Account Manager)
- KAM personal information
- Assigned leads
- Performance metrics

## Error Handling

The system implements a global error handling mechanism with specific exceptions:
- `LeadNotFoundException`
- `KAMNotFoundException`
- `InteractionNotFoundException`
- `KAMPerformanceException`

All errors return a standardized `ErrorResponse` object with appropriate HTTP status codes.
