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
| `POST` | `/api/providers`                | Register a provider profile                                | `201 Created` | `400, 409`  | [US-5](../docs/GlowUpSRS.md#L106)                                      |
| `PUT`  | `/api/providers/{providerId}`   | Update a provider profile                                  | `200 OK`      | `400, 404, 409` | [US-5](../docs/GlowUpSRS.md#L106)                                   |

## Example Customer Request

### Example 
Passwords are accepted only by registration endpoints, hashed with BCrypt, and
never included in API responses. Authentication and authorization are not yet
implemented; IDs in paths are not proof of identity and these endpoints must not
be exposed to untrusted clients until access control is added.

## Services and availability

| Method   | Path                                                     | Purpose                                                             | Response code    | Error codes | User story                          |
| -------- | -------------------------------------------------------- | ------------------------------------------------------------------- | ---------------- | ----------- | ----------------------------------- |

### Example Service Request

### Example Provider Schedule Request

## Appointments and reviews

| Method  | Path                                                          | Purpose                                               | Response code | Error codes | User story                                                              |
| ------- | ------------------------------------------------------------- | ----------------------------------------------------- | ------------- | ----------- | ----------------------------------------------------------------------- |

### Example Appointment Request

