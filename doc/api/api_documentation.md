# REST API Documentation

## Table of Contents
- [REST API Documentation](#rest-api-documentation)
  - [Table of Contents](#table-of-contents)
  - [API Conventions](#api-conventions)
  - [Authentication](#authentication)
  - [User Management](#user-management)
  - [Contract Management](#contract-management)
  - [Offer Management](#offer-management)
  - [Ratings and Feedback](#ratings-and-feedback)
  - [Payment Management](#payment-management)
  - [Notifications and Map Features](#notifications-and-map-features)
  - [Response Formats](#response-formats)
    - [Success Response Format](#success-response-format)
    - [Error Response Format](#error-response-format)
  - [Error Codes](#error-codes)

## API Conventions
- All endpoints require HTTPS
- API version is included in the URL path: `/api/v1/...`
- Authentication uses JWT tokens in the `Authorization` header
- Pagination is supported via `page` and `pageSize` query parameters
- Sorting is supported via `sort` parameter (e.g., `sort=createdAt:desc`)
- Date format: ISO 8601 (YYYY-MM-DDTHH:MM:SSZ)
- All times are in UTC

## Authentication

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | Yes ✅ | `/api/v1/auth/register/driver` | POST | `driverToRegister <User>` | Body | 201, 400, 409 | `createdDriver <User>` | Register a new driver account | S1 | 
| No ❌ | Yes ✅ | `/api/v1/auth/register/requester` | POST | `requesterToRegister <User>` | Body | 201, 400, 409 | `createdRequester <User>` | Register a new requester account | S1 | 
| No ❌ | No ❌ | `/api/v1/auth/login` | POST | `username <string>`, `password <string>` | Body | 200, 401 | `{ "token": "jwt-token", "user": {...} }` | Authenticate user and create session | S2 | 
| No ❌ | No ❌ | `/api/v1/auth/logout` | POST | Auth token | Header | 200, 401 | `{ "message": "Successfully logged out" }` | End user session | S2 |
| No ❌ | No ❌ | `/api/v1/auth/refresh` | POST | Refresh token | Body | 200, 401 | `{ "token": "new-jwt-token" }` | Refresh authentication token | S2 |

## User Management

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | No ❌ | `/api/v1/users/{id}` | GET | `id <string>` | Path | 200, 404 | User object with profile details | Get user details | S3, S10 | 
| No ❌ | No ❌ | `/api/v1/users/{id}` | PUT | `id <string>`, profile fields | Path, Body | 200, 400, 403 | Updated user object | Update user profile | S3, S10 | 
| No ❌ | No ❌ | `/api/v1/users/{id}` | DELETE | `id <string>` | Path | 204, 403 | None | Delete user account | S3, S10 | 
| No ❌ | No ❌ | `/api/v1/users/requesters/{id}` | GET | `id <string>` | Path | 200, 404 | Requester profile details | Get requester-specific profile | S3 | 
| No ❌ | No ❌ | `/api/v1/users/requesters/{id}` | PUT | `id <string>`, requester-specific fields | Path, Body | 200, 400, 403 | Updated requester profile | Update requester profile | S3 | 
| No ❌ | No ❌ | `/api/v1/users/drivers/{id}` | GET | `id <string>` | Path | 200, 404 | Driver profile with vehicle details | Get driver-specific profile | S10 | 
| No ❌ | No ❌ | `/api/v1/users/drivers/{id}` | PUT | `id <string>`, driver-specific fields | Path, Body | 200, 400, 403 | Updated driver profile | Update driver profile | S10 |
| No ❌ | No ❌ | `/api/v1/users/drivers/{id}/vehicle` | PUT | `id <string>`, `model <string>`, `volume <number>`, `isElectric <boolean>`, `image <file>` | Path, Body | 200, 400, 403 | Updated vehicle details | Update vehicle information | S10, S16 | 
| No ❌ | No ❌ | `/api/v1/users/drivers/{id}/insurance` | POST | `id <string>`, `insuranceDocument <file>`, `expiryDate <date>` | Path, Body | 201, 400 | Insurance upload confirmation | Upload insurance policy | S15 | 

## Contract Management

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | Yes ✅ | `/api/v1/contracts` | POST | `newContract <Contract>` | Body | 201, 400, 404 | Created contract object | Create a new contract | S5 | 
| No ❌ | No ❌ | `/api/v1/contracts` | GET | `status <string>`, `fromLocation <string>`, `toLocation <string>`, `radius <number>`, `minPrice <number>`, `maxPrice <number>`, `minDate <date>`, `maxDate <date>`, `page <int>`, `pageSize <int>`, `sort <string>` | Query | 200 | List of Contracts | Get available contracts with filtering | S11 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}` | GET | `id <string>` | Path | 200, 404 | Contract details object | Get contract details | S7, S12 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}` | PUT | `id <string>, contractToUpdate <Contract>` | Path, Body | 200, 400, 403 | Updated contract object | Update a contract | S6 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}/cancel` | PUT | `id <string>`, `reason <string>` | Path, Body | 200, 400, 403, 409 | Updated contract with cancel status | Cancel a contract (72h policy) | S8 | 
| No ❌ | No ❌ | `/api/v1/contracts/{id}/fulfill` | PUT | `id <string>` | Path | 200, 400, 403 | Updated contract status | Mark contract as fulfilled | S9, S18 | 
| No ❌ | No ❌ | `/api/v1/contracts/{id}/photos` | POST | `id <string>`, `photos <file[]>`, `type <string>` (before/after) | Path, Body | 201, 400, 403 | Photo upload confirmation with URLs | Upload before/after photos | S19 | 
| No ❌ | No ❌ | `/api/v1/contracts/{id}/collateral` | POST | `id <string>`, `collateralAmount <number>` | Path, Body | 200, 400, 403, 409 | Updated contract with collateral | Provide contract collateral | S21 | 
| No ❌ | No ❌ | `/api/v1/users/{userId}/contracts` | GET | `userId <string>`, `status <string>`, `sort <string>` | Path, Query | 200 | Paginated list of contracts for a specific user| Get user's contracts | S12 | 

## Offer Management

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | No ❌ | `/api/v1/contracts/{id}/offers/{offerId}` | GET | `id <string>, offerId <string>` | Path | 200, 404 | Detailed offer object | Get a specific offer | S4, S11 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}/offers/{offerId}` | DELETE | `id <string>, offerId <string>` | Path | 204, 403, 409 | None | Delete an offer | S6 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}/offers` | GET | `id <string>`, | Path, Query | 200, 404 | List of driver offers | Get all driver offers for a contract | S7 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}/offers` | POST | `id <string>`, `driverId <string>`, `message <string>` (optional) | Path, Body | 201, 400, 409 | Created offer object | Driver makes an offer for a contract | S12 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}/offers` | PUT | `id <string>`, `driverId <string>`, `message <string>` (optional) | Path, Body | 201, 400, 409 | Cancel offer | Driver cancels an offer for a contract | S12 |

## Ratings and Feedback

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | No ❌ | `/api/v1/contracts/{id}/driver-rating` | POST | `id <string>`, `rating <integer>`, `comment <string>`, `issues <boolean>`, `issueDetails <string>` | Path, Body | 200, 400, 403, 409 | Updated contract with rating | Requester rates driver | S9 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}/requester-rating` | POST | `id <string>`, `rating <integer>`, `comment <string>` | Path, Body | 200, 400, 403, 409 | Updated contract with requester rating | Driver rates requester | S18 | 
| No ❌ | No ❌ | `/api/v1/users/{id}/ratings` | GET | `id <string>`, `role <string>` (driver/requester), `page <int>`, `pageSize <int>` | Path, Query | 200, 404 | Paginated list of ratings | Get user's ratings | S9, S18 |

## Payment Management

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | No ❌ | `/api/v1/users/{id}/wallet` | GET | `id <string>` | Path | 200, 404 | `{ "balance": 100.00, "transactions": [...] }` | Get wallet balance and transactions | S14, S20 |
| No ❌ | No ❌ | `/api/v1/users/{id}/wallet/deposit` | POST | `id <string>`, `amount <number>`, `paymentMethod <string>` | Path, Body | 200, 400 | Updated wallet object | Add funds to wallet | S14, S20 |
| No ❌ | No ❌ | `/api/v1/users/{id}/wallet/withdraw` | POST | `id <string>`, `amount <number>`, `bankDetails <object>` | Path, Body | 200, 400 | Updated wallet object | Withdraw funds from wallet | S20 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}/payment` | POST | `id <string>`, `amount <number>` | Path, Body | 200, 400, 403, 409 | Payment confirmation object | Process payment for contract | S14, S20 | 
| No ❌ | No ❌ | `/api/v1/contracts/{id}/refund` | POST | `id <string>`, `amount <number>`, `reason <string>` | Path, Body | 200, 400, 403, 409 | Refund confirmation object | Process refund for contract | S14, S20 |

## Notifications and Map Features

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | No ❌ | `/api/v1/notifications` | GET | Auth token, `page <int>`, `pageSize <int>` | Header, Query | 200 | `{ "total": 10, "unread": 3, "notifications": [...] }` | Get user notifications | S17 |
| No ❌ | No ❌ | `/api/v1/notifications/{id}` | PUT | `id <string>`, `read <boolean>` | Path, Body | 200, 404 | Updated notification object | Mark notification as read | S17 |
| Yes ✅ | No ❌ | `/api/v1/map/contracts` | GET | `lat <number>`, `lng <number>`, `radius <number>`, `filters <object>`{ price <number>, weight <number>, height <number>, length <number>, width <number>, requiredPeople <number>, fragile <boolean>, coolingRequired <boolean>, rideAlong <boolean>, fromAdress <string of Location Obeject>, toAdress <string of Location Object>, moveDateTime <string of LocalDateTime Object OR null> }| Query | 200 | GeoJSON of proposals | Get proposals for map display | S11, S17 |

## Response Formats

### Success Response Format
```json
{
  "status": "success",
  "data": {
    // Response data depending on the endpoint
  },
  "meta": {
    "page": 1,
    "pageSize": 10,
    "totalPages": 5,
    "totalCount": 42
  }
}
```

### Error Response Format
```json
{
  "status": "error",
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Invalid input data",
    "details": [
      {
        "field": "username",
        "message": "Username must be at least 3 characters"
      }
    ]
  }
}
```

## Error Codes

| Status Code | Error Code | Description |
|-------------|------------|-------------|
| 400 | VALIDATION_ERROR | Invalid input data |
| 401 | UNAUTHORIZED | Authentication required or invalid credentials |
| 403 | FORBIDDEN | Insufficient permissions for this action |
| 404 | NOT_FOUND | Resource not found |
| 409 | CONFLICT | Resource conflict (e.g., duplicate username) |
| 422 | BUSINESS_RULE_VIOLATION | Operation violates business rules (e.g., cancellation policy) |
| 429 | RATE_LIMIT_EXCEEDED | Too many requests, try again later |
| 500 | INTERNAL_SERVER_ERROR | Server encountered an error |
