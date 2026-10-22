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
| `POST` | `/api/providers/register`                | Register a provider profile                                | `201 Created` | `400, 409`  | [US-5](../docs/GlowUpSRS.md#L106)                                      |
| `PUT`  | `/api/providers/{providerId}`   | Update a provider profile                                  | `200 OK`      | `400, 404, 409` | [US-5](../docs/GlowUpSRS.md#L106)                                   |  
| `GET`  | `/api/providers/{providerId}`   | Read a provider profile                                  | `200 OK`      | `400` | [US-5](../docs/GlowUpSRS.md#L106)                                   |  


## Example Customer Request

### Example 
Passwords are accepted only by registration endpoints, hashed with BCrypt, and
never included in API responses. Authentication and authorization are not yet
implemented; IDs in paths are not proof of identity and these endpoints must not
be exposed to untrusted clients until access control is added.

## Services and availability

| Method   | Path                                                     | Purpose                                                             | Response code    | Error codes | User story                          |
| -------- | -------------------------------------------------------- | ------------------------------------------------------------------- | ---------------- | ----------- | ----------------------------------- |
| `GET` | `/api/providers/{providerId}/services`                | List that provider's approved and visible services                               | `200 OK`  | `400, 404, 409`  | [US-6](../docs/GlowUpSRS.md#L121)
| `GET` | `/api/providers/{providerId}/all-services`                | List that provider's all services regardless service's status                               | `200 OK` | `400, 409`  | [US-6](../docs/GlowUpSRS.md#L121)
| `POST` | `/api/providers/{providerId}/services`                | Provider create a service and submit for admin review, PENDING status by default                               | `201 Created` | `400, 404, 409`  | [US-6](../docs/GlowUpSRS.md#L121)  
| `PUT`  | `/api/providers/{providerId}/services/{serviceId}`   | Provider update a service and submit for admin review, old version of service keep visible                                 | `200 OK`      | `400, 404, 409` | [US-6](../docs/GlowUpSRS.md#L129)    
| `PUT` | `/api/providers/{providerId}/schedules`                | Provider update weekly schedule 	                              | `201 Created` | `400, 409`  | [US-7](../docs/GlowUpSRS.md#L136)

### Example Service Request

```http
POST /api/providers/{providerId}/services
Content-Type: application/json
NOTE: Provider account's status has to be ACTIVE to create or update service
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
```http
POST /api/providers/{providerId}/schedules
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
| `GET` | `/api/providers/{providerId}/appointments/{appointmentId}`                | Provider view CONFIRMED appointment detail                               | `200 OK` | `400, 404, 409`  | [US-7](../docs/GlowUpSRS.md#L149) 
| `PAT` | `/api/providers//{providerId}/appointments/{appointmentId}/confirm`                | Provider confirm a REQUESTED appointment                               | `200 OK` | `400, 404, 409`  | [US-7](../docs/GlowUpSRS.md#L143) 

### Example Appointment Request

