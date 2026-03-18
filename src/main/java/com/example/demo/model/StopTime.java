package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "stop_times")
public class StopTime {
    
    @ManyToOne
    private Trip trip;

    @ManyToOne  
    private Stop stop;

    @Column(name = "arrival_time")
    private String arrivalTime;

    @Column(name = "departure_time")
    private String departureTime;

    @Column(name = "stop_sequence")
    private Integer stopSequence;

    //empty constructor for Hibernate to work
    public StopTime() {}

    //constructor to use when parsing the CSV
    public StopTime(Trip trip, Stop stop, String arrivalTime, String departureTime, Integer stopSequence) {
        this.trip = trip;
        this.stop = stop;
        this.arrivalTime = arrivalTime;
        this.departureTime = departureTime;
        this.stopSequence = stopSequence;
    }

    //getters and Setters
    public Trip getTrip() { return trip; }
    public void setTrip(Trip trip) { this.trip = trip; }
    public Stop getStop() { return stop; }
    public void setStop(Stop stop) { this.stop = stop; }
    public String getArrivalTime() { return arrivalTime; }
    public void setArrivalTime(String arrivalTime) { this.arrivalTime = arrivalTime; }
    public String getDepartureTime() { return departureTime; }
    public void setDepartureTime(String departureTime) { this.departureTime = departureTime; }
    public Integer getStopSequence() { return stopSequence; }
    public void setStopSequence(Integer stopSequence) { this.stopSequence = stopSequence; }
    
}