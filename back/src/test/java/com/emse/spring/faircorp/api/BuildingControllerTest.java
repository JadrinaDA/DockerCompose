package com.emse.spring.faircorp.api;

import com.emse.spring.faircorp.dao.BuildingDao;
import com.emse.spring.faircorp.dao.HeaterDao;
import com.emse.spring.faircorp.dao.RoomDao;
import com.emse.spring.faircorp.dao.WindowDao;
import com.emse.spring.faircorp.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BuildingController.class)
class BuildingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private RoomDao roomDao;

    @MockBean
    private BuildingDao buildingDao;

    @MockBean
    private WindowDao windowDao;

    @MockBean
    private HeaterDao heaterDao;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldLoadBuildings() throws Exception {
        given(buildingDao.findAll()).willReturn(List.of(
                createBuilding("Building 1"),
                createBuilding("Building 2")
        ));

        mockMvc.perform(get("/api/buildings").accept(APPLICATION_JSON))
                // check the HTTP response
                .andExpect(status().isOk())
                // the content can be tested with Json path
                .andExpect(jsonPath("[*].name").value(containsInAnyOrder("Building 1", "Building 2")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldLoadABuilding() throws Exception {
        given(buildingDao.findById(999L)).willReturn(Optional.of(createBuilding("Building 1")));

        mockMvc.perform(get("/api/buildings/999").accept(APPLICATION_JSON))
                // check the HTTP response
                .andExpect(status().isOk())
                // the content can be tested with Json path
                .andExpect(jsonPath("$.name").value("Building 1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateBuilding() throws Exception {
        Building expectedBuilding = createBuilding("Building 1");
        expectedBuilding.setId(null);
        String json = objectMapper.writeValueAsString(new BuildingDto(expectedBuilding));

        given(buildingDao.getReferenceById(anyLong())).willReturn(expectedBuilding);
        given(buildingDao.save(any())).willReturn(expectedBuilding);

        mockMvc.perform(post("/api/buildings").content(json).contentType(APPLICATION_JSON_VALUE).with(SecurityMockMvcRequestPostProcessors.csrf()))
                // check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Building 1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteBuilding() throws Exception {
        mockMvc.perform(delete("/api/buildings/999").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldOffHeaters() throws Exception {
        // Create the building
        Building building = createBuilding("Building 1");
        // We'll create two rooms
        Room room1 = createRoom("room 1", building);
        Room room2 = createRoom("room 2", building);
        building.setRooms(Set.of(room1, room2));
        // Each of the rooms gets a heater that's off and one that's on
        Heater heater1= createHeater("heater 1", room1);
        heater1.setHeaterStatus(HeaterStatus.ON);
        Heater heater2 = createHeater("heater 2", room1);
        Heater heater3= createHeater("heater 3", room2);
        heater3.setHeaterStatus(HeaterStatus.ON);
        Heater heater4 = createHeater("heater 4", room2);
        room1.setHeaters(Arrays.asList(heater1, heater2));
        room2.setHeaters(Arrays.asList(heater3, heater4));
        Assertions.assertThat(heater1.getHeaterStatus()).isEqualTo(HeaterStatus.ON);

        given(buildingDao.findById(999L)).willReturn(Optional.of(building));

        mockMvc.perform(put("/api/buildings/999/offHeaters").accept(APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf()))
                // check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("[*].heaterStatus").value(containsInAnyOrder("OFF", "OFF", "OFF", "OFF")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldCloseWindows() throws Exception {
        // Create the building
        Building building = createBuilding("Building 1");
        // We'll create two rooms
        Room room1 = createRoom("room 1", building);
        Room room2 = createRoom("room 2", building);
        building.setRooms(Set.of(room1, room2));
        // Each room gets two windows
        Window window1 = createWindow("window 1", room1);
        Window window2 = createWindow("window 2", room1);
        Window window3 = createWindow("window 3", room2);
        Window window4 = createWindow("window 4", room2);
        room1.setWindows(Arrays.asList(window1, window2));
        room2.setWindows(Arrays.asList(window3, window4));
        Assertions.assertThat(window1.getWindowStatus()).isEqualTo(WindowStatus.OPEN);

        given(buildingDao.findById(999L)).willReturn(Optional.of(building));

        mockMvc.perform(put("/api/buildings/999/closeWindows").accept(APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf()))
                // check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("[*].windowStatus").value(containsInAnyOrder("CLOSED", "CLOSED", "CLOSED", "CLOSED")));
    }

    private Room createRoom(String name, Building building) {
        return new Room(name, 1, building);
    }

    private Window createWindow(String name, Room room) {
        return new Window(name, WindowStatus.OPEN, room);
    }

    private Heater createHeater(String name, Room room) {
        return new Heater(name, HeaterStatus.OFF, room);
    }

    private Building createBuilding(String name) {
        Building building = new Building(name);
        building.setOutsideTemperature(7.0);
        return building;
    }
}