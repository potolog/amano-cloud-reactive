package com.amano.cloud.model;

import java.util.HashMap;
import java.util.Map;

public class ParkingGuidePlate {

    public ParkingGuidePlate() {
        this.floorMap = new HashMap<>();
    }

    private Map<String, ParkingGuideFloor> floorMap;

    public Map<String, ParkingGuideFloor> getFloorMap() {
        return floorMap;
    }

    public void setFloorMap(Map<String, ParkingGuideFloor> floorMap) {
        this.floorMap = floorMap;
    }

    public void addStatus(ParkingStatus parkingStatus) {
        String floor = "F" + parkingStatus.getFloor();
        if (!this.floorMap.containsKey(floor)) {
            this.floorMap.put(floor, new ParkingGuideFloor());
        }

        ParkingGuideFloor parkingGuideFloor = this.floorMap.get(floor);
        Map<String, VehicleInout> sectorMap = parkingGuideFloor.getSectorMap();
        String sector = parkingStatus.getParkingSector();

        sectorMap.put(sector, parkingStatus.getVehicleInout());
    }
}
