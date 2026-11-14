package com.assignment.dispatch.model;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class Vehicle {

    @NotBlank(message = "vehicleId is required")
    private String vehicleId;

    @NotNull(message = "capacity is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "capacity must be greater than 0")
    private Double capacity;

    @NotNull(message = "currentLatitude is required")
    private Double currentLatitude;

    @NotNull(message = "currentLongitude is required")
    private Double currentLongitude;

    @NotBlank(message = "currentAddress is required")
    private String currentAddress;

    public Vehicle() {
    }

    public Vehicle(String vehicleId, Double capacity, Double currentLatitude, Double currentLongitude, String currentAddress) {
        this.vehicleId = vehicleId;
        this.capacity = capacity;
        this.currentLatitude = currentLatitude;
        this.currentLongitude = currentLongitude;
        this.currentAddress = currentAddress;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public Double getCapacity() {
        return capacity;
    }

    public void setCapacity(Double capacity) {
        this.capacity = capacity;
    }

    public Double getCurrentLatitude() {
        return currentLatitude;
    }

    public void setCurrentLatitude(Double currentLatitude) {
        this.currentLatitude = currentLatitude;
    }

    public Double getCurrentLongitude() {
        return currentLongitude;
    }

    public void setCurrentLongitude(Double currentLongitude) {
        this.currentLongitude = currentLongitude;
    }

    public String getCurrentAddress() {
        return currentAddress;
    }

    public void setCurrentAddress(String currentAddress) {
        this.currentAddress = currentAddress;
    }
}
