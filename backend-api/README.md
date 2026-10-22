# GlowUp backend API

# UML Class Diagram

![Class Diagram]()

# API Overview

**Version:** 1.0
**Last Updated:** October 20, 2026

**Base URL:**

- local: http://localhost:8080/api
- production:

JSON request bodies are validated. The endpoint tables list applicable error
code: `400 Bad Request` for invalid input, `404 Not Found` for missing
resources, and `409 Conflict` for duplicate records or invalid state
transitions.

## Profile and discovery

| Method | Path                            | Purpose                                                    | Response code | Error codes | User story                                                              |
| ------ | ------------------------------- | ---------------------------------------------------------- | ------------- | ----------- | ----------------------------------------------------------------------- |
| `POST` | `/api/providers/register`                | Register a provider profile with starting opening hours    | `201 Created` | `400, 409`  | [US-5](../docs/GlowUpSRS.md#L115) |
| `GET`  | `/api/providers/{providerId}`   | Read a provider's public profile (ACTIVE providers only)   | `200 OK`      | `404`       | [US-5](../docs/GlowUpSRS.md#L115) |
| `GET`  | `/api/providers/{providerId}/profile` | Provider reads their own profile in any status       | `200 OK`      | `404`       | [US-5](../docs/GlowUpSRS.md#L115) |
| `PUT`  | `/api/providers/{providerId}`   | Update a provider profile                                  | `200 OK`      | `400, 404, 409` | [US-5](../docs/GlowUpSRS.md#L115) |


## Example Customer Request

### Example 
Passwords are accepted only by registration endpoints, hashed with BCrypt, and
never included in API responses. Authentication and authorization are not yet
implemented; IDs in paths are not proof of identity and these endpoints must not
be exposed to untrusted clients until access control is added.

## Services and availability

| Method   | Path                                                     | Purpose                                                             | Response code    | Error codes | User story                          |
| -------- | -------------------------------------------------------- | ------------------------------------------------------------------- | ---------------- | ----------- | ----------------------------------- |
| `GET` | `/api/providers/{providerId}/services`                | List that provider's approved and visible services                               | `200 OK`  | `404`  | [US-6](../docs/GlowUpSRS.md#L130) |
| `GET` | `/api/providers/{providerId}/all-services`                | List all of that provider's services in every status (My services page)        | `200 OK` | `404`  | [US-6](../docs/GlowUpSRS.md#L130) |
| `POST` | `/api/providers/{providerId}/services`                | Create a service; it is PENDING and hidden until an admin approves it            | `201 Created` | `400, 403, 404`  | [US-6](../docs/GlowUpSRS.md#L130) |
| `PUT`  | `/api/providers/{providerId}/services/{serviceId}`   | Edit a service; an approved one gets a draft and the old version stays visible  | `200 OK`      | `400, 403, 404, 409` | [US-6](../docs/GlowUpSRS.md#L138) |
| `PATCH` | `/api/providers/{providerId}/services/{serviceId}/archive` | Stop offering a service (or withdraw a submission); open requests are declined | `200 OK` | `404, 409` | [US-6](../docs/GlowUpSRS.md#L143) |
| `GET` | `/api/providers/{providerId}/schedules?availableOnly=false` | List the weekly opening hours                                         | `200 OK` | `404` | [US-7](../docs/GlowUpSRS.md#L152) |
| `PUT` | `/api/providers/{providerId}/schedules`                | Update the weekly opening hours (one or more weekdays)                          | `200 OK` | `400, 404` | [US-7](../docs/GlowUpSRS.md#L152) |

### Example Service Request

```http
POST /api/providers/{providerId}/services
Content-Type: application/json
NOTE: Provider account's status has to be ACTIVE to create or update a service (otherwise 403)

{
	"name": "Eyebrows waxing",
	"description": "Eyebrow shaping and waxing using professional wax and soothing post-wax lotion.",
	"price": 15,
	"category": "BROWS_AND_LASHES",
	"durationMin": 10,
    "imagePath":"../images/design.jpg"
}
```

### Example Provider Update Schedule Request
Send the weekdays to change, each at most once; days not sent stay as they are. Times are in 15-minute steps.
A closed day may leave the times out, and then the saved times are kept for when it reopens.
```http
PUT /api/providers/{providerId}/schedules
Content-Type: application/json

{
  "providerDailySchedule": [
    {
      "weekday": "MONDAY",
      "isAvailable": true,
      "openTime": "09:00:00",
      "closeTime": "17:00:00"
    },
    {
      "weekday": "TUESDAY",
      "isAvailable": true,
      "openTime": "09:00:00",
      "closeTime": "17:00:00"
    },
    {
      "weekday": "WEDNESDAY",
      "isAvailable": true,
      "openTime": "09:00:00",
      "closeTime": "17:00:00"
    },
    {
      "weekday": "THURSDAY",
      "isAvailable": true,
      "openTime": "09:00:00",
      "closeTime": "17:00:00"
    },
    {
      "weekday": "FRIDAY",
      "isAvailable": true,
      "openTime": "09:00:00",
      "closeTime": "19:30:00"
    },
    {
      "weekday": "SATURDAY",
      "isAvailable": true,
      "openTime": "10:00:00",
      "closeTime": "15:00:00"
    },
    {
      "weekday": "SUNDAY",
      "isAvailable": false,
      "openTime": null,
      "closeTime": null
    }
  ]
}
```

## Appointments and reviews

| Method  | Path                                                          | Purpose                                               | Response code | Error codes | User story                                                              |
| ------- | ------------------------------------------------------------- | ----------------------------------------------------- | ------------- | ----------- | ----------------------------------------------------------------------- |
| `GET` | `/api/providers/{providerId}/appointments`                | List the provider's appointments                               | `200 OK` | `404`  | [US-7](../docs/GlowUpSRS.md#L158) |
| `GET` | `/api/providers/{providerId}/appointments/{appointmentId}`                | View an appointment; contact info and safety notes only when confirmed | `200 OK` | `404`  | [US-7](../docs/GlowUpSRS.md#L165) |
| `PATCH` | `/api/providers/{providerId}/appointments/{appointmentId}/confirm`                | Confirm a REQUESTED appointment (or accept a new time)          | `200 OK` | `400, 404, 409`  | [US-7](../docs/GlowUpSRS.md#L158) |
| `PATCH` | `/api/providers/{providerId}/appointments/{appointmentId}/decline`                | Decline a request, with a reason                                | `200 OK` | `400, 404, 409`  | [US-7](../docs/GlowUpSRS.md#L158) |
| `PATCH` | `/api/providers/{providerId}/appointments/{appointmentId}/cancel`                | Cancel a confirmed appointment before it starts, with a reason  | `200 OK` | `400, 404, 409`  | [US-7](../docs/GlowUpSRS.md#L158) |
| `PATCH` | `/api/providers/{providerId}/appointments/{appointmentId}/complete`                | Mark a confirmed appointment completed after it ends            | `200 OK` | `404, 409`  | [US-7](../docs/GlowUpSRS.md#L158) |

### Example Appointment Request

## Admin

Every admin request sends the admin's account id in the `X-Admin-Id` header until login is added (`403` otherwise).
Every decision needs a reason and is written to the admin audit log.

| Method | Path | Purpose | Response code | Error codes | User story |
| ------ | ---- | ------- | ------------- | ----------- | ---------- |
| `POST` | `/api/admin/users/{userId}/suspend` | Suspend an account; its upcoming requested and confirmed appointments are cancelled | `200 OK` | `400, 403, 404, 409` | [US-9](../docs/GlowUpSRS.md#L187) |
| `POST` | `/api/admin/services/{serviceId}/decision` | Approve, request changes, or reject a PENDING service | `200 OK` | `400, 403, 404, 409` | [US-10](../docs/GlowUpSRS.md#L201) |
| `POST` | `/api/admin/services/{serviceId}/archive` | Take down a live (approved) service; the reason is shown to the provider | `200 OK` | `400, 403, 404, 409` | [US-10](../docs/GlowUpSRS.md#L210) |

```http
POST /api/admin/services/{serviceId}/archive
X-Admin-Id: 1
Content-Type: application/json

{ "reason": "The photos are not of the provider's own work." }
```
