package com.emse.spring.faircorp.dao;

import com.emse.spring.faircorp.model.Building;
import com.emse.spring.faircorp.model.Heater;
import com.emse.spring.faircorp.model.Room;
import com.emse.spring.faircorp.model.Window;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BuildingDao extends JpaRepository<Building, Long> {

    @Query("select b from Building b where b.id=:id")
    Building findbyId(@Param("id") Long id);

    @Query("select w from Window w where w.room.building.id =:id")
    List<Window> findAllWindows(@Param("id") Long id);

    @Query("select h from Heater h where h.room.building.id =:id")
    List<Heater> findAllHeaters(@Param("id") Long id);
}
