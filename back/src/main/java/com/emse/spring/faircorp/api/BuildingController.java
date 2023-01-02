package com.emse.spring.faircorp.api;

import com.emse.spring.faircorp.dao.BuildingDao;
import com.emse.spring.faircorp.dao.HeaterDao;
import com.emse.spring.faircorp.dao.RoomDao;
import com.emse.spring.faircorp.dao.WindowDao;
import com.emse.spring.faircorp.model.*;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("api/buildings")
@Transactional
public class BuildingController {

    private final BuildingDao buildingDao;
    private final WindowDao windowDao;
    private final HeaterDao heaterDao;
    private final RoomDao roomDao;

    public BuildingController(BuildingDao buildingDao, WindowDao windowDao, HeaterDao heaterDao, RoomDao roomDao){
        this.buildingDao = buildingDao;
        this.windowDao = windowDao;
        this.heaterDao = heaterDao;
        this.roomDao = roomDao;
    }

    @GetMapping
    public List<BuildingDto> findAll() {
        return buildingDao.findAll().stream().map(BuildingDto::new).collect(Collectors.toList());
    }

    @GetMapping(path = "/{id}")
    public BuildingDto findById(@PathVariable Long id) {
        return buildingDao.findById(id).map(BuildingDto::new).orElse(null);
    }

    @PostMapping
    public BuildingDto create(@RequestBody BuildingDto dto) {
        // Building doesn't need to have rooms at start
        Building building = null;
        // Check if it exists or not to see if we create or update
        if (dto.getId()==null) {
            building = buildingDao.save(new Building(dto.getName()));
        }
        else {
            building = buildingDao.getReferenceById(dto.getId());
        }
        if (dto.getOutsideTemperature() != null)
        {
            // Only thing that can be updated is outside temp
            building.setOutsideTemperature(dto.getOutsideTemperature());
        }
        return new BuildingDto(building);
    }

    @DeleteMapping(path = "/{id}")
    public void delete(@PathVariable Long id) {
        buildingDao.deleteById(id);
    }

    @PutMapping(path = "/{id}/offHeaters")
    public List<HeaterDto> offHeaters(@PathVariable Long id) {
        Building building = buildingDao.findById(id).orElseThrow(IllegalArgumentException::new);
        List<HeaterDto> heaterDtos = new ArrayList<>();
        for(Room room: building.getRooms())
        {
            for(Heater heater : room.getHeaters())
            {
                heater.setHeaterStatus(HeaterStatus.OFF);
                heaterDtos.add(new HeaterDto(heater));
            }
        }
        return heaterDtos;
    }

    @PutMapping(path = "/{id}/closeWindows")
    public List<WindowDto> closeWindows(@PathVariable Long id) {
        Building building = buildingDao.findById(id).orElseThrow(IllegalArgumentException::new);
        List<WindowDto> windows = new ArrayList<>();
        for(Room room: building.getRooms())
        {
            for(Window window : room.getWindows())
            {
                window.setWindowStatus(WindowStatus.CLOSED);
                windows.add(new WindowDto(window));
            }
        }
        return windows;
    }


}
