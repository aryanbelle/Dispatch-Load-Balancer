package com.assignment.dispatch.service;

import com.assignment.dispatch.exception.BadRequestException;
import com.assignment.dispatch.model.DispatchVehiclePlan;
import com.assignment.dispatch.model.Order;
import com.assignment.dispatch.model.Priority;
import com.assignment.dispatch.model.Vehicle;
import com.assignment.dispatch.util.HaversineDistanceCalculator;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

@Service
public class DispatchService {

    private final Map<String, Order> ordersById = new LinkedHashMap<>();
    private final Map<String, Vehicle> vehiclesById = new LinkedHashMap<>();

    public synchronized void setOrders(List<Order> incomingOrders) {
        List<Order> orders = normalizeOrders(incomingOrders);
        ordersById.clear();
        for (Order order : orders) {
            ordersById.put(order.getOrderId(), order);
        }
    }

    public synchronized void setVehicles(List<Vehicle> incomingVehicles) {
        List<Vehicle> vehicles = normalizeVehicles(incomingVehicles);
        vehiclesById.clear();
        for (Vehicle vehicle : vehicles) {
            vehiclesById.put(vehicle.getVehicleId(), vehicle);
        }
    }

    public synchronized List<DispatchVehiclePlan> generateDispatchPlan() {
        if (vehiclesById.isEmpty()) {
            return List.of();
        }

        List<Order> sortedOrders = ordersById.values().stream()
                .sorted(Comparator
                        .comparingInt((Order order) -> priorityRank(order.getPriority())).reversed()
                        .thenComparing(Comparator.comparingDouble(Order::getPackageWeight).reversed())
                        .thenComparing(Order::getOrderId))
                .toList();

        Map<String, List<Order>> assignedOrdersByVehicle = new LinkedHashMap<>();
        Map<String, Double> remainingCapacityByVehicle = new LinkedHashMap<>();

        for (Vehicle vehicle : vehiclesById.values()) {
            assignedOrdersByVehicle.put(vehicle.getVehicleId(), new ArrayList<>());
            remainingCapacityByVehicle.put(vehicle.getVehicleId(), vehicle.getCapacity());
        }

        for (Order order : sortedOrders) {
            Vehicle bestVehicle = findBestVehicle(order, remainingCapacityByVehicle);
            if (bestVehicle == null) {
                continue;
            }

            assignedOrdersByVehicle.get(bestVehicle.getVehicleId()).add(order);
            remainingCapacityByVehicle.put(
                    bestVehicle.getVehicleId(),
                    remainingCapacityByVehicle.get(bestVehicle.getVehicleId()) - order.getPackageWeight());
        }

        List<DispatchVehiclePlan> plans = new ArrayList<>();
        for (Vehicle vehicle : vehiclesById.values()) {
            List<Order> assignedOrders = assignedOrdersByVehicle.get(vehicle.getVehicleId());
            double totalLoad = assignedOrders.stream().mapToDouble(Order::getPackageWeight).sum();
            double totalDistance = calculateTotalRouteDistance(vehicle, assignedOrders);
            plans.add(new DispatchVehiclePlan(
                    vehicle.getVehicleId(),
                    roundDouble(totalLoad),
                    formatDistance(totalDistance),
                    assignedOrders));
        }
        return plans;
    }

    private Vehicle findBestVehicle(Order order, Map<String, Double> remainingCapacityByVehicle) {
        Vehicle bestVehicle = null;
        double bestDistance = Double.MAX_VALUE;

        for (Vehicle vehicle : vehiclesById.values()) {
            Double remainingCapacity = remainingCapacityByVehicle.get(vehicle.getVehicleId());
            if (remainingCapacity == null || remainingCapacity < order.getPackageWeight()) {
                continue;
            }

            double distance = HaversineDistanceCalculator.calculate(
                    vehicle.getCurrentLatitude(),
                    vehicle.getCurrentLongitude(),
                    order.getLatitude(),
                    order.getLongitude());

            if (distance < bestDistance || (Math.abs(distance - bestDistance) < 1e-9 && (bestVehicle == null || vehicle.getCapacity() > bestVehicle.getCapacity()))) {
                bestVehicle = vehicle;
                bestDistance = distance;
            }
        }

        return bestVehicle;
    }

    private static int priorityRank(Priority priority) {
        if (priority == null) {
            return 0;
        }
        return switch (priority) {
            case HIGH -> 3;
            case MEDIUM -> 2;
            case LOW -> 1;
        };
    }

    private static double calculateTotalRouteDistance(Vehicle vehicle, List<Order> assignedOrders) {
        if (assignedOrders == null || assignedOrders.isEmpty()) {
            return 0.0;
        }

        List<Order> remainingOrders = new ArrayList<>(assignedOrders);
        double currentLatitude = vehicle.getCurrentLatitude();
        double currentLongitude = vehicle.getCurrentLongitude();
        double totalDistance = 0.0;

        while (!remainingOrders.isEmpty()) {
            final double currentLat = currentLatitude;
            final double currentLng = currentLongitude;

            Order nextOrder = remainingOrders.stream()
                    .min(Comparator.comparingDouble(order -> HaversineDistanceCalculator.calculate(
                            currentLat,
                            currentLng,
                            order.getLatitude(),
                            order.getLongitude())))
                    .orElse(remainingOrders.get(0));

            totalDistance += HaversineDistanceCalculator.calculate(
                    currentLat,
                    currentLng,
                    nextOrder.getLatitude(),
                    nextOrder.getLongitude());

            currentLatitude = nextOrder.getLatitude();
            currentLongitude = nextOrder.getLongitude();
            remainingOrders.remove(nextOrder);
        }

        return totalDistance;
    }

    private static String formatDistance(double totalDistance) {
        double roundedDistance = Math.round(totalDistance * 10.0) / 10.0;
        if (Math.abs(roundedDistance - Math.rint(roundedDistance)) < 1e-9) {
            return String.format(Locale.US, "%d km", (int) Math.rint(roundedDistance));
        }
        return String.format(Locale.US, "%.1f km", roundedDistance);
    }

    private static double roundDouble(double value) {
        return Math.round(value * 100.0) / 100.0;
    }

    private static List<Order> normalizeOrders(List<Order> incomingOrders) {
        if (incomingOrders == null || incomingOrders.isEmpty()) {
            throw new BadRequestException("orders cannot be empty");
        }

        List<Order> normalized = new ArrayList<>();
        for (Order order : incomingOrders) {
            if (order == null) {
                throw new BadRequestException("order details cannot be null");
            }
            validateOrder(order);
            normalized.add(order);
        }

        if (normalized.stream().map(Order::getOrderId).distinct().count() != normalized.size()) {
            throw new BadRequestException("duplicate orderId values detected");
        }

        return normalized;
    }

    private static List<Vehicle> normalizeVehicles(List<Vehicle> incomingVehicles) {
        if (incomingVehicles == null || incomingVehicles.isEmpty()) {
            throw new BadRequestException("vehicles cannot be empty");
        }

        List<Vehicle> normalized = new ArrayList<>();
        for (Vehicle vehicle : incomingVehicles) {
            if (vehicle == null) {
                throw new BadRequestException("vehicle details cannot be null");
            }
            validateVehicle(vehicle);
            normalized.add(vehicle);
        }

        if (normalized.stream().map(Vehicle::getVehicleId).distinct().count() != normalized.size()) {
            throw new BadRequestException("duplicate vehicleId values detected");
        }

        return normalized;
    }

    private static void validateOrder(Order order) {
        if (order.getOrderId() == null || order.getOrderId().isBlank()) {
            throw new BadRequestException("orderId is required");
        }
        if (order.getLatitude() == null || order.getLatitude() < -90 || order.getLatitude() > 90) {
            throw new BadRequestException("latitude for order " + order.getOrderId() + " must be between -90 and 90");
        }
        if (order.getLongitude() == null || order.getLongitude() < -180 || order.getLongitude() > 180) {
            throw new BadRequestException("longitude for order " + order.getOrderId() + " must be between -180 and 180");
        }
        if (order.getAddress() == null || order.getAddress().isBlank()) {
            throw new BadRequestException("address for order " + order.getOrderId() + " is required");
        }
        if (order.getPackageWeight() == null || order.getPackageWeight() <= 0) {
            throw new BadRequestException("packageWeight for order " + order.getOrderId() + " must be greater than 0");
        }
        if (order.getPriority() == null) {
            throw new BadRequestException("priority for order " + order.getOrderId() + " is required");
        }
    }

    private static void validateVehicle(Vehicle vehicle) {
        if (vehicle.getVehicleId() == null || vehicle.getVehicleId().isBlank()) {
            throw new BadRequestException("vehicleId is required");
        }
        if (vehicle.getCapacity() == null || vehicle.getCapacity() <= 0) {
            throw new BadRequestException("capacity for vehicle " + vehicle.getVehicleId() + " must be greater than 0");
        }
        if (vehicle.getCurrentLatitude() == null || vehicle.getCurrentLatitude() < -90 || vehicle.getCurrentLatitude() > 90) {
            throw new BadRequestException("currentLatitude for vehicle " + vehicle.getVehicleId() + " must be between -90 and 90");
        }
        if (vehicle.getCurrentLongitude() == null || vehicle.getCurrentLongitude() < -180 || vehicle.getCurrentLongitude() > 180) {
            throw new BadRequestException("currentLongitude for vehicle " + vehicle.getVehicleId() + " must be between -180 and 180");
        }
        if (vehicle.getCurrentAddress() == null || vehicle.getCurrentAddress().isBlank()) {
            throw new BadRequestException("currentAddress for vehicle " + vehicle.getVehicleId() + " is required");
        }
    }
}
