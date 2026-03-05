package com.example.demo.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Stop {

    @Id
    private String stopId;
    private String stopName;

    public String getStopId() { return stopId; }
    public void setStopId(String stopId) { this.stopId = stopId; }

    public String getStopName() { return stopName; }
    public void setStopName(String stopName) { this.stopName = stopName; }
}