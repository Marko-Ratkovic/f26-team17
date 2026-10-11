# Requirements – GlowUp

**Project Name:** GlowUp \
**Team:** Marko Ratkovic - Customer, Hana Lenh - Provider, Alisena Nasirali - SysAdmin \
**Course:** CSC 340\
**Version:** 1.0\
**Date:** 2026-09-18

---
## 1. Overview
**Vision.** GlowUp is a platform that pairs customers who needs hair, nail, or makeup services with professional nail technicians, barbers, and makeup artists.

**Glossary** Terms used in the project
- **Customer:** A person seeking professional nail technicians, barbers, or makeup artists.
- **Provider:** Nail technicians, barbers, makeup artists.
- **System Admin:** The platform user responsible for overseeing GlowUp's daily operation.
- **Customer profile:**  Contains personal details, contact info, booking history, and service/style preferences.
- **Provider profile:**  Contains personal details, contact info, and service offerings.


**Primary Users / Roles.**
- **Customer** — Find professional provider and book appointment.
- **Provider** — Attract clients and manage services.
- **SysAdmin** —  Maintain platform quality and security.

**Scope**
- User profiles (customers and providers)
- Search and browse providers by service menu.
- Appointment requests and provider confirmation
- Reviews and ratings

**Out of scope (deferred).**
- Payment processing, provider payouts, refunds, and other automated financial policies
- SMS/text-message notifications and phone-number verification
- Complex rescheduling and negotiation workflows

Booking requests do not collect or authorize payment in this project. A customer selects an available slot and submits a request; the provider confirms or declines it. Only a confirmed appointment is treated as a scheduled booking. Any future payment integration requires a separately defined payment, cancellation, refund, and payout policy.
---

## 2. Functional Requirements (User Stories)

### 2.1 Customer Stories
- **US‑1 — Create/modify customer profile**  
  _Story:_ As a customer, I want to manage my contact information, private safety notes, and booking preferences so that I can control what I share and make discovery more relevant.
  _Acceptance:_
  ```gherkin
  Scenario: Register with valid credentials
    Given I am not registered
    When  I provide my full name and valid credentials
    Then  my account is created and a single-use verification link is emailed to me
    And I cannot log in until I open the link to verify my email address
    And after verifying, I can log in and view and modify my profile
    And my phone number may be left blank
    And changing my email requires verifying the new address before it replaces the current one
  ```

  ```gherkin
  Scenario: Protect customer beauty and safety notes
    Given I am a customer managing optional allergy or sensitivity notes
    When I save or view those notes
    Then the notes must not appear on my public profile or ordinary provider search results
    And a provider may access them only in the context of my confirmed appointment
    And I can distinguish no known allergies from information not provided
  ```

  ```gherkin
  Scenario: Manage booking and notification preferences
    Given I am logged in as a customer
    When I update preferred service categories, days, or time ranges
    Then my preferences may guide discovery but must not limit access to services
    And I can manage booking notifications separately from those preferences
    And booking notifications are shown in the app and emailed to my verified address
    And I can turn off booking emails, but not essential account emails such as verification and password reset
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

- **US‑3 — Request an appointment**
  _Story:_ As a customer, I want to request an available time for an approved service so that the provider can confirm the appointment and I can track its status in my booking history.
  _Acceptance:_
  ```gherkin
  Scenario: Request an appointment
    Given I am viewing an approved service and a provider's available time slots
    When I select a slot and submit an appointment request
    Then the request is recorded with Requested status and appears in my appointment history
    And the provider can confirm or decline the request
    And no payment is collected or authorized as part of this project
  ```

  ```gherkin
  Scenario: View the provider's booking decision
    Given I have submitted an appointment request
    When the provider confirms or declines it
    Then the appointment status and decision timestamp are recorded
    And I can see the updated status in my booking history
    And only a confirmed appointment is treated as scheduled
  ```

- **US‑4 — Write a review after a service**  
  _Story:_ As a customer, I want to write a review after my appointment so that other customers can make informed decisions about the provider.
  _Acceptance:_
  ```gherkin
  Scenario: Publish a review for a completed appointment
    Given I have completed a service appointment with a provider
    And I have not already reviewed that appointment
    When I submit a rating and review for the appointment
    Then the review should be published and visible to other customers and the provider
    And I should be able to report a review or provider reply that violates the community guidelines
  ```

### 2.2 Provider Stories

- **US-5 — Create and update provider profile** \
  *Story:* As a provider, I want to manage private account details separately from my public professional profile so customers can evaluate and contact me appropriately.
  *Acceptance:*

  ```gherkin
  Scenario: Create a provider profile
    Given I choose the Provider role during registration
    When I submit valid account and professional profile information
    Then my private account details are stored separately from public profile fields
    And only approved public profile information is discoverable by customers
  ```

  ```gherkin
  Scenario: Use the platform while awaiting account approval
    Given I registered as a provider and verified my email address
    And a SysAdmin has not yet approved my account
    When I log in
    Then I can complete my profile and weekly availability
    But I cannot submit listings for review or receive booking requests until my account is approved
  ```

  ```gherkin
  Scenario: Update public provider profile
    Given I am authenticated as a provider
    When I update my professional biography, address, or certifications
    Then the server validates and saves the update
    And private contact details are not added to public search results
    And certifications are shown as self-reported, not verified by GlowUp
  ```

- **US-6 — Define services and pricing** \
  *Story:* As a provider, I want to submit accurate service details for review so that compliant services can be published for customers.
  *Acceptance:*

  ```gherkin
  Scenario: Submit a new service for approval
    Given I am logged in as a provider
    When I submit a service name, category, description, image, duration, and price
    Then the listing should have Pending review status
    And customers should not see or book the listing before approval
  ```

  ```gherkin
  Scenario: Revise a service listing after review
    Given an admin requested changes to my listing with an explanation
    When I update the listing and submit it again
    Then the listing should return to Pending review
    And it should remain unavailable to customers until approved
  ```

- **US-7 — Manage availability and appointments** \
  *Story:* As a provider, I want to define weekly availability and manage booking requests so that customers can request realistic appointment times.
  *Acceptance:*

  ```gherkin
  Scenario: Publish weekly availability
    Given I am authenticated as a provider
    When I set available time ranges for each day
    Then the server validates the ranges and prevents overlapping availability
    And new booking requests can use only available slots for approved services
    And changing availability does not silently cancel existing confirmed appointments
  ```

  ```gherkin
  Scenario: Confirm or decline a booking request
    Given a customer has requested an available slot for an approved service
    When I confirm or decline the request
    Then the appointment transitions to Confirmed or Declined with an audit timestamp
    And confirming reserves the slot atomically so it cannot be double-booked
    And the customer receives an in-app and email notification reflecting the outcome
  ```

  ```gherkin
  Scenario: View customer appointment details
    Given I am the provider assigned to a confirmed appointment
    When I open its appointment details
    Then I can see only the contact and private safety information needed for that appointment
    And the server denies access to providers who are not assigned to the appointment
    And the appointment remains in booking history when later cancelled or completed
  ```

- **US-8 — Respond to reviews** \
  *Story:* As a provider, I want to respond to reviews, so that I can engage with customer.
  *Acceptance:*

  ```gherkin
  Scenario: Respond to a review
    Given I am logged in as the provider for a completed appointment
    When I submit a response to the customer's review
    Then the response should be visible with the review
    And users should be able to report the response if it violates the community guidelines
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

  ```gherkin
 Scenario: Approve a pending provider account
  Given I am logged in as a SysAdmin
  And a provider has verified their email and is awaiting approval
  When I approve the account
  Then the provider can submit listings for review and receive booking requests
  And the decision is recorded in the admin audit log
  ```

**US-10 — Moderate services** \
*Story:* As a SysAdmin, I want to review new service listings before publication, so that customers can discover accurate and policy-compliant services.\
*Acceptance:*

  ```gherkin
  Scenario: Approve a compliant new service
  Given I am logged in as a SysAdmin
  And a provider has submitted a new service listing
  When I verify the service details comply with GlowUp's terms
  Then I can approve the listing and make it visible to customers
  ```

  ```gherkin
  Scenario: Request changes to a service listing
  Given I am reviewing a new service listing with a fixable issue
  When I return it to the provider with a specific explanation
  Then the listing should have Changes requested status
  And it should remain hidden until the provider resubmits and the listing is approved
  ```

  ```gherkin
  Scenario: Reject a prohibited service
  Given I am reviewing a new service listing that offers a prohibited service
  When I reject the listing and record the reason
  Then the listing should remain unavailable to customers
  And the provider should be shown the decision and reason
  ```

**Service listing review policy**
- Every new service listing is reviewed before it appears in customer search or can be booked.
- Admins check service name, category, description, image, duration, price, and compliance with GlowUp's terms and safety expectations.
- A requested-change listing remains hidden until the provider resubmits it and it is approved.
- Editing an already approved listing creates a pending revision; the currently approved version remains visible and unchanged until the revision is approved.
- Show providers their current review status and any reason or requested changes. Keep the decision and reason in the moderation history.

**Provider appointment and availability policy**
- A customer request begins in `REQUESTED`; the provider may confirm or decline it. Confirmed appointments, completed appointments, and cancellations are distinct states. Preserve transitions and timestamps in history rather than deleting records.
- Confirming a request must reserve the selected slot atomically. Notifications report committed state changes and must not be sent as if a failed transition succeeded.
- Weekly availability governs new requests. Editing a schedule does not automatically move or cancel existing confirmed bookings.
- Providers may access customer contact details and safety notes only through appointments assigned to them and only for the appointment status authorized by the service policy. Enforce this in the backend, not through UI visibility.
- Payment authorization, collection, provider payouts, refunds, and payment-related cancellation rules are out of scope; do not imply that a request or confirmation charges a customer.

**US-11 — Moderate reported reviews and replies** \
*Story:* As a SysAdmin, I want to review reports about customer reviews and provider replies, so that I can address policy violations without delaying ordinary feedback.\
*Acceptance:*

 ```gherkin
  Scenario: Resolve a report about public review content
  Given I am logged in as a SysAdmin
  And a user has reported a customer review or provider reply
  When I review the content and the report reason
  Then I can keep compliant content visible or hide content that violates the guidelines
  And the moderation decision and reason are recorded
   ```

**Review and reply policy**
- Reviews are available only for completed appointments, with at most one customer review per appointment.
- Submitted reviews are published immediately.
- Customers and providers may report reviews or provider replies that violate the community guidelines.
- Reportable violations include harassment, threats, hate or explicit content, personal information, spam, and unrelated content.
- Moderators review the reported content and reason, then keep it visible or hide it. Moderation decisions include a reason and are retained in the audit history; content is not silently or permanently deleted as the default workflow.


**US-12 — View usage statistics** \
*Story:* As a SysAdmin, I want to view appointment activity, user growth, provider activity, and service-review volume
so that I can manage and monitor platform operations.\
*Acceptance:*

```gherkin
  Scenario: View operational platform metrics
  Given I am logged in as a SysAdmin
  When I select a reporting period
  Then I can view appointment activity, user growth, provider activity, and service-review volume for that period
  And the metrics do not report sales, revenue, payouts, or other financial activity
   ```

### 2.4 Customer privacy and account settings

- **US-13 — Manage sign-in security**
  _Story:_ As a customer, I want to change my password without exposing it so that I can protect my account.
  - Require the current password for an authenticated password change and validate the new password on the server.
  - Store only a password hash; never return or email an existing password.
  - Use a separate password-reset flow when the customer cannot sign in: email a single-use link that expires after 30 minutes, store only a hash of the reset token, and show the same response whether or not the email belongs to an account.

- **US-14 — Request account deactivation or deletion**
  _Story:_ As a customer, I want to pause or close my account with a clear explanation of the effect on my appointments and retained records.
  - Re-authenticate and confirm a deactivation or deletion request; track its status and processing outcome.
  - Explain the effect on confirmed appointments before processing the request.
  - Do not silently or immediately delete booking, review, report, or moderation history. Define retention periods and remove or de-identify personal details when retention is no longer required.
  - Keep account-request status and administrative decisions auditable without retaining unnecessary sensitive information.

**Customer profile privacy rules**
- Store allergy and sensitivity notes as optional, private customer data; do not expose them through public profile or provider-search responses.
- Permit a provider to view safety notes only through a confirmed appointment associated with that provider. Enforce this on the server for each request; hiding a UI element is not authorization.
- Represent allergy status explicitly (known notes, no known allergies, or not provided) so blank data is not interpreted as “no allergies.”
- Separate saved-provider relationships from the providers and counts derived from appointment history.
- Keep notification channel choices separate from booking preferences. Do not allow disabling essential account emails (verification, password reset, security). Booking emails may be turned off. SMS is out of scope.

---