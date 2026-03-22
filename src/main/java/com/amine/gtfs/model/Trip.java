package com.amine.gtfs.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "trips")
public class Trip {
    
    @Id
    @Column(name = "trip_id")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    @Column(name = "service_id", nullable = false)
    private String serviceId;

    @Column(name = "direction_id")
    private Integer directionId;

    @Column(name = "trip_headsign")
    private String headsign;

    //empty constructor for Hibernate to work
    public Trip() {}

    //constructor to use when parsing the CSV
    public Trip(String id, Route route, String serviceId, Integer directionId, String headsign) {
        this.id = id;
        this.route = route;
        this.serviceId = serviceId;
        this.directionId = directionId;
        this.headsign = headsign;
    }

    //getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public Route getRoute() { return route; }
    public void setRoute(Route route) { this.route = route; }
    public String getServiceId() { return serviceId; }
    public void setServiceId(String serviceId) { this.serviceId = serviceId; }
    public Integer getDirectionId() { return directionId; }
    public void setDirectionId(Integer directionId) { this.directionId = directionId; }
    public String getHeadsign() { return headsign; }
    public void setHeadsign(String headsign) { this.headsign = headsign; }
}
