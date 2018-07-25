package com.amano.cloud.controller;

import com.amano.cloud.model.VehicleInout;
import com.amano.cloud.repository.VehicleInoutRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RestController
public class VehicleInoutController {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
            //이 레파지토리는 Rest api 로만 통신하고 절대 직접 참조하지 말것.
    VehicleInoutRepository vehicleInoutRepository;

//    @GetMapping("/vehicle")
//    public Flux<VehicleInout> getAllVehicleInout(@PageableDefault Pageable pageable) {
//        return vehicleInoutRepository.findAllPage(pageable);
//    }

    @GetMapping("/vehicle")
    public Flux<VehicleInout> getAllVehicleInout() {
        return vehicleInoutRepository.findAll();
    }

    @PostMapping("/vehicle")
    public Mono<VehicleInout> createVehicleIn(@Valid @RequestBody VehicleInout vehicleInout) {
        return vehicleInoutRepository.findByLicenseAndOutDateNull(vehicleInout.getLicense())
                .log()
                .defaultIfEmpty(new VehicleInout())
                .flatMap(exist -> {
                    if (exist.getId() == null) {
                        return vehicleInoutRepository.save(vehicleInout);
                    } else {
                        throw new NotOutBoundedVehicleException(vehicleInout.getLicense());
                    }
                });
    }

    @GetMapping("/vehicle/{license}/parking")
    public Mono<ResponseEntity<VehicleInout>> getParkingVehicleByLicense(@PathVariable(value = "license") String license) {
        return vehicleInoutRepository.findByLicenseAndOutDateNull(license)
                .log()
                .map(vehicleInout -> ResponseEntity.ok(vehicleInout))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/vehicle/{license}")
    public Flux<VehicleInout> getVehicleInoutByLicense(@PathVariable(value = "license") String license) {
        return vehicleInoutRepository.findByLicense(license).log();
    }

    @PutMapping("/vehicle/{license}")
    public Mono<VehicleInout> updateVehicleOut(@PathVariable(value = "license") String license) {
        return vehicleInoutRepository.findByLicenseAndOutDateNull(license)
                .log()
                .defaultIfEmpty(new VehicleInout())
                .flatMap(exist -> {
                    if (exist.getId() != null) {
                        exist.setOutDate(new Date());
                        return vehicleInoutRepository.save(exist);
                    } else {
                        throw new NotInBoundedVehicleException(license);
                    }
                });
    }

    @DeleteMapping("/vehicle/{id}")
    public Mono<ResponseEntity<Void>> deleteVehicleInout(@PathVariable(value = "id") String id) {

        return vehicleInoutRepository.findById(id)
                .log()
                .flatMap(existingInout ->
                        vehicleInoutRepository.delete(existingInout)
                                .then(Mono.just(new ResponseEntity<Void>(HttpStatus.OK)))
                )
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // VehicleInout are Sent to the client as Server Sent Events
    @GetMapping(value = "/vehicle/stream/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<VehicleInout> streamAllVehicles() {
        return vehicleInoutRepository.findAll();
    }

    @GetMapping(value = "/vehicle/stream/sync")
    public List<VehicleInout> syncAllVehicles() {
        List<VehicleInout> list = vehicleInoutRepository.findAll()
                .collectList()
                .block();
        return list;
    }


    public class NotOutBoundedVehicleException extends RuntimeException {
        public NotOutBoundedVehicleException(String license) {
            super("Vehicle not out bounded " + license);
        }
    }

    public class NotInBoundedVehicleException extends RuntimeException {
        public NotInBoundedVehicleException(String license) {
            super("Vehicle not in bounded " + license);
        }
    }

    @ExceptionHandler({NotOutBoundedVehicleException.class, NotInBoundedVehicleException.class})
    public ResponseEntity handleVehicleException(RuntimeException ex) {
        logger.error(ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
