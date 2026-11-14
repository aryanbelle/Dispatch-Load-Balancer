# Backend Assignment - Dispatch Load Balancer

Develop a Spring Boot application that optimizes the allocation of delivery orders to a fleet of vehicles based on their locations. The application should return an optimized dispatch plan while considering vehicle capacities and minimizing total travel distance.

Use Haversine distance formula to calculate distance between different drop points.

## Updated Requirements

### 1. Input Delivery Orders

Each order must include:

- `orderId`
- `latitude` and `longitude`
- `address` (e.g., "MG Road, Bangalore, Karnataka, India")
- `packageWeight`
- `priority` (HIGH, MEDIUM, LOW)

### 2. Input Fleet Details

Each vehicle must include:

- `vehicleId`
- `capacity`
- `currentLatitude` and `currentLongitude`
- `currentAddress` (e.g., "Indiranagar, Bangalore, Karnataka, India")

### 3. Dispatch Optimization

Assign orders to vehicles ensuring:

- Total load does not exceed vehicle capacity.
- High-priority orders are assigned first.
- Total travel distance is minimized.

Use the Haversine formula to calculate distances between coordinates.

### 4. Output Dispatch Plan

The dispatch plan should include:

- Vehicle details with assigned orders.
- Total load and total travel distance for each vehicle.
- Addresses of assigned orders for easier visualization.

### 5. Error Handling

Handle invalid input, overcapacity, and edge cases (e.g., unassignable orders).

## API Specifications

### 1. Input Delivery Orders

**Endpoint:**

`POST /api/dispatch/orders`

**Request Body:**

```json
{
  "orders": [
    {
      "orderId": "ORD001",
      "latitude": 12.9716,
      "longitude": 77.5946,
      "address": "MG Road, Bangalore, Karnataka, India",
      "packageWeight": 10,
      "priority": "HIGH"
    },
    {
      "orderId": "ORD002",
      "latitude": 13.0827,
      "longitude": 80.2707,
      "address": "Anna Salai, Chennai, Tamil Nadu, India",
      "packageWeight": 20,
      "priority": "MEDIUM"
    }
  ]
}
```

**Response:**

```json
{
  "message": "Delivery orders accepted.",
  "status": "success"
}
```

### 2. Input Fleet Details

**Endpoint:**

`POST /api/dispatch/vehicles`

**Request Body:**

```json
{
  "vehicles": [
    {
      "vehicleId": "VEH001",
      "capacity": 100,
      "currentLatitude": 12.9716,
      "currentLongitude": 77.6413,
      "currentAddress": "Indiranagar, Bangalore, Karnataka, India"
    },
    {
      "vehicleId": "VEH002",
      "capacity": 150,
      "currentLatitude": 13.0674,
      "currentLongitude": 80.2376,
      "currentAddress": "T Nagar, Chennai, Tamil Nadu, India"
    }
  ]
}
```

**Response:**

```json
{
  "message": "Vehicle details accepted.",
  "status": "success"
}
```

### 3. Retrieve Dispatch Plan

**Endpoint:**

`GET /api/dispatch/plan`

**Response:**

```json
{
  "dispatchPlan": [
    {
      "vehicleId": "VEH001",
      "totalLoad": 10,
      "totalDistance": "5 km",
      "assignedOrders": [
        {
          "orderId": "ORD001",
          "latitude": 12.9716,
          "longitude": 77.5946,
          "address": "MG Road, Bangalore, Karnataka, India",
          "packageWeight": 10,
          "priority": "HIGH"
        }
      ]
    },
    {
      "vehicleId": "VEH002",
      "totalLoad": 20,
      "totalDistance": "6 km",
      "assignedOrders": [
        {
          "orderId": "ORD002",
          "latitude": 13.0827,
          "longitude": 80.2707,
          "address": "Anna Salai, Chennai, Tamil Nadu, India",
          "packageWeight": 20,
          "priority": "MEDIUM"
        }
      ]
    }
  ]
}
```

## Sample inputs

### Orders

```json
[
  { "orderId": "ORD001", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 15, "priority": "HIGH" },
  { "orderId": "ORD002", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 10, "priority": "MEDIUM" },
  { "orderId": "ORD003", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 20, "priority": "LOW" },
  { "orderId": "ORD004", "latitude": 28.5355, "longitude": 77.3910, "address": "Sector 18, Noida, Uttar Pradesh, India", "packageWeight": 25, "priority": "HIGH" },
  { "orderId": "ORD005", "latitude": 28.4595, "longitude": 77.0266, "address": "Cyber Hub, Gurgaon, Haryana, India", "packageWeight": 30, "priority": "MEDIUM" },
  { "orderId": "ORD006", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 40, "priority": "LOW" },
  { "orderId": "ORD007", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 20, "priority": "HIGH" },
  { "orderId": "ORD008", "latitude": 28.5355, "longitude": 77.3910, "address": "Sector 18, Noida, Uttar Pradesh, India", "packageWeight": 25, "priority": "HIGH" },
  { "orderId": "ORD009", "latitude": 28.4595, "longitude": 77.0266, "address": "Cyber Hub, Gurgaon, Haryana, India", "packageWeight": 15, "priority": "MEDIUM" },
  { "orderId": "ORD010", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 30, "priority": "LOW" },
  { "orderId": "ORD011", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 20, "priority": "HIGH" },
  { "orderId": "ORD012", "latitude": 28.5355, "longitude": 77.3910, "address": "Sector 18, Noida, Uttar Pradesh, India", "packageWeight": 10, "priority": "MEDIUM" },
  { "orderId": "ORD013", "latitude": 28.4595, "longitude": 77.0266, "address": "Cyber Hub, Gurgaon, Haryana, India", "packageWeight": 25, "priority": "HIGH" },
  { "orderId": "ORD014", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 15, "priority": "LOW" },
  { "orderId": "ORD015", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 20, "priority": "HIGH" },
  { "orderId": "ORD016", "latitude": 28.5355, "longitude": 77.3910, "address": "Sector 18, Noida, Uttar Pradesh, India", "packageWeight": 30, "priority": "LOW" },
  { "orderId": "ORD017", "latitude": 28.4595, "longitude": 77.0266, "address": "Cyber Hub, Gurgaon, Haryana, India", "packageWeight": 15, "priority": "MEDIUM" },
  { "orderId": "ORD018", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 10, "priority": "HIGH" },
  { "orderId": "ORD019", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 20, "priority": "LOW" },
  { "orderId": "ORD020", "latitude": 28.5355, "longitude": 77.3910, "address": "Sector 18, Noida, Uttar Pradesh, India", "packageWeight": 30, "priority": "HIGH" },
  { "orderId": "ORD021", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 25, "priority": "MEDIUM" },
  { "orderId": "ORD022", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 20, "priority": "LOW" },
  { "orderId": "ORD023", "latitude": 28.5355, "longitude": 77.3910, "address": "Sector 18, Noida, Uttar Pradesh, India", "packageWeight": 30, "priority": "HIGH" },
  { "orderId": "ORD024", "latitude": 28.4595, "longitude": 77.0266, "address": "Cyber Hub, Gurgaon, Haryana, India", "packageWeight": 15, "priority": "LOW" },
  { "orderId": "ORD025", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 20, "priority": "MEDIUM" },
  { "orderId": "ORD026", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 25, "priority": "HIGH" },
  { "orderId": "ORD027", "latitude": 28.5355, "longitude": 77.3910, "address": "Sector 18, Noida, Uttar Pradesh, India", "packageWeight": 20, "priority": "MEDIUM" },
  { "orderId": "ORD028", "latitude": 28.4595, "longitude": 77.0266, "address": "Cyber Hub, Gurgaon, Haryana, India", "packageWeight": 15, "priority": "LOW" },
  { "orderId": "ORD029", "latitude": 28.6139, "longitude": 77.2090, "address": "Connaught Place, Delhi, India", "packageWeight": 30, "priority": "HIGH" },
  { "orderId": "ORD030", "latitude": 28.7041, "longitude": 77.1025, "address": "Karol Bagh, Delhi, India", "packageWeight": 20, "priority": "LOW" }
]
```

### Vehicles

```json
[
  { "vehicleId": "VEH001", "capacity": 100, "currentLatitude": 28.7041, "currentLongitude": 77.1025, "currentAddress": "Karol Bagh, Delhi, India" },
  { "vehicleId": "VEH002", "capacity": 80, "currentLatitude": 28.5355, "currentLongitude": 77.3910, "currentAddress": "Sector 18, Noida, Uttar Pradesh, India" },
  { "vehicleId": "VEH003", "capacity": 120, "currentLatitude": 28.4595, "currentLongitude": 77.0266, "currentAddress": "Cyber Hub, Gurgaon, Haryana, India" },
  { "vehicleId": "VEH004", "capacity": 90, "currentLatitude": 28.6139, "currentLongitude": 77.2090, "currentAddress": "Connaught Place, Delhi, India" },
  { "vehicleId": "VEH005", "capacity": 110, "currentLatitude": 28.7041, "currentLongitude": 77.1025, "currentAddress": "Karol Bagh, Delhi, India" }
]
```

## Evaluation Criteria

- Assignment code submitted to be in Java
- REST Compliant APIs, (No UI)
- Testable by Postman, (No UI)
- Will prefer classes properly refactored and following design patterns,
- Clarity of API documentation and how to run the project.
- Will Prefer TDD, JUnit , Test Coverage: Comprehensive test cases covering various equation scenarios.
- Efficient database operations (e.g., upsert for repository data).
- Code quality and structure (e.g., modular design, clear separation of concerns).
- Error Handling: Robust validation and error messages. Also Graceful handling of invalid or edge-case scenarios.
- Performance - Efficient handling of large datasets.
- Correctness:
  - Orders are correctly assigned based on constraints.
  - Accurate distance calculations.
