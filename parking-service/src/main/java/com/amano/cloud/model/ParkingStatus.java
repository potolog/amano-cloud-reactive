package com.amano.cloud.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Range;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Collections;
import java.util.Date;

@Document(collection = "parking-status")
@Data
public class ParkingStatus {

    @Id
    private String id;

    @NotBlank
    private String parkingSector;

    @Range(min = 1)
    private int floor;

    private String license;

    private VehicleInout vehicleInout;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    public VehicleInout getVehicleInout() {
        return vehicleInout;
    }

    public void setVehicleInout(VehicleInout vehicleInout) {
        this.vehicleInout = vehicleInout;
    }
}
