package pl.cezarysanecki.parkingdomain;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import pl.cezarysanecki.parkingdomain.model.ParkingSpot;
import pl.cezarysanecki.parkingdomain.model.Vehicle;
import pl.cezarysanecki.parkingdomain.model.VehicleType;
import pl.cezarysanecki.parkingdomain.service.ParkingSpotService;
import pl.cezarysanecki.parkingdomain.service.VehicleService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ParkVehicleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ParkingSpotService parkingSpotService;
    @Autowired
    private VehicleService vehicleService;


    @Test
    void shouldParkVehicleAnywhere() throws Exception {
        Vehicle vehicle = vehicleService.create(VehicleType.CAR);
        ParkingSpot parkingSpot = parkingSpotService.create();

        mockMvc.perform(post("/vehicle/" + vehicle.getId() + "/park-anywhere/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(vehicle.getId()))
                .andExpect(jsonPath("$.parkingSpotId").value(parkingSpot.getId()));
    }

}
