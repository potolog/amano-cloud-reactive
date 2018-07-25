package com.amano.cloud.service;

import com.amano.cloud.model.ParkingGuidePlate;
import com.amano.cloud.repository.ParkingStatusRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Service
public class ParkingGuidePlateService implements InitializingBean {

    private final Log logger = LogFactory.getLog(getClass());

    private ParkingGuidePlate parkingGuidePlate = new ParkingGuidePlate();

    public ParkingGuidePlate getParkingGuidePlate() {
        return parkingGuidePlate;
    }

    @Autowired
    ParkingStatusRepository parkingStatusRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        Flux.interval(Duration.ofSeconds(3))
                .subscribe(i -> {
                    parkingStatusRepository.findAll()
                            .subscribeOn(Schedulers.single())
                            .subscribe(parkingStatus -> {
                                parkingGuidePlate.addStatus(parkingStatus);
                            }, error -> System.err.println("CAUGHT " + error));
                });
    }
}
