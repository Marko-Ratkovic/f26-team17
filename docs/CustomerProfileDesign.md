# Customer Profile and Privacy Design

This is a proposed design note for the future Spring Boot implementation. The current HTML prototype is static: it does not persist edits, authenticate requests, or enforce the access rules below. The lifecycle rules below are recommended MVP defaults; the team must approve the retention schedule and service target before launch.

## Data ownership

| Concept | Proposed data | Source of truth |
|---|---|---|
| `UserAccount` | User ID, normalized email, email verification state, password hash, role, account status, timestamps | Authentication/account service |
| `CustomerProfile` | User ID, first/middle/last name, optional phone, phone verification state | Customer profile |
| `CustomerSafetyNotes` | Customer ID, allergy status (`KNOWN`, `NO_KNOWN`, `NOT_PROVIDED`), optional allergy and sensitivity details, update timestamp | Private customer record |
| `CustomerPreference` | Customer ID, preferred service categories, preferred days and time ranges | Customer preference settings |
| `SavedProvider` | Customer ID, provider ID, saved timestamp; unique customer/provider pair | Explicit customer save action |
| `NotificationPreference` | Customer ID, notification event, channel, enabled state | Notification settings |
| `AccountRequest` | Request ID, customer ID, request type, status, created/confirmed/processed timestamps | Account lifecycle workflow |
| `Appointment` | Customer ID, provider ID, service ID, status, scheduled time and booking details | Booking service; use for history and counts |

Store authentication credentials only as a one-way password hash in `UserAccount`; never put a raw password in the profile record, logs, analytics, or response models. Email changes should remain pending until the new address is verified. Permit SMS notification preferences only for verified phone numbers. Essential security messages are distinct from optional reminders and marketing.

Use normalized category/day/channel rows or constrained values rather than arbitrary free-text preferences where practical. Allergy and sensitivity details can remain optional text for the course prototype, paired with an explicit allergy-status value. Do not store a denormalized `activeProviderCount` or appointment totals: derive them from appointments. Do not treat an appointment-derived provider as a saved provider; these represent different customer intent.

Keep allergy status and details consistent: `KNOWN` requires at least one allergy detail; `NO_KNOWN` and `NOT_PROVIDED` must not retain allergy details. Sensitivities remain independently optional. If a customer changes from `KNOWN` to either other status, clear the old allergy details in the same transaction.

## Access rules

- A customer may view and update only their own profile, safety notes, preferences, notification settings, saved-provider relationships, and account requests.
- Public profile and service-search responses must not include customer safety notes or private contact information.
- A provider may read safety notes only from the provider's own confirmed appointment context. The backend must verify the authenticated provider, appointment ownership, and allowed appointment status on every request. A customer-info page or hidden form field is not an authorization boundary.
- Providers should receive only the customer contact details needed to fulfill an associated appointment. Ordinary provider browsing must not expose customer profiles.
- Administrators should not see safety-note contents for routine moderation. Any exceptional support access needs a documented purpose, least privilege, and an audit trail.
- Keep audit entries for account requests and moderation/access decisions, but never log passwords or copy allergy-note contents into audit records.

## Account lifecycle and retained history

Model temporary deactivation and deletion requests as explicit states, not as a destructive delete button. Re-authenticate and confirm sensitive requests, record request status, explain how existing confirmed appointments will be handled, and prevent ambiguous partial processing. Acknowledge each request immediately and provide a status view. A proposed service target is to complete or explain a blocked request within 30 calendar days; make the target configurable and confirm it against applicable legal requirements before promising it publicly.

Do not cascade-delete appointment, review, report, or moderation history when a user requests deletion. Do not invent a single blanket retention period: before launch, approve a record-class retention schedule that states each purpose, retention trigger, access, and deletion/de-identification action. Once a request is approved and outstanding appointments are resolved, remove or de-identify optional profile, preference, and safety-note data unless a documented legal or operational need requires retention. Keep only the minimum identity and audit metadata needed to honor the request and preserve records under that schedule; do not retain sensitive safety-note contents in account-request history.

## Spring Boot implementation boundary

Use authenticated server-rendered forms or controller endpoints for profile edits, password changes, preference updates, and account requests. Validate permissions and submitted values on the server, then return a clear success or validation-error page. The prototype's disabled buttons are intentional: static HTML cannot save state or provide secure account actions by itself.
