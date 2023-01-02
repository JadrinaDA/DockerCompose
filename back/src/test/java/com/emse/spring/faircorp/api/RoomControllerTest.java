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

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RoomController.class)
class RoomControllerTest {

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
    void shouldLoadRooms() throws Exception {
        given(roomDao.findAll()).willReturn(List.of(
                createRoom("room 1"),
                createRoom("room 2")
        ));

        mockMvc.perform(get("/api/rooms").accept(APPLICATION_JSON))
                // check the HTTP response
                .andExpect(status().isOk())
                // the content can be tested with Json path
                .andExpect(jsonPath("[*].name").value(containsInAnyOrder("room 1", "room 2")));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldLoadARoom() throws Exception {
        given(roomDao.findById(999L)).willReturn(Optional.of(createRoom("room 1")));

        mockMvc.perform(get("/api/rooms/999").accept(APPLICATION_JSON))
                // check the HTTP response
                .andExpect(status().isOk())
                // the content can be tested with Json path
                .andExpect(jsonPath("$.name").value("room 1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateRoom() throws Exception {
        Room expectedRoom = createRoom("room 1");
        expectedRoom.setId(null);
        String json = objectMapper.writeValueAsString(new RoomDto(expectedRoom));

        given(roomDao.getReferenceById(anyLong())).willReturn(expectedRoom);
        given(roomDao.save(any())).willReturn(expectedRoom);

        mockMvc.perform(post("/api/rooms").content(json).contentType(APPLICATION_JSON_VALUE).with(SecurityMockMvcRequestPostProcessors.csrf()))
                // check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("room 1"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteRoom() throws Exception {
        mockMvc.perform(delete("/api/rooms/999").with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldSwitchWindows() throws Exception {
        Room expectedRoom = createRoom("room 1");
        Window expectedWindow = createWindow("window 1", expectedRoom);
        expectedRoom.setWindows(Arrays.asList(expectedWindow));
        Assertions.assertThat(expectedWindow.getWindowStatus()).isEqualTo(WindowStatus.OPEN);

        given(roomDao.findById(999L)).willReturn(Optional.of(expectedRoom));

        mockMvc.perform(put("/api/rooms/999/switchWindows").accept(APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf()))
                // check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("[*].windowStatus").value(containsInAnyOrder("CLOSED")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldSwitchHeaters() throws Exception {
        Room expectedRoom = createRoom("room 1");
        Heater expectedHeater= createHeater("heater 1", expectedRoom);
        expectedRoom.setHeaters(Arrays.asList(expectedHeater));
        Assertions.assertThat(expectedHeater.getHeaterStatus()).isEqualTo(HeaterStatus.OFF);

        given(roomDao.findById(999L)).willReturn(Optional.of(expectedRoom));

        mockMvc.perform(put("/api/rooms/999/switchHeaters").accept(APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf()))
                // check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("[*].heaterStatus").value(containsInAnyOrder("ON")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldOffHeaters() throws Exception {
        Room expectedRoom = createRoom("room 1");
        Heater expectedHeater= createHeater("heater 1", expectedRoom);
        expectedHeater.setHeaterStatus(HeaterStatus.ON);
        Heater heater2 = createHeater("heater 2", expectedRoom);
        expectedRoom.setHeaters(Arrays.asList(expectedHeater, heater2));
        Assertions.assertThat(expectedHeater.getHeaterStatus()).isEqualTo(HeaterStatus.ON);

        given(roomDao.findById(999L)).willReturn(Optional.of(expectedRoom));

        mockMvc.perform(put("/api/rooms/999/offHeaters").accept(APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf()))
                // check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("[*].heaterStatus").value(containsInAnyOrder("OFF", "OFF")));
    }

    @Test
    @WithMockUser(username = "admin", authorities = "ADMIN")
    void shouldCloseWindows() throws Exception {
        Room expectedRoom = createRoom("room 1");
        Window window1 = createWindow("window 1", expectedRoom);
        Window window2 = createWindow("window 2", expectedRoom);
        expectedRoom.setWindows(Arrays.asList(window1, window2));
        Assertions.assertThat(window1.getWindowStatus()).isEqualTo(WindowStatus.OPEN);

        given(roomDao.findById(999L)).willReturn(Optional.of(expectedRoom));

        mockMvc.perform(put("/api/rooms/999/closeWindows").accept(APPLICATION_JSON).with(SecurityMockMvcRequestPostProcessors.csrf()))
                // check the HTTP response
                .andExpect(status().isOk())
                .andExpect(jsonPath("[*].windowStatus").value(containsInAnyOrder("CLOSED", "CLOSED")));
    }

    private Room createRoom(String name) {
        Building building = new Building("Building 1");
        return new Room(name, 1, building);
    }

    private Window createWindow(String name, Room room) {
        return new Window(name, WindowStatus.OPEN, room);
    }

    private Heater createHeater(String name, Room room) {
        return new Heater(name, HeaterStatus.OFF, room);
    }
}