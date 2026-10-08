# Provider Workflow Design

This note describes a proposed future Spring Boot implementation. Provider screens currently contain static examples only: they do not save profile or schedule changes, process bookings, verify credentials, send notifications, or enforce authorization. Confirm appointment cancellation, provider verification, and record-retention policies before launch.

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

Suggested appointment states are `REQUESTED`, `CONFIRMED`, `DECLINED`, `CANCELLED`, and `COMPLETED`. Define cancellation windows and who may transition a confirmed booking before implementation. Keep an append-only status history with the acting user and reason when applicable.

Confirmation must run transactionally: verify the request is still actionable, the provider and approved service are active, and the slot is still available; reserve it with a database constraint or locking strategy; then commit the status transition. Send notifications only after the state change commits, using retry-safe delivery so notification failures do not fabricate or roll back booking state.

Model weekly availability with an explicit time zone and effective dates. Reject invalid ranges and overlapping provider availability. Availability governs new requests; changing it must not silently cancel or move confirmed bookings. Define how holidays, buffers, travel time, and daylight-saving transitions work before launch.

## Access rules

- Providers may update only their own profile, services, availability, and notification preferences.
- Customers see only approved active profile and service versions.
- Only the assigned provider and the customer may access private appointment details, subject to the appointment's authorized status.
- Private customer safety notes must be loaded only within an authorized appointment context; do not include them in general appointment lists, search responses, logs, or analytics.
- Never treat a hidden link, form field, or static prototype page as an authorization boundary. Check identity, ownership, role, and state in every backend request.
- Keep an audit trail for service moderation and appointment status changes while limiting retained personal information to the approved retention policy.

## Server-rendered implementation direction

Spring MVC controllers can render these views and receive standard form submissions without frontend JavaScript. Use CSRF protection, server-side Bean Validation, authorization checks, Post/Redirect/Get after successful actions, and explicit validation feedback. Disable or omit actions until their backend handler exists; never render a success state for an unpersisted change.
