package com.assignment.dispatch;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class DispatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldAcceptOrdersAndVehiclesAndReturnPlan() throws Exception {
        mockMvc.perform(post("/api/dispatch/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orders\":[{\"orderId\":\"ORD001\",\"latitude\":28.6139,\"longitude\":77.2090,\"address\":\"Connaught Place\",\"packageWeight\":15,\"priority\":\"HIGH\"},{\"orderId\":\"ORD002\",\"latitude\":28.7041,\"longitude\":77.1025,\"address\":\"Karol Bagh\",\"packageWeight\":20,\"priority\":\"LOW\"}] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(post("/api/dispatch/vehicles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"vehicles\":[{\"vehicleId\":\"VEH001\",\"capacity\":100,\"currentLatitude\":28.7041,\"currentLongitude\":77.1025,\"currentAddress\":\"Karol Bagh\"},{\"vehicleId\":\"VEH002\",\"capacity\":80,\"currentLatitude\":28.6139,\"currentLongitude\":77.2090,\"currentAddress\":\"Connaught Place\"}] }"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("success"));

        mockMvc.perform(get("/api/dispatch/plan"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dispatchPlan").isArray())
                .andExpect(jsonPath("$.dispatchPlan[0].vehicleId").exists());
    }

    @Test
    void shouldExposeOpenApiDocumentation() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.info.title").value("Dispatch Load Balancer API"))
                .andExpect(jsonPath("$.paths['/api/dispatch/orders']").exists())
                .andExpect(jsonPath("$.paths['/api/dispatch/vehicles']").exists())
                .andExpect(jsonPath("$.paths['/api/dispatch/orders'].post.requestBody.content['application/json'].examples['Assignment sample'].value.orders[29].orderId").value("ORD030"))
                .andExpect(jsonPath("$.paths['/api/dispatch/vehicles'].post.requestBody.content['application/json'].examples['Assignment sample'].value.vehicles[4].vehicleId").value("VEH005"))
                .andExpect(jsonPath("$.paths['/api/dispatch/plan']").exists());

        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }
}
