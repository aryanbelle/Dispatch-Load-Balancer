package com.assignment.dispatch;

import com.assignment.dispatch.model.DispatchVehiclePlan;
import com.assignment.dispatch.model.Order;
import com.assignment.dispatch.model.Priority;
import com.assignment.dispatch.model.Vehicle;
import com.assignment.dispatch.service.DispatchService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DispatchServiceTest {

    private final DispatchService dispatchService = new DispatchService();

    @Test
    void shouldAssignOrdersWithinVehicleCapacity() {
        dispatchService.setOrders(List.of(
                new Order("ORD001", 28.6139, 77.2090, "Connaught Place", 15.0, Priority.HIGH),
                new Order("ORD002", 28.7041, 77.1025, "Karol Bagh", 20.0, Priority.MEDIUM),
                new Order("ORD003", 28.5355, 77.3910, "Noida", 25.0, Priority.HIGH),
                new Order("ORD004", 28.4595, 77.0266, "Gurugram", 10.0, Priority.LOW)
        ));

        dispatchService.setVehicles(List.of(
                new Vehicle("VEH001", 50.0, 28.7041, 77.1025, "Karol Bagh"),
                new Vehicle("VEH002", 30.0, 28.6139, 77.2090, "Connaught Place")
        ));

        List<DispatchVehiclePlan> plans = dispatchService.generateDispatchPlan();

        assertEquals(2, plans.size());
        for (DispatchVehiclePlan plan : plans) {
            double totalLoad = plan.getAssignedOrders().stream().mapToDouble(Order::getPackageWeight).sum();
            assertTrue(totalLoad <= getCapacity(plan.getVehicleId()));
        }
    }

    @Test
    void shouldPreferHighPriorityOrdersFirst() {
        dispatchService.setOrders(List.of(
                new Order("ORD001", 28.6139, 77.2090, "Connaught Place", 15.0, Priority.HIGH),
                new Order("ORD002", 28.7041, 77.1025, "Karol Bagh", 20.0, Priority.LOW),
                new Order("ORD003", 28.5355, 77.3910, "Noida", 25.0, Priority.HIGH)
        ));

        dispatchService.setVehicles(List.of(
                new Vehicle("VEH001", 60.0, 28.7041, 77.1025, "Karol Bagh"),
                new Vehicle("VEH002", 60.0, 28.6139, 77.2090, "Connaught Place")
        ));

        List<DispatchVehiclePlan> plans = dispatchService.generateDispatchPlan();
        List<Order> assignedOrders = plans.stream()
                .flatMap(plan -> plan.getAssignedOrders().stream())
                .toList();

        assertTrue(assignedOrders.stream().anyMatch(order -> order.getOrderId().equals("ORD001")));
        assertTrue(assignedOrders.stream().anyMatch(order -> order.getOrderId().equals("ORD003")));
    }

    @Test
    void shouldAssignHighPriorityOrderWhenCapacityCannotFitEveryOrder() {
        dispatchService.setOrders(List.of(
                new Order("ORD001", 28.6139, 77.2090, "Connaught Place", 10.0, Priority.LOW),
                new Order("ORD002", 28.7041, 77.1025, "Karol Bagh", 10.0, Priority.HIGH)
        ));
        dispatchService.setVehicles(List.of(
                new Vehicle("VEH001", 10.0, 28.6139, 77.2090, "Connaught Place")
        ));

        List<DispatchVehiclePlan> plans = dispatchService.generateDispatchPlan();

        assertEquals(1, plans.get(0).getAssignedOrders().size());
        assertEquals("ORD002", plans.get(0).getAssignedOrders().get(0).getOrderId());
    }

    private double getCapacity(String vehicleId) {
        return switch (vehicleId) {
            case "VEH001" -> 50.0;
            case "VEH002" -> 30.0;
            default -> 0.0;
        };
    }
}
