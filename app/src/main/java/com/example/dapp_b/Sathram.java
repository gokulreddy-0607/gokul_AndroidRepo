package com.example.dapp_b;

import java.io.Serializable;
import java.util.List;

public class Sathram implements Serializable {
    private String name;
    private String year;
    private String startCost;
    private String endCost;
    private Double lat;
    private Double lng;
    private String busDistance;
    private String railDistance;
    private String description;
    private String website;
    private String phone;
    private Double rating;
    private boolean acRooms;
    private boolean nonAcRooms;
    private boolean hotWater;
    private boolean parking;
    private String stayType;
    private String checkInTime;
    private String checkOutTime;
    private List<String> imageUrls;
    private String key;

    // Public no-argument constructor is required for Firebase deserialization
    public Sathram() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getStartCost() {
        return startCost;
    }

    public void setStartCost(String startCost) {
        this.startCost = startCost;
    }

    public String getEndCost() {
        return endCost;
    }

    public void setEndCost(String endCost) {
        this.endCost = endCost;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getBusDistance() {
        return busDistance;
    }

    public void setBusDistance(String busDistance) {
        this.busDistance = busDistance;
    }

    public String getRailDistance() {
        return railDistance;
    }

    public void setRailDistance(String railDistance) {
        this.railDistance = railDistance;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public boolean isAcRooms() {
        return acRooms;
    }

    public void setAcRooms(boolean acRooms) {
        this.acRooms = acRooms;
    }

    public boolean isNonAcRooms() {
        return nonAcRooms;
    }

    public void setNonAcRooms(boolean nonAcRooms) {
        this.nonAcRooms = nonAcRooms;
    }

    public boolean isHotWater() {
        return hotWater;
    }

    public void setHotWater(boolean hotWater) {
        this.hotWater = hotWater;
    }

    public boolean isParking() {
        return parking;
    }

    public void setParking(boolean parking) {
        this.parking = parking;
    }

    public String getStayType() {
        return stayType;
    }

    public void setStayType(String stayType) {
        this.stayType = stayType;
    }

    public String getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(String checkInTime) {
        this.checkInTime = checkInTime;
    }

    public String getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(String checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}