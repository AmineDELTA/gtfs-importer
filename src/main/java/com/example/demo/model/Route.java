package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "routes")
public class Route {
    
    @Id
    @Column(name = "route_id")
    private String id;

    @Column(name = "route_short_name")
    private String shortName;

    @Column(name = "route_long_name")
    private String longName;

    @Column(name = "route_type")
    private Integer type;

    //empty constructor for Hibernate to work
    public Route() {}

    //constructor to use when parsing the CSV
    public Route(String id, String shortName, String longName, Integer type) {
        this.id = id;
        this.shortName = shortName;
        this.longName = longName;
        this.type = type;
    }

    //getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getShortName() { return shortName; }
    public void setShortName(String shortName) { this.shortName = shortName; }

    public String getLongName() { return longName; }
    public void setLongName(String longName) { this.longName = longName; }
    
    public Integer getType() { return type; }
    public void setType(Integer type) { this.type = type; }
}
