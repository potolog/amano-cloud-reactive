package com.amano.cloud.controller;

import com.amano.cloud.model.ParkingGuidePlate;
import com.amano.cloud.model.ParkingStatus;
import com.amano.cloud.repository.ParkingStatusRepository;
import com.amano.cloud.service.ParkingGuidePlateService;
import com.amano.cloud.service.ParkingStatusService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

@RestController
public class ParkingGuidePlateController{

    @Autowired
    private ParkingGuidePlateService parkingGuidePlateService;

    private final Log logger = LogFactory.getLog(getClass());

    private final CopyOnWriteArrayList<SseEmitter> emitters = new CopyOnWriteArrayList<>();

    @GetMapping("/parking-guide")
    public SseEmitter handle(HttpServletRequest request,
                             HttpServletResponse response
    ) throws Exception {
        try {

            SseEmitter emitter = new SseEmitter(360_000L); //360 sec.
            emitters.add(emitter);

            emitter.onCompletion(() -> this.emitters.remove(emitter));
            emitter.onTimeout(() -> this.emitters.remove(emitter));

            logger.info("emitter counts: " + emitters.size());

            return emitter;
        } catch (Exception e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unauthorized");
            return null;
        }
    }

    @Scheduled(initialDelay = 1000, fixedDelay = 3000)
    public void emitterSend() throws Exception {
        List<SseEmitter> deadEmitters = new ArrayList<>();
        this.emitters.forEach(emitter -> {
            try {
                emitter.send(parkingGuidePlateService.getParkingGuidePlate());
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });
        emitters.removeAll(deadEmitters);
    }
}
