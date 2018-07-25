package com.amano.cloud.fakedoor;

import com.amano.cloud.model.VehicleInout;
import com.amano.cloud.repository.VehicleInoutRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.integration.annotation.InboundChannelAdapter;
import org.springframework.integration.annotation.Poller;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.*;

@EnableBinding(Door.class)
public class VehicleInProcessor {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    VehicleInoutRepository vehicleInoutRepository;

    public static String getRandomChar() {
        String chars = "ABC";
        Random rnd = new Random();
        char c = chars.charAt(rnd.nextInt(chars.length()));
        return String.valueOf(c);
    }

    @InboundChannelAdapter(channel = Door.OUTPUT, poller = @Poller(fixedDelay = "1000"))
    public String sendVehicleInMessage() throws Exception {

        //create random license vehicles
        Random r = new Random();
        int Low = 1;
        int High = 1000;
        int result = r.nextInt(High - Low) + Low;

        VehicleInout vehicleInout = new VehicleInout();
        vehicleInout.setLicense("AMANO-86-" + result);
        vehicleInout.setColor(getRandomChar());
        vehicleInout.setVehicleMaker(getRandomChar());
        vehicleInout.setVehicleModel(getRandomChar());
        vehicleInout.setVehicleType(getRandomChar());

        String s = new ObjectMapper().writeValueAsString(vehicleInout);
        logger.info("Send : " + s);
        return s;
    }

    @StreamListener
    public void receiveVehicleInMessage(@Input(Door.INPUT) Flux<String> inbound) {
        inbound
                .log()
                .subscribeOn(Schedulers.single())
                .subscribe(value -> {
                    try {
                        VehicleInout vehicleInout = new ObjectMapper().readValue(value, VehicleInout.class);
                        this.addNewVehicle(vehicleInout);
                    } catch (Exception ex) {
                        throw new RuntimeException("Conversation failed");
                    }
                }, error -> System.err.println("CAUGHT " + error));
    }

    public void addNewVehicle(VehicleInout vehicleInout) {
        vehicleInoutRepository.findByLicenseAndOutDateNull(vehicleInout.getLicense())
                .log()
                .defaultIfEmpty(new VehicleInout())
                .subscribeOn(Schedulers.single())
                .subscribe(exist -> {
                            if (exist.getId() == null) {
                                try {
                                    vehicleInoutRepository.save(vehicleInout)
                                            .subscribeOn(Schedulers.single())
                                            .subscribe();
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            } else {
                                logger.warn("Vehicle not out bounded " + vehicleInout.getLicense());
                            }
                        },
                        error -> System.err.println("CAUGHT " + error)
                );
    }

}


