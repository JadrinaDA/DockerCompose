package com.emse.spring.faircorp.api;

import com.emse.spring.faircorp.dao.BuildingDao;
import com.emse.spring.faircorp.dao.HeaterDao;
import com.emse.spring.faircorp.dao.RoomDao;
import com.emse.spring.faircorp.dao.WindowDao;
import com.emse.spring.faircorp.model.*;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/rooms")
@Transactional
public class RoomController {
    private final RoomDao roomDao;
    private final BuildingDao buildingDao;
    private final WindowDao windowDao;
    private final HeaterDao heaterDao;

    public RoomController(RoomDao roomDao, BuildingDao buildingDao, WindowDao windowDao, HeaterDao heaterDao) {
        this.roomDao = roomDao;
        this.buildingDao = buildingDao;
        this.windowDao = windowDao;
        this.heaterDao = heaterDao;
    }

    @GetMapping
    public List<RoomDto> findAll() {
        return roomDao.findAll().stream().map(RoomDto::new).collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public RoomDto findById(@PathVariable Long id) {
        return roomDao.findById(id).map(RoomDto::new).orElse(null);
    }

    @PostMapping
    public RoomDto create(@RequestBody RoomDto dto) {
        Room room = null;
        Building building = buildingDao.getReferenceById(dto.getBuildingId());
        // When created the id isn't defined
        if (dto.getId() == null) {
            room = roomDao.save(new Room(dto.getName(), dto.getFloor(), building));
        }
        else {
            room = roomDao.getReferenceById(dto.getId());
        }
        if (dto.getTargetTemperature() != null)
        {
            room.setTargetTemperature(dto.getTargetTemperature());
        }
        return new RoomDto(room);
    }

    @PutMapping(path = "/{id}/switchWindows")
    public List<WindowDto> switchWindows(@PathVariable Long id) {
        Room room = roomDao.findById(id).orElseThrow(IllegalArgumentException::new);
        List<WindowDto> windows = new ArrayList<>();
        for(Window window : room.getWindows())
        {
            window.setWindowStatus(window.getWindowStatus() == WindowStatus.OPEN ? WindowStatus.CLOSED : WindowStatus.OPEN);
            windows.add(new WindowDto(window));
        }

        return windows;
    }

    @PutMapping(path = "/{id}/switchHeaters")
    public List<HeaterDto> switchHeaters(@PathVariable Long id) {
        Room room = roomDao.findById(id).orElseThrow(IllegalArgumentException::new);
        List<HeaterDto> heaterDtos = new ArrayList<>();
        for(Heater heater : room.getHeaters())
        {
            heater.setHeaterStatus(heater.getHeaterStatus() == HeaterStatus.ON ? HeaterStatus.OFF : HeaterStatus.ON);
            heaterDtos.add(new HeaterDto(heater));
        }
        return heaterDtos;
    }

    @PutMapping(path = "/{id}/offHeaters")
    public List<HeaterDto> offHeaters(@PathVariable Long id) {
        Room room = roomDao.findById(id).orElseThrow(IllegalArgumentException::new);
        List<HeaterDto> heaterDtos = new ArrayList<>();
        for(Heater heater : room.getHeaters())
        {
            heater.setHeaterStatus(HeaterStatus.OFF);
            heaterDtos.add(new HeaterDto(heater));
        }
        return heaterDtos;
    }

    @PutMapping(path = "/{id}/closeWindows")
    public List<WindowDto> closeWindows(@PathVariable Long id) {
        Room room = roomDao.findById(id).orElseThrow(IllegalArgumentException::new);
        List<WindowDto> windows = new ArrayList<>();
        for(Window window : room.getWindows())
        {
            window.setWindowStatus(WindowStatus.CLOSED);
            windows.add(new WindowDto(window));
        }

        return windows;
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        windowDao.deleteWindows(id);
        heaterDao.deleteByRoom(id);
        roomDao.deleteById(id);
    }

}