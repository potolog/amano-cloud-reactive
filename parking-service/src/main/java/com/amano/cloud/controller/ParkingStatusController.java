package com.amano.cloud.controller;

import com.amano.cloud.model.ParkingStatus;
import com.amano.cloud.model.VehicleInout;
import com.amano.cloud.repository.ParkingStatusRepository;
import com.amano.cloud.service.ParkingStatusService;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
public class ParkingStatusController {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    ParkingStatusService parkingStatusService;

    @Autowired
    ParkingStatusRepository parkingStatusRepository;


//    @GetMapping("/parking")
//    public Flux<ParkingStatus> getAllParkingStatus(@PageableDefault Pageable pageable) {
//        return parkingStatusRepository.findAllPage(pageable);
//    }

    @GetMapping("/parking")
    public Flux<ParkingStatus> getAllParkingStatus() {
        return parkingStatusRepository.findAll();
    }

    @PostMapping("/parking")
    public Mono<ParkingStatus> updateParkingStatus(@Valid @RequestBody ParkingStatus parkingStatus) {
        return parkingStatusService.updateParkingStatus(parkingStatus);
    }

    @GetMapping("/parking/{id}")
    public Mono<ParkingStatus> getParkingStatusById(@PathVariable(value = "id") String id) {
        return parkingStatusRepository.findById(id).log();
    }

    @DeleteMapping("/parking/{id}")
    public Mono<ResponseEntity<Void>> deleteParkingStatus(@PathVariable(value = "id") String id) {
        return parkingStatusRepository.findById(id)
                .log()
                .flatMap(existingStatus ->
                        parkingStatusRepository.delete(existingStatus)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // VehicleInout are Sent to the client as Server Sent Events
    @GetMapping(value = "/parking/stream/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ParkingStatus> streamAllVehicles() {
        return parkingStatusRepository.findAll();
    }
}
