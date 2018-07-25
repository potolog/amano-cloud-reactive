package com.amano.cloud.repository;

import com.amano.cloud.model.ParkingStatus;
import com.amano.cloud.model.VehicleInout;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ParkingStatusRepository extends ReactiveCrudRepository<ParkingStatus, String> {

    @Query("{}")
    Flux<ParkingStatus> findAllPage(Pageable pageable);

    Mono<ParkingStatus> findByFloorAndParkingSector(int floor, String parkingSector);

    Flux<ParkingStatus> findByLicense(String license);

    Flux<ParkingStatus> findByFloor(String floor);
}
