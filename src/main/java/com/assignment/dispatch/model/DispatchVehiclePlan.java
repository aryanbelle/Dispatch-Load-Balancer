package com.assignment.dispatch.model;

import java.util.ArrayList;
import java.util.List;

public class DispatchVehiclePlan {

    private String vehicleId;
    private Double totalLoad;
    private String totalDistance;
    private List<Order> assignedOrders = new ArrayList<>();

    public DispatchVehiclePlan() {
    }

    public DispatchVehiclePlan(String vehicleId, Double totalLoad, String totalDistance, List<Order> assignedOrders) {
        this.vehicleId = vehicleId;
        this.totalLoad = totalLoad;
        this.totalDistance = totalDistance;
        this.assignedOrders = assignedOrders;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getTotalLoad() {
        return totalLoad;
    }

    public void setTotalLoad(Double totalLoad) {
        this.totalLoad = totalLoad;
    }

    public String getTotalDistance() {
        return totalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        this.totalDistance = totalDistance;
    }

    public List<Order> getAssignedOrders() {
        return assignedOrders;
    }

    public void setAssignedOrders(List<Order> assignedOrders) {
        this.assignedOrders = assignedOrders;
    }
}
