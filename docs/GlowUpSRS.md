# Requirements – GlowUp

**Project Name:** GlowUp \
**Team:** Marko Ratkovic - Customer, Hana Lenh - Provider, Alisena Nasirali - SysAdmin \
**Course:** CSC 340\
**Version:** 1.0\
**Date:** 2026-09-18

---
## 1. Overview
**Vision.** GlowUp is a platform that pairs customers seeking needs hairs, nails, or makeup services with professional nail technicians, barbers, and makeup artists.

**Glossary** Terms used in the project
- **Customer:** A person seeking professional nail technicians, barbers, or makeup artists.
- **Provider:** Nail technicians, barbers, makeup artists.
- **System Admin:** The platform user responsible for overseeing GlowUp's daily operation.
- **Customer profile:**  Contains personal details, contact info, booking history, and service/style preferences.
- **Provider profile:**  Contains personal details, contact info, service offerings, and professional certifications/licenses.
- **Services:** The specific nail, hair, or beauty service. 
- **Session:** A scheduled appointment between a customer and a provider for service.

**Primary Users / Roles.**
- **Customer** — Find professional provider and book appointment.
- **Provider** — Attract clients and manage services.
- **SysAdmin (optional)** —  Maintain platform quality and security..

**Scope (this semester).**
- User profiles (customers and providers)
- Search and browse providers by service menu.
- Booking service session
- Basic system tracking
- Reviews and ratings

**Out of scope (deferred).**
- Automated Financial Policies
- Complex rescheduling & Negotiations

> This document is **requirements‑level** and solution‑neutral; design decisions (UI layouts, API endpoints, schemas) are documented separately.
---

## 2. Functional Requirements (User Stories)

### 2.1 Customer Stories
- **US‑1 — Create/modify customer profile**  
  _Story:_ As a customer, I want to create and modify a personal profile with contact info and beauty and grooming preferences so that the providers can understand my preferences.  
  _Acceptance:_
  ```gherkin
  Scenario: Register with valid credentials
    Given I am not registered
    When  I provide valid registration details
    Then  I should be successfully registered and logged in
    And I can view and modify my profile
  ```

- **US‑2 — Browse providers via a service menu**  
  _Story:_ As a customer, I want to browse stylists, barbers, and make up artists by service menu so that I can find a relevant matches.  
  _Acceptance:_
  ```gherkin
  Scenario: Browse providers by service menu
    Given I am logged in as a customer
    When  I select a service via the service menu
    Then  I should see a list of providers who specialize in that service
  ```

- **US‑3 — Book an appointment**  
  _Story:_ As a customer, I want to book an appointment for a chosen service and time slot so that I can pay online and track it in my booking history.  
  _Acceptance:_
  ```gherkin
  Scenario: Booking an appointment
    Given I am viewing a provider's available time slots
    When  I select a time slot for a service and confirm payment
    Then  the appointment should be confirmed and added to my booking history 
  ```

- **US‑4 — Write a review after a service**  
  _Story:_ As a customer, I want to write a review after my appointment so that other customers can make informed decisions about the provider.
  _Acceptance:_
  ```gherkin
  Scenario: Write a review after a appointment
    Given I have completed a service appointment with a provider
    When  I submit a review for that appointment
    Then  the review should be saved and visible to other customers and the provider  
  ```

### 2.2 Provider Stories

- **US-5 — Create and update provider profile** \
  *Story:* As a provider, I want to create and update my profile so that customer can reach out to book my services.
  *Acceptance:*

  ```gherkin
  Scenario: <Create and update provider profile>
    Given <I do not have a profile>
    When  <I provide my details and submit the form>
    Then  <my profile should be created>
    And   <the profile should be visible to customers>
  ```

- **US-6 — Define services and pricing** \
  *Story:* As a provider, I want to define and pricing my services so that customers can can select and schedule the right services.
  *Acceptance:*

  ```gherkin
  Scenario: <Define and pricing>
    Given <I am logged in as a provider>
    When  <I add my services and set pricing>
    Then  <the services should be saved and visible to customers>
  ```

  **US-7 — Manage customer's booking** \
  *Story:* As a provider, I want to manage customer's booking slot so that I can be flexible with my schedule and customer’s schedule.
  *Acceptance:*

  ```gherkin
  Scenario: <Manage booking>
    Given <I am logged in as a provider>
    When  <I need to confirm or cancel the booking>
    Then  <the appointment status should update accordingly on my schedule>
    And   <the customer should receive an automated notification reflecting the decision>
  ```

**US-8 — Respond to reviews** \
  *Story:* As a provider, I want to respond to reviews, so that I can engage with customer.
  *Acceptance:*

  ```gherkin
  Scenario: <Response to reviews>
    Given <I am logged in as a provider>
    When  <I receive a review for one of my sessions>
    Then  <I should be able to submit a response to the review>
  ```

### 2.3 SysAdmin Stories

**US-9 — Manage user access** \
*Story:* As a SysAdmin, I want to approve, suspend, or 
reinstate customer and provider accounts, so that I can keep
the platform secure and enforce policies.\
*Acceptance:*

  ```gherkin
 Scenario: <Suspend a policy-violating account>
  Given <I am logged in as a SysAdmin>
  When <I suspend a provider or customer account for a policy violation>
  Then <the user should immediately lose access to the platform>
  ```

**US-10 — Moderate services** \
*Story:* As a SysAdmin, I want to moderate services and content offered by providers,
  so that I can remove fraudulent or policy-violating listings.\
*Acceptance:*

  ```gherkin
  Scenario: <Remove a fraudulent service>
  Given <I am logged in as a SysAdmin>
  When <I flag a provider's service listing as fraudulent>
  Then <the service should be immediately removed from the public search menu>
  ```
**US-11 — Moderate reviews** \
*Story:* As a SysAdmin, I want to moderate customer and provider reviews, so that 
I can ensure validity and remove spam\
*Acceptance:*

 ```gherkin
  Scenario: <Delete a spam review>
  Given <I am logged in as a SysAdmin>
  When <I determine a posted review is spam or invalid>
  Then <the review should be permanently deleted from the platform>
   ```
**US-12 — View usage statistics** \
*Story:* As a SysAdmin, I want to view sale volume, bookings, and activities,
so that I can manage and monitor platform growth.\
*Acceptance:*

```gherkin
  Scenario: <View platform analytics>
  Given <I am logged in as a SysAdmin>
  When <I navigate to the admin dashboard>
  Then <I should see accurate metrics for sales volume, active bookings, and user activity>
   ```

---
## 3. Non‑Functional Requirements (make them measurable)
- **Performance:** 95% of service search and provider listing responses should be returned in less than 2 seconds under typical load.
- **Availability/Reliability:** The system should be available 99.5% of the time, with planned maintenance windows communicated in advance.
- **Security/Privacy:** The system must implement secure authentication and authorization mechanisms. All sensitive data should be encrypted in transit and at rest.
- **Usability:** New customers should be able to complete registration and book their first appointment within 5 minutes without external assistance.
---

## 4. Assumptions, Constraints, and Policies
- list any rules, policies, assumptions, etc.

---

## 5. Milestones (course‑aligned)
- **M1 Requirements** — this file + stories opened as issues.
- **M2 High‑fidelity prototype** — core customer/provider flows fully interactive.
- **M3 Design** — architecture, schema, API outline.
- **M4 Backend API** — key endpoints + tests.
- **M5 Increment** — ≥2 use cases end‑to‑end.
- **M6 Final** — complete system & documentation.

---
## 6. Change Management

- Stories are living artifacts; changes are tracked via repository issues and linked pull requests.
- Major changes should update this SRS
