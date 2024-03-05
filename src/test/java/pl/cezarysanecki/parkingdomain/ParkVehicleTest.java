package pl.cezarysanecki.parkingdomain;

import jakarta.servlet.ServletException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    @Test
    void failWhenParkingSpotIsOccupiedByOtherCar() throws Exception {
        Vehicle vehicle1 = vehicleService.create(VehicleType.CAR);
        Vehicle vehicle2 = vehicleService.create(VehicleType.CAR);
        ParkingSpot parkingSpot = parkingSpotService.create();

        mockMvc.perform(post("/vehicle/" + vehicle1.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());

        ServletException exception = assertThrows(
                ServletException.class,
                () -> mockMvc.perform(post("/vehicle/" + vehicle2.getId() + "/park-on/" + parkingSpot.getId())));
        assertEquals("Parking spot is already occupied by car", exception.getCause().getMessage());
    }

    @Test
    void allowToParkTwoMotorcyclesOnOnePlace() throws Exception {
        Vehicle vehicle1 = vehicleService.create(VehicleType.MOTORCYCLE);
        Vehicle vehicle2 = vehicleService.create(VehicleType.MOTORCYCLE);
        ParkingSpot parkingSpot = parkingSpotService.create();

        mockMvc.perform(post("/vehicle/" + vehicle1.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(post("/vehicle/" + vehicle2.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void onlyTwoMotorcyclesCanParkOnOneParkingSpot() throws Exception {
        Vehicle vehicle1 = vehicleService.create(VehicleType.MOTORCYCLE);
        Vehicle vehicle2 = vehicleService.create(VehicleType.MOTORCYCLE);
        Vehicle vehicle3 = vehicleService.create(VehicleType.MOTORCYCLE);
        ParkingSpot parkingSpot = parkingSpotService.create();

        mockMvc.perform(post("/vehicle/" + vehicle1.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(post("/vehicle/" + vehicle2.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        ServletException exception = assertThrows(
                ServletException.class,
                () -> mockMvc.perform(post("/vehicle/" + vehicle3.getId() + "/park-on/" + parkingSpot.getId())));
        assertEquals("Parking spot is already occupied by 2 motorcycles", exception.getCause().getMessage());
    }

    @Test
    void allowToParkThreeBikesOrScootersOnOnePlace() throws Exception {
        Vehicle vehicle1 = vehicleService.create(VehicleType.BIKE);
        Vehicle vehicle2 = vehicleService.create(VehicleType.BIKE);
        Vehicle vehicle3 = vehicleService.create(VehicleType.BIKE);
        ParkingSpot parkingSpot = parkingSpotService.create();

        mockMvc.perform(post("/vehicle/" + vehicle1.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(post("/vehicle/" + vehicle2.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(post("/vehicle/" + vehicle3.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void onlyThreeBikesOrScootersCanParkOnOneParkingSpot() throws Exception {
        Vehicle vehicle1 = vehicleService.create(VehicleType.SCOOTER);
        Vehicle vehicle2 = vehicleService.create(VehicleType.SCOOTER);
        Vehicle vehicle3 = vehicleService.create(VehicleType.SCOOTER);
        Vehicle vehicle4 = vehicleService.create(VehicleType.SCOOTER);
        ParkingSpot parkingSpot = parkingSpotService.create();

        mockMvc.perform(post("/vehicle/" + vehicle1.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(post("/vehicle/" + vehicle2.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        mockMvc.perform(post("/vehicle/" + vehicle3.getId() + "/park-on/" + parkingSpot.getId()))
                .andDo(print())
                .andExpect(status().isOk());
        ServletException exception = assertThrows(
                ServletException.class,
                () -> mockMvc.perform(post("/vehicle/" + vehicle4.getId() + "/park-on/" + parkingSpot.getId())));
        assertEquals("Parking spot is already occupied by 3 bikes or scooters", exception.getCause().getMessage());
    }

    @Test
    void parkAnywhereLogicShouldPlaceTwoMotorcyclesOnTheSameParkingSpot() throws Exception {
        Vehicle vehicle1 = vehicleService.create(VehicleType.MOTORCYCLE);
        Vehicle vehicle2 = vehicleService.create(VehicleType.MOTORCYCLE);
        ParkingSpot parkingSpot = parkingSpotService.create();

        mockMvc.perform(post("/vehicle/" + vehicle1.getId() + "/park-anywhere/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(vehicle1.getId()))
                .andExpect(jsonPath("$.parkingSpotId").value(parkingSpot.getId()));
        mockMvc.perform(post("/vehicle/" + vehicle2.getId() + "/park-anywhere/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.vehicleId").value(vehicle2.getId()))
                .andExpect(jsonPath("$.parkingSpotId").value(parkingSpot.getId()));
    }

}
