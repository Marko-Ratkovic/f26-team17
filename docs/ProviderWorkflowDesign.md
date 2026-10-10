# Provider Workflow Design

This note describes a proposed future Spring Boot implementation. Provider screens currently contain static examples only: they do not save profile or schedule changes, process bookings or payments, verify credentials, send notifications, or enforce authorization. Their fictional sample data is illustrative per screen and is not a synchronized fixture or shared source of truth. When the backend is implemented, replace these examples with consistent records loaded from the server. The MVP rules below are recommendations, not launch policy; confirm provider verification criteria and local requirements before enabling bookings.

## Domain boundaries

| Concept | Proposed data | Ownership |
|---|---|---|
| `UserAccount` | ID, normalized email, password hash, role, account status, verification state | Authentication/account service |
| `ProviderProfile` | Provider ID, public name, biography, service area, credential claims, profile status | Provider profile |
| `ProviderContact` | Provider ID, private email/phone, verification state | Private account contact |
| `ServiceListing` | Provider ID, current approved version ID, listing status | Service catalog |
| `ServiceListingVersion` | Listing ID, name, category, description, image reference, duration, price, review decision/reason and timestamps | Moderation history |
| `WeeklyAvailability` | Provider ID, day, start/end time, time zone, effective range | Scheduling |
| `Appointment` | Customer, provider, service version, slot, price snapshot, status and transition timestamps | Booking |
| `AppointmentStatusHistory` | Appointment ID, previous/new status, actor, reason and timestamp | Booking audit |
| `NotificationPreference` | Provider ID, event, channel and enabled state | Provider settings |

Keep private login/contact data separate from fields returned by customer search. Store images using controlled references rather than accepting arbitrary file paths. Treat credentials as claims until a defined verification workflow marks them verified. Money should use a decimal/currency representation in the database and preserve the agreed price on the appointment rather than recalculating old bookings from a later service price.

## Service review lifecycle

- New submissions begin as `PENDING`; they are not discoverable or bookable.
- Approval publishes the reviewed version.
- A requested-change version remains hidden until it is resubmitted and approved.
- Editing an approved service creates a new pending version. The previous approved version remains visible and bookable until the new version is approved or explicitly withdrawn.
- Record moderation actor, decision, reason, and timestamps. Do not overwrite the approved version or lose prior decisions.
- Validate image type and size, category, name, duration, and price on the server. Use an explicit archive/deactivation transition rather than deleting listings referenced by bookings.

## Appointment and schedule behavior

Suggested appointment states are `REQUESTED`, `CONFIRMED`, `DECLINED`, `CANCELLED`, and `COMPLETED`. Customers submit a request for an available slot; the request is not a confirmed booking until the provider confirms it. The provider may decline a request. Recommended MVP cancellation behavior: either party may cancel a requested appointment; after confirmation, either party may cancel before the appointment start, with the provider supplying a reason and the customer receiving notice. Do not charge cancellation fees or imply a payment consequence. After the appointment has started, do not allow self-service cancellation; route corrections to support. Cancellation releases the slot for new requests but never silently reschedules another appointment. Require team approval of these rules before backend implementation. Keep an append-only status history with the acting user and reason when applicable.

Confirmation must run transactionally: verify the request is still actionable, the provider and approved service are active, and the slot is still available; reserve it with a database constraint or locking strategy; then commit the status transition. Send notifications only after the state change commits, using retry-safe delivery so notification failures do not fabricate or roll back booking state.

Model weekly availability with an IANA time-zone identifier and effective dates. Store confirmed appointment instants in UTC and retain the provider's time-zone identifier and selected local offset for display and audit. Show availability in the provider's local time zone and clearly label that zone to customers. Reject invalid ranges and overlapping provider availability. Treat holidays and one-off closures as date-specific exceptions that take precedence over weekly hours. Availability governs new requests; changing it must not silently cancel or move confirmed bookings. During daylight-saving transitions, reject nonexistent local times and require an explicit offset choice when a local time occurs twice. These rules avoid ambiguous bookings; confirm any product-specific buffer or travel-time needs before launch.

## Provider verification recommendation

Treat submitted licenses, certifications, and experience as unverified claims by default. Use explicit `UNVERIFIED`, `PENDING`, `VERIFIED`, `REJECTED`, and `EXPIRED` states; show a verified badge only for an actively verified claim, and record reviewer, evidence reference, decision, expiry, and reason. Permit profile drafting while unverified, but do not let an unverified provider accept appointment requests until the team has approved verification criteria for the service category and operating jurisdiction. GlowUp must not imply that a self-reported credential has been checked or that platform review replaces government licensing requirements. The evidence and renewal policy require team approval before launch.

Payment processing is out of scope for this project. A request or confirmation must not collect, authorize, or imply a charge. Any future payment feature needs separately approved rules for payment timing, cancellations, refunds, disputes, taxes, and provider payouts.

## Access rules

- Providers may update only their own profile, services, availability, and notification preferences.
- Customers see only approved active profile and service versions.
- Only the assigned provider and the customer may access private appointment details, subject to the appointment's authorized status.
- Private customer safety notes must be loaded only within an authorized appointment context; do not include them in general appointment lists, search responses, logs, or analytics.
- Never treat a hidden link, form field, or static prototype page as an authorization boundary. Check identity, ownership, role, and state in every backend request.
- Keep an audit trail for service moderation and appointment status changes while limiting retained personal information to the approved retention policy.

## Server-rendered implementation direction

Spring MVC controllers can render these views and receive standard form submissions without frontend JavaScript. Use CSRF protection, server-side Bean Validation, authorization checks, Post/Redirect/Get after successful actions, and explicit validation feedback. Disable or omit actions until their backend handler exists; never render a success state for an unpersisted change.
