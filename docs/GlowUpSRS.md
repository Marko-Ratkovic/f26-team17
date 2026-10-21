# Requirements – GlowUp

**Project Name:** GlowUp \
**Team:** Marko Ratkovic - Customer, Hana Lenh - Provider, Alisena Nasirali - SysAdmin \
**Course:** CSC 340\
**Version:** 1.1\
**Date:** 2026-10-11

---
## 1. Overview
**Vision.** GlowUp connects customers who need hair, nail, or makeup services with professional nail technicians, barbers, and makeup artists. Customers request a time, providers confirm or decline, and customers pay the provider in person.

**Glossary**
- **Customer:** A person booking beauty or grooming services.
- **Provider:** A nail technician, barber, or makeup artist offering services.
- **SysAdmin:** The user who approves providers and moderates services and reviews.
- **Time block:** Morning (9 AM–12 PM), Afternoon (12–5 PM), or Evening (5–9 PM).
- **Completed appointment:** A confirmed appointment whose end time has passed. The provider can also mark it completed after it ends, but reviews and statistics never wait for that.

**Scope**
- Customer and provider profiles
- Browse, filter, and sort services
- Appointment requests, confirmation, and simple rescheduling
- Reviews, provider replies, and reporting
- Email notifications

**Out of scope**
- Payments, refunds, payouts, and price filtering or sorting
- SMS, phone verification, and in-app notifications
- Rescheduling beyond one request for a new time

---
## 2. User Stories

### 2.1 Customer

- **US‑1 — Manage customer profile**  
  _Story:_ As a customer, I want to manage my contact info, private safety notes, and saved providers.
  ```gherkin
  Scenario: Register and verify email
    Given I am not registered
    When I sign up with my full name, email, and password
    Then a single-use verification link is emailed to me
    And I cannot log in until I verify my email
    And changing my email later requires verifying the new address

  Scenario: Keep safety notes private
    When I save allergy or sensitivity notes
    Then they never appear on public pages or in search results
    And only the provider of my confirmed appointment can see them
    And "no known allergies" is stored differently from "not provided"

  Scenario: Save providers
    When I save or unsave a provider
    Then my saved list updates, separate from providers I have booked
  ```

- **US‑2 — Browse and filter services**  
  _Story:_ As a customer, I want to filter and sort services so I can find a provider who fits my needs and schedule.
  ```gherkin
  Scenario: Browse services
    When I filter by category, keyword, city, day and time block, minimum rating, or saved providers
    Then I see only approved services from approved providers that match every filter
    And I can sort by top rated, most reviewed, or newest
    And ratings count only visible reviews

  Scenario: Read a provider's reviews
    When I open a provider's reviews
    Then I see the average rating, review count, and number of reviews for each star rating
    And I can filter by star rating and sort by newest, highest, or lowest rating
    And hidden reviews are not shown
  ```

- **US‑3 — Request and reschedule appointments**  
  _Story:_ As a customer, I want to request a time for a service and track or change my booking.
  ```gherkin
  Scenario: Request an appointment
    When I choose a date and a time inside one of the provider's available time blocks
    Then the request is saved as Requested and appears in my history
    And I receive an email when the provider confirms or declines it

  Scenario: Reschedule a confirmed appointment
    When I request a new available time
    Then my original appointment stays confirmed until the provider responds
    And if accepted, the original becomes Rescheduled and the new one Confirmed
    And if declined, the original is unchanged
  ```

- **US‑4 — Review and report**  
  _Story:_ As a customer, I want to review my appointments and report inappropriate content.
  ```gherkin
  Scenario: Write a review
    Given my appointment was completed within the last 30 days
    And I have not reviewed it yet
    When I submit a 1–5 rating and a comment
    Then the review is published immediately
    And I can edit it until the provider replies

  Scenario: Report a review or reply
    When I report someone else's review or reply with a reason
    Then an admin is notified and the content stays visible until decided
    And I cannot report my own content or the same item twice while my report is open
  ```

### 2.2 Provider

- **US-5 — Manage provider profile** \
  *Story:* As a provider, I want a public professional profile so customers can find and evaluate me.
  ```gherkin
  Scenario: Register as a provider
    When I sign up with my account details, professional details, and available days and time blocks
    Then my account is Pending approval after I verify my email
    And I can edit my profile and availability, but cannot submit services or receive bookings until approved

  Scenario: Update my profile
    When I update my bio, address, or certifications
    Then the changes are saved and shown publicly
    And certifications are labeled self-reported
    And my email and phone never appear on public pages
  ```

- **US-6 — Manage services** \
  *Story:* As a provider, I want to submit services for admin review so they can be published.
  ```gherkin
  Scenario: Submit or revise a service
    When I submit a name, category, description, image, duration, and price
    Then the service is Pending review and hidden from customers
    And if an admin requests changes, I can edit and resubmit it

  Scenario: Edit an approved service
    When I change an approved service
    Then the changes go to Pending review
    And customers keep seeing the approved version until the changes are approved
  ```

- **US-7 — Manage availability and appointments** \
  *Story:* As a provider, I want to set my availability and handle booking requests.
  ```gherkin
  Scenario: Set availability
    When I choose my available time blocks for each day
    Then customers can only request times inside those blocks
    And changing availability never cancels confirmed appointments

  Scenario: Respond to a request or reschedule
    When I confirm, decline, or accept a new time for a request
    Then the change is saved with a timestamp and the customer is emailed
    And confirming prevents double-booking that time
    And declining requires a reason

  Scenario: View appointment details
    Given I am the provider of a confirmed appointment
    Then I can see that customer's contact info and safety notes
    And no other provider can see them
  ```

- **US-8 — Reply to reviews** \
  *Story:* As a provider, I want to reply to reviews of my services.
  ```gherkin
  Scenario: Reply to a review
    When I reply to a review of my appointment
    Then the reply appears under the review and the customer is emailed
    And I can reply only once and cannot edit the reply
    And if an admin hides my reply, I cannot reply to that review again

  Scenario: Report a review
    When I report a review of my service with a reason
    Then an admin is notified and the review stays visible until decided
  ```

### 2.3 SysAdmin

**US-9 — Manage user access** \
*Story:* As a SysAdmin, I want to approve, suspend, and reinstate accounts to keep the platform safe.
  ```gherkin
  Scenario: Change an account's access
    When I approve a pending provider, or suspend or reinstate any account, with a reason
    Then the change takes effect immediately
    And the decision is recorded in the admin audit log
  ```

**US-10 — Moderate services** \
*Story:* As a SysAdmin, I want to review new and edited services before customers see them.
  ```gherkin
  Scenario: Review a service
    When I check the name, category, description, image, and compliance with GlowUp's terms
    Then I approve it, request changes, or reject it
    And changes-requested and rejected decisions require a reason shown to the provider
    And the decision is recorded in the admin audit log
  ```

**US-11 — Moderate reported content** \
*Story:* As a SysAdmin, I want to handle reports on reviews and replies.
  ```gherkin
  Scenario: Resolve a report
    When I keep the reported content visible or hide it, with a reason
    Then every open report on that review or reply is resolved
    And hidden reviews stop counting toward the provider's rating
    And the author is emailed if their content is hidden

  Scenario: Restore hidden content
    When I restore content that was hidden by mistake
    Then it becomes visible again and the restore is logged
  ```
A low rating or respectful criticism is not a reason to hide a review. Reportable reasons: harassment, threats, hate or explicit content, personal information, spam, off-topic.

**US-12 — View usage statistics** \
*Story:* As a SysAdmin, I want platform activity numbers for a chosen period.
  ```gherkin
  Scenario: View statistics
    When I select a reporting period
    Then I see completed appointments, new users, active providers, and services submitted
    And no financial figures are shown
  ```

### 2.4 Account settings (customers and providers)

- **US-13 — Password security**
  - Changing a password requires the current password.
  - Only a password hash is stored; passwords are never shown or emailed.
  - Forgot password: email a single-use reset link that expires in 30 minutes, and show the same message whether or not the email exists.

- **US-14 — Close an account**
  - Requires re-entering the password and confirming.
  - The account is deactivated immediately; upcoming appointments are cancelled and the other party is emailed.
  - Logging in within 14 days reactivates it.
  - After 14 days, personal details are removed and the name shows as "Deleted user"; appointment and review history is kept.

**Notifications:** All notifications are email. Verification and password-reset emails are always sent.

---