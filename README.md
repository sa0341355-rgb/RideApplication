# AgenticRideApplication

A ride-booking backend built as a Spring Boot microservices system, similar in concept to Uber/Ola. The system handles rider and driver management, intelligent driver matching, real-time ride orchestration via Kafka, route calculation via LocationIQ, and payment processing via Razorpay.

---

## Architecture

```
                        ┌─────────────────────┐
                        │   Eureka Discovery   │
                        │   Server  :8080      │
                        └──────────┬──────────┘
                                   │ (all services register)
                        ┌──────────▼──────────┐
           Client ────► │    API Gateway       │
                        │    :8082             │
                        └──┬──────┬──────┬──┬─┘
                           │      │      │  │
            ┌──────────────▼┐  ┌──▼────┐ │ ┌▼──────────────┐  ┌────────────────┐
            │ rider-service │  │driver-│ │ │ ride-service  │  │payment-service │
            │   :8083       │  │service│ │ │   :8085       │  │   :8086        │
            └───────────────┘  │ :8084 │ │ └──────┬────────┘  └────────────────┘
                               └───────┘ │        │
                                    ▲    │        │ Kafka: ride-events
                                    │    │        ▼
                                    │    │  ┌─────────────┐
                                    └────┘  │    Kafka     │
                                            │   :9092      │
                                            └─────────────┘
```

**Synchronous communication**: ride-service → rider-service and driver-service via OpenFeign (REST + Eureka load balancing)

**Asynchronous communication**: ride-service → Kafka topic `ride-events` → driver-service consumer

**External APIs**: ride-service → LocationIQ (routing), payment-service → Razorpay (payments)

---

## Services

| Service | Port | Role |
|---|---|---|
| `discoveryserver` | 8080 | Netflix Eureka service registry |
| `api-gateway` | 8082 | Single entry point, routes all client traffic |
| `rider-service` | 8083 | Rider profile management (CRUD) |
| `driver-service` | 8084 | Driver profiles, location tracking, availability |
| `ride-service` | 8085 | Core orchestrator — creates rides, matches nearest driver |
| `paymentservice` | 8086 | Razorpay order creation and payment verification |

---

## Tech Stack

- **Java 17**
- **Spring Boot 4.1.1**
- **Spring Cloud 2025.1.3** (Eureka, OpenFeign, Gateway, LoadBalancer)
- **Apache Kafka** — asynchronous ride events
- **H2 In-Memory Database** — rider, driver, and ride data (dev only)
- **Razorpay Java SDK 1.4.10** — payment processing
- **LocationIQ REST API** — driving route and distance calculation
- **Lombok** — boilerplate reduction
- **Maven** — build tool (each service is an independent Maven project)

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Apache Kafka running on `localhost:9092`

> No Docker or docker-compose files are included. All services must be started manually.

---

## Getting Started

### 1. Start Kafka

Start your local Kafka broker on `localhost:9092`. Kafka must be running before any service starts.

### 2. Start services in order

Services must be started in this order — downstream services depend on Eureka being up first:

```bash
# 1. Discovery Server (Eureka) — must be first
cd discoveryserver
./mvnw spring-boot:run

# 2. API Gateway
cd api-gateway
./mvnw spring-boot:run

# 3. Rider Service
cd rider-service
./mvnw spring-boot:run

# 4. Driver Service
cd driver-service
./mvnw spring-boot:run

# 5. Ride Service
cd ride-service
./mvnw spring-boot:run

# 6. Payment Service
cd paymentservice
./mvnw spring-boot:run
```

On Windows (Command Prompt or PowerShell), replace `./mvnw` with `mvnw.cmd`:

```powershell
mvnw.cmd spring-boot:run
```

### 3. Verify registration

Open the Eureka dashboard at [http://localhost:8080](http://localhost:8080) and confirm all 5 client services appear as registered instances.

---

## Configuration

Each service has its own `src/main/resources/application.properties`.

### External API Keys

| Service | Property | Description |
|---|---|---|
| `ride-service` | `locationiq.api-key` | LocationIQ API key for route calculation |
| `paymentservice` | `razorpay.key-id` | Razorpay public key ID |
| `paymentservice` | `razorpay.key-secret` | Razorpay secret key (used for HMAC-SHA256 verification) |

> ⚠️ **Warning**: These keys are currently hardcoded in properties files. Before pushing to any shared or public repository, move them to environment variables or a secrets manager.

### H2 Console (dev only)

| Service | Console URL | JDBC URL |
|---|---|---|
| rider-service | http://localhost:8083/h2-console | `jdbc:h2:mem:rider_db` |
| driver-service | http://localhost:8084/h2-console | `jdbc:h2:mem:driver_db` |
| ride-service | http://localhost:8085/h2-console | `jdbc:h2:mem:ride_db` |

Username: `sa` | Password: *(empty)*

> All databases are in-memory. Data is lost on every restart.

---

## API Reference

All requests go through the API Gateway at `http://localhost:8082`. The service name is the first path segment and is stripped before forwarding to the downstream service.

---

### Rider Service — `/rider-service`

#### Create a Rider
```http
POST /rider-service/riders
Content-Type: application/json

{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "9876543210"
}
```

#### Get All Riders
```http
GET /rider-service/riders
```

#### Get Rider by ID
```http
GET /rider-service/riders/{id}
```

#### Delete Rider
```http
DELETE /rider-service/riders/{id}
```

---

### Driver Service — `/driver-service`

#### Create a Driver
```http
POST /driver-service/drivers
Content-Type: application/json

{
  "name": "Ravi Kumar",
  "phone": "9123456780",
  "vehicleNumber": "KA01AB1234",
  "vehicleType": "SEDAN",
  "available": true,
  "latitude": 12.9716,
  "longitude": 77.5946
}
```

#### Get All Drivers
```http
GET /driver-service/drivers
```

#### Get Available Drivers
```http
GET /driver-service/drivers/available
```

#### Update Driver Location
```http
PUT /driver-service/drivers/{id}/location
Content-Type: application/json

{
  "latitude": 12.9800,
  "longitude": 77.6000
}
```

#### Update Driver Availability
```http
PUT /driver-service/drivers/{id}/availability?available=true
```

#### Delete Driver
```http
DELETE /driver-service/drivers/{id}
```

---

### Ride Service — `/ride-service`

#### Create a Ride
```http
POST /ride-service/rides?riderId=1&pickupLocation=Koramangala&dropLocation=Indiranagar&pickupLatitude=12.9352&pickupLongitude=77.6245&fare=150.0
```

> Automatically validates the rider, finds the nearest available driver using the Haversine formula, marks the driver unavailable, saves the ride, and publishes a Kafka event.

#### Get All Rides
```http
GET /ride-service/rides
```

#### Get Ride by ID
```http
GET /ride-service/rides/{id}
```

#### Get Nearest Driver
```http
GET /ride-service/rides/nearest-driver?pickupLatitude=12.9352&pickupLongitude=77.6245
```

#### Get Available Drivers (proxied from driver-service)
```http
GET /ride-service/rides/available-drivers
```

#### Get Route (LocationIQ)
```http
GET /ride-service/rides/route?pickupLat=12.9352&pickupLon=77.6245&dropLat=12.9719&dropLon=77.6412
```

Returns driving distance (metres) and duration (seconds) from LocationIQ directions API.

---

### Payment Service — `/payment-service`

#### Create a Razorpay Order
```http
POST /payment-service/payments/order?rideId=1&amount=150.0
```

Returns a Razorpay order JSON. Amount is converted to paise (×100) internally.

#### Verify Payment
```http
POST /payment-service/payments/verify
Content-Type: application/json

{
  "razorpayOrderId": "order_XXXXXXXXXX",
  "razorpayPaymentId": "pay_XXXXXXXXXX",
  "razorpaySignature": "<hmac_sha256_signature>"
}
```

Returns `true` if the signature is valid, `false` otherwise.

---

## Ride Creation Flow

```
Client → POST /ride-service/rides (via API Gateway :8082)
  │
  ├─ Feign → GET /riders/{id}           (rider-service: validate rider)
  ├─ Feign → GET /drivers/available     (driver-service: fetch available drivers)
  ├─ Haversine formula                  (pick nearest driver by GPS distance)
  ├─ Feign → PUT /drivers/{id}/availability?available=false  (mark driver busy)
  ├─ Save Ride to H2 DB                 (status: DRIVER_ASSIGNED)
  └─ Kafka → topic: ride-events         (publish RideCreateEvent as JSON)
               │
               └─ driver-service consumer receives event (currently logs to console)
```

---

## Ride Status Lifecycle

```
REQUESTED → DRIVER_ASSIGNED → DRIVER_ARRIVING → IN_PROGRESS → COMPLETED
                                                             → CANCELLED
```

> ⚠️ Currently, only the `DRIVER_ASSIGNED` state is set. Endpoints to transition through the remaining states are not yet implemented.

---

## Known Limitations

| Area | Issue |
|---|---|
| Security | No authentication or authorization. All endpoints are publicly accessible. |
| Data | H2 in-memory databases only. All data is lost on restart. |
| Fare | Fare is supplied by the client, not calculated server-side. |
| Ride lifecycle | No endpoints to transition ride status beyond `DRIVER_ASSIGNED`. |
| Kafka consumer | driver-service consumes ride events but only logs them — no business logic. |
| Payment linkage | Payment verification does not update ride status. |
| Error handling | No global exception handler. Raw stack traces returned on errors. |
| Infrastructure | No Docker, docker-compose, or Kubernetes manifests. |
| Tracing | No distributed tracing (Zipkin / Micrometer Tracing) configured. |
| Frontend | No UI. Backend APIs only. |

---

## Project Structure

```
AgenticRideApplication/
├── discoveryserver/          # Eureka Server
├── api-gateway/              # Spring Cloud Gateway
├── rider-service/            # Rider management
├── driver-service/           # Driver management + Kafka consumer
├── ride-service/             # Ride orchestration + Kafka producer
└── paymentservice/           # Razorpay integration
```

Each service is a self-contained Maven project with its own `pom.xml`, `src/`, and `application.properties`.

---

## Port Summary

| Component | Port |
|---|---|
| Eureka Discovery Server | 8080 |
| API Gateway | 8082 |
| Rider Service | 8083 |
| Driver Service | 8084 |
| Ride Service | 8085 |
| Payment Service | 8086 |
| Kafka Broker | 9092 |
