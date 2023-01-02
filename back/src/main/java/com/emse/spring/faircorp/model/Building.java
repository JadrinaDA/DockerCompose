package com.emse.spring.faircorp.model;

import javax.persistence.*;
import java.util.Set;

@Entity
public class Building {
    // ID
    @Id
    @GeneratedValue
    private Long id;

    // Name
    @Column(nullable = false)
    private String name;

    // Outside temperature
    private Double outsideTemperature;

    @OneToMany(mappedBy = "building")
    private Set<Room> rooms;

    // Empty constructor
    public Building() {
    }

    public Building(String name) {
        this.name = name;
    }

    // Getters and setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getOutsideTemperature() {
        return outsideTemperature;
    }

    public void setOutsideTemperature(Double outsideTemperature) {
        this.outsideTemperature = outsideTemperature;
    }

    public Set<Room> getRooms() {
        return rooms;
    }

    public void setRooms(Set<Room> rooms) {
        this.rooms = rooms;
    }
}
