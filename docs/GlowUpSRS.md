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

- **US‑1 —** \
  *Story:* As a customer, I want … so that …\
  *Acceptance:*

  ```gherkin
  Scenario: <happy path>
    Given <preconditions>
    When  <action>
    Then  <observable outcome>
  ```

- **US‑2 —** \
  *Story:* As a customer, I want … so that …\
  *Acceptance:*

  ```gherkin
  Scenario: <happy path>
    Given <preconditions>
    When  <action>
    Then  <observable outcome>
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

- **US‑30 —** \
  *Story:* As a sysadmin, I want … so that …\
  *Acceptance:*

  ```gherkin
  Scenario: <happy path>
    Given <preconditions>
    When  <action>
    Then  <observable outcome>
  ```

- **US‑31 —** \
  *Story:* As a sysadmin, I want … so that …\
  *Acceptance:*

  ```gherkin
  Scenario: <happy path>
    Given <preconditions>
    When  <action>
    Then  <observable outcome>
  ```

---

## 3. Non‑Functional Requirements (make them measurable)

- **Performance:** description
- **Availability/Reliability:** description
- **Security/Privacy:** description
- **Usability:** description

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
