package com.amano.cloud.repository;

import com.amano.cloud.model.VehicleInout;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

public interface VehicleInoutRepository extends ReactiveCrudRepository<VehicleInout, String> {

    @Query("{}")
    Flux<VehicleInout> findAllPage(Pageable pageable);

    Flux<VehicleInout> findByLicense(String license);

    Mono<VehicleInout> findByLicenseAndOutDateNull(String license);
}
