package com.assignment.dispatch.model;

import java.util.ArrayList;
import java.util.List;

public class DispatchPlanResponse {

    private List<DispatchVehiclePlan> dispatchPlan = new ArrayList<>();

    public DispatchPlanResponse() {
    }

    public DispatchPlanResponse(List<DispatchVehiclePlan> dispatchPlan) {
        this.dispatchPlan = dispatchPlan;
    }

    public List<DispatchVehiclePlan> getDispatchPlan() {
        return dispatchPlan;
    }

    public void setDispatchPlan(List<DispatchVehiclePlan> dispatchPlan) {
        this.dispatchPlan = dispatchPlan;
    }
}
