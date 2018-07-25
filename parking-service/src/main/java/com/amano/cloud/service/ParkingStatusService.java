package com.amano.cloud.service;

import com.amano.cloud.model.ParkingStatus;
import com.amano.cloud.model.VehicleInout;
import com.amano.cloud.repository.ParkingStatusRepository;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@Service
public class ParkingStatusService {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    ParkingStatusRepository parkingStatusRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    public Mono<ParkingStatus> updateParkingStatus(ParkingStatus parkingStatus) {
        Assert.notNull(parkingStatus.getFloor(), "Floor required.");
        Assert.notNull(parkingStatus.getParkingSector(), "ParkingSector required.");

        Map map = new HashMap();
        if (parkingStatus.getLicense() != null) {
            map = webClientBuilder.build().get().uri("http://vehicle-service/vehicle/{license}/parking", parkingStatus.getLicense())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .defaultIfEmpty(new HashMap())
                    .block();
        }
        ParkingStatus existStatus = parkingStatusRepository
                .findByFloorAndParkingSector(parkingStatus.getFloor(), parkingStatus.getParkingSector())
                .log()
                .defaultIfEmpty(new ParkingStatus())
                .block();

        if (!map.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            parkingStatus.setVehicleInout(objectMapper.convertValue(map, VehicleInout.class));
        }

        //update if ParkingStatus exist
        if (existStatus.getId() != null) {
            logger.info("update exist parking status");
            parkingStatus.setId(existStatus.getId());
            return parkingStatusRepository.save(parkingStatus);
        } else {
            logger.info("create new parking status");
            return parkingStatusRepository.save(parkingStatus);
        }
    }
}
