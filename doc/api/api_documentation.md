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
- Authentication requires both UserId and Authorization headers
- Date format: ISO 8601 (YYYY-MM-DDTHH:MM:SSZ)
- All times are in UTC

## Authentication

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | Yes ✅ | `/api/v1/auth/register` | POST | `user <BaseUserRegisterDTO>`, `car <CarDTO>` (optional), `location <LocationDTO>` (optional) | Body | 201, 400, 409 | `{ "token": "uuid-token", "userId": 1, "userAccountType": "REQUESTER", ... }` | Register a new user account | S1 | 

| No ❌ | Yes ✅ | `/api/v1/auth/login` | POST | `username <string>`, `password <string>` | Body | 200, 401 | `{ "token": "uuid-token", "userId": 1, "userAccountType": "REQUESTER", ... }` | Authenticate user and create session | S2 | 
| No ❌ | Yes ✅ | `/api/v1/auth/logout` | POST | `UserId <string>`, `Authorization <string>` | Header | 200, 401 | `{ "message": "Successfully logged out", "timestamp": 1234567890 }` | End user session | S2 |

### Authentication Headers
All authenticated endpoints require the following headers:
- `UserId`: The ID of the authenticated user
- `Authorization`: The authentication token received during login/registration

### Example Authentication Flow
1. Register a new user:
```bash
curl -X POST "http://localhost:8080/api/v1/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"user": {"username": "testuser", "password": "testpass", "email": "test@test.com", "userAccountType": "REQUESTER", ...}}'
```

2. Use the received token and userId in subsequent requests:
```bash
curl -X GET "http://localhost:8080/api/v1/contracts" \
  -H "Content-Type: application/json" \
  -H "UserId: 1" \
  -H "Authorization: uuid-token"
```

### Error Responses
Authentication errors return the following format:
```json
{
  "message": "Error message",
  "timestamp": 1234567890
}
```

Common authentication error codes:
- 400: Missing required headers
- 401: Invalid credentials
- 403: Insufficient permissions

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
| No ❌ | Yes ✅ | `/api/v1/contracts` | POST | `newContract <Contract>` { title, mass, volume, fragile, coolingRequired, rideAlong, manPower, contractDescription, price, collateral, requesterId, fromLocation, toLocation, moveDateTime, contractPhotos } | Body | 201, 400, 404 | Created contract object | Create a new contract | S5 | 
| Yes ✅ | Yes ✅ | `/api/v1/contracts` | GET | `lat <number>`, `lng <number>`, `filters <object>`{ radius (number), price (number), weight (number), height (number), length (number), width (number), requiredPeople (number), fragile (boolean), coolingRequired (boolean), rideAlong (boolean), fromAddress (string), toAddress (string), moveDateTime (string) } | Query | 200 | `{ "contracts": [...], "timestamp": 1234567890 }` | Get available contracts with filtering | S11 |
| No ❌ | Yes ✅ | `/api/v1/contracts/{id}` | GET | `id <string>` | Path | 200, 404 | Contract details object | Get contract details | S7, S12 |
| No ❌ | Yes ✅ | `/api/v1/contracts/{id}` | PUT | `id <string>`, `contractToUpdate <Contract>` | Path, Body | 200, 400, 403 | Updated contract object | Update a contract | S6 |
| No ❌ | Yes ✅ | `/api/v1/contracts/{id}/cancel` | PUT | `id <string>`, `reason <string>` | Path, Body | 200, 400, 403, 409 | Updated contract with cancel status | Cancel a contract (72h policy) | S8 | 
| No ❌ | Yes ✅ | `/api/v1/contracts/{id}/fulfill` | PUT | `id <string>` | Path | 200, 400, 403 | Updated contract status | Mark contract as fulfilled | S9, S18 | 
| No ❌ | No ❌ | `/api/v1/contracts/{id}/collateral` | POST | `id <string>`, `collateralAmount <number>` | Path, Body | 200, 400, 403, 409 | Updated contract with collateral | Provide contract collateral | S21 | 
| No ❌ | Yes ✅ | `/api/v1/users/{userId}/contracts` | GET | `userId <string>`, `status <string>` (optional) | Path, Query | 200 | List of contracts for a specific user| Get user's contracts with optional status filtering | S12 | 
| No ❌ | Yes ✅ | `/api/v1/contracts/{id}` | DELETE | `id <string>` | Path | 204, 403, 409 | None | Delete a contract (soft delete) | S8 | 

### User Contracts Details
The GET `/api/v1/users/{userId}/contracts` endpoint supports the following parameters:

#### Path Parameters
- `userId` (required): The ID of the user whose contracts to retrieve

#### Query Parameters
- `status` (optional): Contract status to filter by. If not provided, returns all contracts for the user.

#### Supported Status Values
- REQUESTED: Initial state when a contract is created
- DELETED: Contract has been deleted
- OFFERED: Driver has made an offer
- ACCEPTED: Contract has been accepted by both parties
- CANCELED: Contract has been canceled (72h policy applies)
- COMPLETED: Contract has been fulfilled
- FINALIZED: Contract is fully completed with all steps

#### Example Requests
```
# Get all contracts for a user
GET /api/v1/users/123/contracts

# Get only requested contracts for a user
GET /api/v1/users/123/contracts?status=REQUESTED

# Get only completed contracts for a user
GET /api/v1/users/123/contracts?status=COMPLETED
```

#### Response Format
The endpoint returns a list of contracts in the following format:
```json
[
  {
    "contractId": 123,
    "title": "Moving furniture",
    "price": 100.0,
    "mass": 50.0,
    "volume": 2.0,
    "fragile": true,
    "coolingRequired": false,
    "rideAlong": true,
    "manPower": 2,
    "contractDescription": "Moving a sofa and two chairs",
    "moveDateTime": "2024-04-15T10:00:00",
    "contractStatus": "REQUESTED",
    "creationDateTime": "2024-04-01T15:30:00",
    "requesterId": 123,
    "fromLocation": {
      "address": "Zurich",
      "latitude": 47.3769,
      "longitude": 8.5417
    },
    "toLocation": {
      "address": "Bern",
      "latitude": 46.9480,
      "longitude": 7.4474
    }
  }
]
```

#### Notes
- The endpoint automatically detects whether the user is a requester or driver and returns the appropriate contracts
- For requesters, it returns contracts they have created
- For drivers, it returns contracts they have been assigned to
- Status filtering works for both requester and driver accounts

### Contract Deletion Details
The DELETE `/api/v1/contracts/{id}` endpoint supports the following:

#### Path Parameters
- `id` (required): The ID of the contract to delete

#### Rules and Restrictions
- Only the requester who created the contract can delete it
- A contract can only be deleted if it's in REQUESTED or OFFERED status
- Cannot delete a contract that is:
  - Already ACCEPTED
  - Already COMPLETED
  - Already CANCELED
  - Already DELETED
- Cannot delete a contract less than 72 hours before the move date
- Deletion is implemented as a soft delete (status set to DELETED)
- The contract remains in the database for record-keeping

#### Example Request
```
DELETE /api/v1/contracts/123
```

#### Response Codes
- 204: Successfully deleted
- 403: Not authorized to delete this contract
- 409: Contract cannot be deleted (wrong status or too close to move date)

## Offer Management

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | Yes ✅ | `/api/v1/offers` | GET | `contractId` (optional), `driverId` (optional), `status` (optional) | Query | 200 | List of offers | Get all offers with optional filtering | S4, S11 |
| No ❌ | Yes ✅ | `/api/v1/offers` | POST | `contractId <string>`, `driverId <string>` | Body | 201, 400, 404, 409 | Created offer object | Create a new offer | S12 |
| No ❌ | Yes ✅ | `/api/v1/offers/{offerId}` | GET | `offerId <string>` | Path | 200, 404 | Detailed offer object | Get a specific offer | S4, S11 |
| No ❌ | Yes ✅ | `/api/v1/offers/{offerId}` | DELETE | `offerId <string>` | Path | 204, 403, 404 | None | Delete an offer | S6 |
| No ❌ | Yes ✅ | `/api/v1/contracts/{contractId}/offers` | GET | `contractId <string>` | Path | 200, 404 | List of offers | Get all offers for a specific contract | S7 |
| No ❌ | Yes ✅ | `/api/v1/offers/{offerId}/status` | PUT | `offerId <string>`, `status <string>` | Path, Body | 200, 400, 403, 404, 409 | Updated offer with new status | Update offer status | S12 |
| No ❌ | Yes ✅ | `/api/v1/users/{driverId}/offers` | GET | `driverId <string>`, `status` (optional) | Path, Query | 200, 404 | List of offers | Get all offers for a specific driver | S4, S11 |

### Offer Status Values
- CREATED: Initial state when offer is created
- ACCEPTED: Offer has been accepted by requester
- REJECTED: Offer has been rejected by requester
- DELETED: Offer has been deleted

### Request/Response Formats

#### Create Offer (POST /api/v1/offers)
**Request Body:**
```json
{
  "contractId": 123,
  "driverId": 456
}
```

**Response (201 Created):**
```json
{
  "offerId": 789,
  "contract": {
    "contractId": 123,
    "title": "Moving furniture",
    "price": 100.0,
    "mass": 50.0,
    "volume": 2.0,
    "fragile": true,
    "coolingRequired": false,
    "rideAlong": true,
    "manPower": 2,
    "contractDescription": "Moving a sofa and two chairs",
    "moveDateTime": "2024-04-15T10:00:00",
    "contractStatus": "OFFERED",
    "creationDateTime": "2024-04-01T15:30:00",
    "requesterId": 123,
    "fromLocation": {
      "address": "Zurich",
      "latitude": 47.3769,
      "longitude": 8.5417
    },
    "toLocation": {
      "address": "Bern",
      "latitude": 46.9480,
      "longitude": 7.4474
    }
  },
  "driver": {
    "userId": 456,
    "username": "driver123",
    "email": "driver@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+41791234567",
    "walletBalance": 100.0,
    "birthDate": "1990-01-01",
    "userBio": "Professional driver",
    "profilePicturePath": "/uploads/profile-pictures/driver123.jpg",
    "driverLicensePath": "/uploads/driver-licenses/driver123.jpg",
    "driverInsurancePath": "/uploads/driver-insurances/driver123.pdf",
    "preferredRange": 50.0,
    "location": {
      "address": "Zurich",
      "latitude": 47.3769,
      "longitude": 8.5417
    },
    "carDTO": {
      "model": "Volkswagen Transporter",
      "volume": 8.0,
      "isElectric": true
    }
  },
  "offerStatus": "CREATED",
  "creationDateTime": "2024-04-05T10:00:00"
}
```

#### Get Offer (GET /api/v1/offers/{offerId})
**Response (200 OK):**
```json
{
  "offerId": 789,
  "contract": {
    "contractId": 123,
    "title": "Moving furniture",
    "price": 100.0,
    "mass": 50.0,
    "volume": 2.0,
    "fragile": true,
    "coolingRequired": false,
    "rideAlong": true,
    "manPower": 2,
    "contractDescription": "Moving a sofa and two chairs",
    "moveDateTime": "2024-04-15T10:00:00",
    "contractStatus": "OFFERED",
    "creationDateTime": "2024-04-01T15:30:00",
    "requesterId": 123,
    "fromLocation": {
      "address": "Zurich",
      "latitude": 47.3769,
      "longitude": 8.5417
    },
    "toLocation": {
      "address": "Bern",
      "latitude": 46.9480,
      "longitude": 7.4474
    }
  },
  "driver": {
    "userId": 456,
    "username": "driver123",
    "email": "driver@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+41791234567",
    "walletBalance": 100.0,
    "birthDate": "1990-01-01",
    "userBio": "Professional driver",
    "profilePicturePath": "/uploads/profile-pictures/driver123.jpg",
    "driverLicensePath": "/uploads/driver-licenses/driver123.jpg",
    "driverInsurancePath": "/uploads/driver-insurances/driver123.pdf",
    "preferredRange": 50.0,
    "location": {
      "address": "Zurich",
      "latitude": 47.3769,
      "longitude": 8.5417
    },
    "carDTO": {
      "model": "Volkswagen Transporter",
      "volume": 8.0,
      "isElectric": true
    }
  },
  "offerStatus": "CREATED",
  "creationDateTime": "2024-04-05T10:00:00"
}
```

#### Update Offer Status (PUT /api/v1/offers/{offerId}/status)
**Request Body:**
```json
{
  "status": "ACCEPTED"
}
```

**Response (200 OK):**
```json
{
  "offerId": 789,
  "contract": {
    "contractId": 123,
    "title": "Moving furniture",
    "price": 100.0,
    "mass": 50.0,
    "volume": 2.0,
    "fragile": true,
    "coolingRequired": false,
    "rideAlong": true,
    "manPower": 2,
    "contractDescription": "Moving a sofa and two chairs",
    "moveDateTime": "2024-04-15T10:00:00",
    "contractStatus": "OFFERED",
    "creationDateTime": "2024-04-01T15:30:00",
    "requesterId": 123,
    "fromLocation": {
      "address": "Zurich",
      "latitude": 47.3769,
      "longitude": 8.5417
    },
    "toLocation": {
      "address": "Bern",
      "latitude": 46.9480,
      "longitude": 7.4474
    }
  },
  "driver": {
    "userId": 456,
    "username": "driver123",
    "email": "driver@example.com",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "+41791234567",
    "walletBalance": 100.0,
    "birthDate": "1990-01-01",
    "userBio": "Professional driver",
    "profilePicturePath": "/uploads/profile-pictures/driver123.jpg",
    "driverLicensePath": "/uploads/driver-licenses/driver123.jpg",
    "driverInsurancePath": "/uploads/driver-insurances/driver123.pdf",
    "preferredRange": 50.0,
    "location": {
      "address": "Zurich",
      "latitude": 47.3769,
      "longitude": 8.5417
    },
    "carDTO": {
      "model": "Volkswagen Transporter",
      "volume": 8.0,
      "isElectric": true
    }
  },
  "offerStatus": "ACCEPTED",
  "creationDateTime": "2024-04-05T10:00:00"
}
```

#### Get Offers with Filtering (GET /api/v1/offers)
**Query Parameters:**
- `contractId`: Filter by contract ID
- `driverId`: Filter by driver ID
- `status`: Filter by offer status (CREATED, ACCEPTED, REJECTED, DELETED)

**Response (200 OK):**
```json
[
  {
    "offerId": 789,
    "contract": {
      "contractId": 123,
      "title": "Moving furniture",
      "price": 100.0,
      "mass": 50.0,
      "volume": 2.0,
      "fragile": true,
      "coolingRequired": false,
      "rideAlong": true,
      "manPower": 2,
      "contractDescription": "Moving a sofa and two chairs",
      "moveDateTime": "2024-04-15T10:00:00",
      "contractStatus": "OFFERED",
      "creationDateTime": "2024-04-01T15:30:00",
      "requesterId": 123,
      "fromLocation": {
        "address": "Zurich",
        "latitude": 47.3769,
        "longitude": 8.5417
      },
      "toLocation": {
        "address": "Bern",
        "latitude": 46.9480,
        "longitude": 7.4474
      }
    },
    "driver": {
      "userId": 456,
      "username": "driver123",
      "email": "driver@example.com",
      "firstName": "John",
      "lastName": "Doe",
      "phoneNumber": "+41791234567",
      "walletBalance": 100.0,
      "birthDate": "1990-01-01",
      "userBio": "Professional driver",
      "profilePicturePath": "/uploads/profile-pictures/driver123.jpg",
      "driverLicensePath": "/uploads/driver-licenses/driver123.jpg",
      "driverInsurancePath": "/uploads/driver-insurances/driver123.pdf",
      "preferredRange": 50.0,
      "location": {
        "address": "Zurich",
        "latitude": 47.3769,
        "longitude": 8.5417
      },
      "carDTO": {
        "model": "Volkswagen Transporter",
        "volume": 8.0,
        "isElectric": true
      }
    },
    "offerStatus": "CREATED",
    "creationDateTime": "2024-04-05T10:00:00"
  },
  // ... more offers
]
```

### Error Responses

**400 Bad Request:**
```json
{
  "message": "Status is required"
}
```

**403 Forbidden:**
```json
{
  "message": "Cannot delete an accepted offer"
}
```
or
```json
{
  "message": "Cannot delete a rejected offer"
}
```
or
```json
{
  "message": "Cannot delete an offer for an accepted contract"
}
```

**404 Not Found:**
```json
{
  "message": "Offer not found"
}
```
or
```json
{
  "message": "Contract not found"
}
```
or
```json
{
  "message": "User not found"
}
```

**409 Conflict:**
```json
{
  "message": "Offer already exists for this contract and driver"
}
```
or
```json
{
  "message": "Cannot update a deleted offer"
}
```
or
```json
{
  "message": "Cannot change status of an accepted offer"
}
```
or
```json
{
  "message": "Cannot change status of a rejected offer"
}
```

### Notes
- All timestamps are in ISO 8601 format (YYYY-MM-DDTHH:MM:SS)
- Status transitions are strictly validated:
  - Cannot update a deleted offer
  - Cannot change status of an accepted offer
  - Cannot change status of a rejected offer
  - Cannot delete an offer for an accepted contract
- Contract status is automatically updated:
  - Changes to OFFERED when first offer is created
  - Reverts to REQUESTED when last offer is deleted
  - Changes to ACCEPTED when an offer is accepted
- When an offer is accepted, all other offers for the same contract are automatically rejected
- Offer deletion is only allowed for offers in CREATED status

## Ratings and Feedback

| FE | BE | Mapping | Method | Parameter | Parameter Type | Status Code | Response | Description | User Story |
|---------|--------|-----------|----------------|-------------|----------|-------------|-----------|-----------|-----------|
| No ❌ | No ❌ | `/api/v1/contracts/{id}/driver-rating` | POST | `id <string>`, `rating <integer>`, `comment <string>`, `issues <boolean>`, `issueDetails <string>` | Path, Body | 200, 400, 403, 409 | Updated contract with rating | Requester rates driver | S9 |
| No ❌ | No ❌ | `/api/v1/contracts/{id}/requester-rating` | POST | `id <string>`, `rating <integer>`, `comment <string>` | Path, Body | 200, 400, 403, 409 | Updated contract with requester rating | Driver rates requester | S18 | 
| No ❌ | No ❌ | `/api/v1/users/{id}/ratings` | GET | `id <string>`, `role <string>` (driver/requester) | Path, Query | 200, 404 | List of ratings | Get user's ratings | S9, S18 |

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
| No ❌ | No ❌ | `/api/v1/notifications` | GET | Auth token | Header | 200 | `{ "total": 10, "unread": 3, "notifications": [...] }` | Get user notifications | S17 |
| No ❌ | No ❌ | `/api/v1/notifications/{id}` | PUT | `id <string>`, `read <boolean>` | Path, Body | 200, 404 | Updated notification object | Mark notification as read | S17 |
| Yes ✅ | No ❌ | `/api/v1/map/contracts` | GET | `lat <number>`, `lng <number>`, `filters <object>`{ radius (number), price (number), weight (number), height (number), length (number), width (number), requiredPeople (number), fragile (boolean), coolingRequired (boolean), rideAlong (boolean), fromAdress (string of Location Obeject), toAdress (string of Location Object), moveDateTime (string of LocalDateTime Object) }| Query | 200 | GeoJSON of proposals | Get proposals for map display | S11, S17 |

## Response Formats

### Success Response Format
```json
{
  "status": "success",
  "data": {
    // Response data depending on the endpoint
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

#### Create Contract (POST /api/v1/contracts)
**Request Body:**
```json
{
  "title": "Moving furniture",
  "mass": 100.0,
  "volume": 2.0,
  "fragile": true,
  "coolingRequired": false,
  "rideAlong": true,
  "manPower": 2,
  "contractDescription": "Moving a sofa and two chairs",
  "price": 150.0,
  "collateral": 200.0,
  "requesterId": 1,
  "fromLocation": {
    "formattedAddress": "Zurich, Switzerland",
    "latitude": 47.3769,
    "longitude": 8.5417
  },
  "toLocation": {
    "formattedAddress": "Bern, Switzerland",
    "latitude": 46.9480,
    "longitude": 7.4474
  },
  "moveDateTime": "2025-05-20T10:00:00",
  "contractPhotos": [
    "/path/to/photo1.jpg",
    "/path/to/photo2.jpg"
  ]
}
```

**Response (201 Created):**
```json
{
  "contractId": 8,
  "title": "Moving furniture",
  "mass": 100.0,
  "volume": 2.0,
  "fragile": true,
  "coolingRequired": false,
  "rideAlong": true,
  "manPower": 2,
  "contractDescription": "Moving a sofa and two chairs",
  "price": 150.0,
  "collateral": 200.0,
  "requesterId": 1,
  "fromLocation": {
    "locationId": 6,
    "formattedAddress": "Zurich, Switzerland",
    "latitude": 47.3769,
    "longitude": 8.5417
  },
  "toLocation": {
    "locationId": 7,
    "formattedAddress": "Bern, Switzerland",
    "latitude": 46.9480,
    "longitude": 7.4474
  },
  "moveDateTime": "2025-05-20T10:00:00",
  "contractStatus": "REQUESTED",
  "creationDateTime": "2025-04-13T13:15:41.80276",
  "contractPhotos": [
    "/path/to/photo1.jpg",
    "/path/to/photo2.jpg"
  ]
}
```
