package com.assignment.dispatch.controller;

import com.assignment.dispatch.OpenApiExamples;
import com.assignment.dispatch.model.ApiResponse;
import com.assignment.dispatch.model.DeliveryOrdersRequest;
import com.assignment.dispatch.model.DispatchPlanResponse;
import com.assignment.dispatch.model.VehicleRequest;
import com.assignment.dispatch.service.DispatchService;
import jakarta.validation.Valid;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dispatch")
@Tag(name = "Dispatch", description = "Submit orders and vehicles, then retrieve the dispatch plan")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @PostMapping("/orders")
    @Operation(
            summary = "Submit delivery orders",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "All 30 delivery orders from the assignment sample.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Assignment sample",
                                    summary = "Full 30-order Delhi NCR sample",
                                    value = OpenApiExamples.ASSIGNMENT_ORDERS))))
    public ResponseEntity<ApiResponse> acceptOrders(@Valid @RequestBody DeliveryOrdersRequest request) {
        dispatchService.setOrders(request.getOrders());
        return ResponseEntity.ok(new ApiResponse("Delivery orders accepted.", "success"));
    }

    @PostMapping("/vehicles")
    @Operation(
            summary = "Submit fleet vehicles",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "All five vehicles from the assignment sample.",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "Assignment sample",
                                    summary = "Full five-vehicle Delhi NCR sample",
                                    value = OpenApiExamples.ASSIGNMENT_VEHICLES))))
    public ResponseEntity<ApiResponse> acceptVehicles(@Valid @RequestBody VehicleRequest request) {
        dispatchService.setVehicles(request.getVehicles());
        return ResponseEntity.ok(new ApiResponse("Vehicle details accepted.", "success"));
    }

    @GetMapping("/plan")
    @Operation(summary = "Retrieve the dispatch plan")
    public ResponseEntity<DispatchPlanResponse> getDispatchPlan() {
        return ResponseEntity.ok(new DispatchPlanResponse(dispatchService.generateDispatchPlan()));
    }
}
