package com.amano.cloud.fakecamera;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;

@Data
public class ImageAnalysisResult {
    private String parkingSector;

    private int floor;

    private String license;

    public String getParkingSector() {
        return parkingSector;
    }

    public void setParkingSector(String parkingSector) {
        this.parkingSector = parkingSector;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }
}
