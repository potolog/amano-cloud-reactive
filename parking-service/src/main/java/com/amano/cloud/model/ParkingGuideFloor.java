package com.amano.cloud.model;

import java.util.HashMap;
import java.util.Map;

public class ParkingGuideFloor {
    public ParkingGuideFloor() {
        this.sectorMap = new HashMap<>();
    }

    private Map<String, VehicleInout> sectorMap;

    public Map<String, VehicleInout> getSectorMap() {
        return sectorMap;
    }

    public void setSectorMap(Map<String, VehicleInout> sectorMap) {
        this.sectorMap = sectorMap;
    }
}
