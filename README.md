# GlowUp - Book beauty & grooming pros you can trust.
## Title
> GlowUp

## Team Members
> Marko Ratkovic

> Alisena Nasirali

> Hana Lenh


## Description
GlowUp is a software that connects customers who needs theirs hairs, nails or makeup services with professional nail technicians, barbers, and makeup artists.

## App Functions
1. Customer:
    1. **Create/modify customer profile** - Register and manage a personal
       profile with contact info and beauty/grooming preferences
       (preferred services, stylist, notes like allergies).
    2. **View available services** - Browse nail technicians, barbers, and makeup
       artists by service menu, portfolio, price, and available time
       slots; filter by service type and location.
    3. **Request an appointment** - Choose an approved service and available
       time slot, submit a booking request for provider confirmation, and view
       appointment history. Payment processing is out of scope for this project.
    4. **Write reviews for completed appointments** - Submit one review per
       completed appointment; reviews publish immediately, and users can report
       reviews or provider replies that violate community guidelines.
2. Provider (Nail technicians, barbers, makeup artist):
    1. **Create/modify/remove provider profile** - Register as a provider and showcase certifications, experience, address, and business hours.
    2. **Define services and pricing** - Submit accurate service details, images, pricing, and duration for admin review; new listings appear to customers only after approval.
    3. **Manage customer's booking** -  Manage customer's booking slot including customer's information.
    4. **Reply to reviews** - Respond professionally to customer feedback;
       published replies can also be reported for policy violations.
3. SysAdmin: (the user with the admin role if applicable):
    1. **Manage user access** - Approve, suspend, or reinstate customer and provider accounts, review suspicious profiles, and maintain an audit log of access-related actions.
    2. **Review service listings** - Review new provider listings before publication, approve compliant listings, or request specific changes; reject prohibited listings and record decisions.
    3. **Moderate reported content** - Review reports on customer reviews and
       provider replies, keep compliant content visible, hide policy-violating
       content, and record moderation decisions.
    4. **View usage statistics** - Monitor appointment activity, provider activity, service-review volume, and user growth while tracking admin actions and audit records for reporting and accountability.

## Admin Prototype Notes

The admin pages include sample-data previews of search, status filters, sorting, pagination, bulk-action toolbars, charts, and dedicated user, service, and report detail views. Usage statistics cover appointment activity, user growth, provider activity, and service-review volume only. The admin prototype has no financial analytics. These controls are visual placeholders in the static prototype; future backend work should implement server-side filtering and pagination, persist admin decisions with reasons, and expose only the personal data needed for each admin task. The charts and metrics are fictional and are not connected to live data.

The customer dashboard also uses fixed sample data. Its summary cards link to the related provider or appointment section, and appointment-history navigation uses regular in-page links so it works without JavaScript. Counts and appointment dates will need to come from the backend when that is implemented.

Customer profile, safety-note, preference, security, notification, and account-request screens are also static previews. Save/request controls are disabled until a backend exists. Safety notes are intended to be private and visible to a provider only from that customer's confirmed appointment; the prototype does not enforce authorization. See `docs/CustomerProfileDesign.md` for the proposed data ownership, Spring Boot boundary, access rules, and account-retention considerations.

Provider registration, public-profile editing, service review, weekly availability, appointment history, security, and notification screens are static previews with fictional October 2026 data. These screens are illustrative views, not a single synchronized sample database; repeated-looking names or listings across screens are not guaranteed to represent the same record unless explicitly linked. Backend-only actions are disabled. Service edits are modeled as pending revisions while the last approved listing remains visible; requested changes and new listings remain hidden until approved. Customers request available appointment slots, providers confirm or decline requests, and payment processing is out of scope. Appointment transitions must be authorized and recorded by the backend, and schedule edits must not silently cancel confirmed bookings. Customer contact and private safety details belong only in an authorized appointment context. See `docs/ProviderWorkflowDesign.md` for the proposed Spring Boot domain boundaries, state transitions, and access rules.
