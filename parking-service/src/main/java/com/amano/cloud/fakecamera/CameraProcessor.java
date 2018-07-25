package com.amano.cloud.fakecamera;

import com.amano.cloud.model.ParkingStatus;
import com.amano.cloud.model.VehicleInout;
import com.amano.cloud.service.ParkingStatusService;
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

import java.util.Random;

@EnableBinding(Camera.class)
public class CameraProcessor {

    private final Log logger = LogFactory.getLog(getClass());

    @Autowired
    ParkingStatusService parkingStatusService;

    public static String getRandomParkingSector() {
        String chars = "ABCDEF";
        Random rnd = new Random();
        char c = chars.charAt(rnd.nextInt(chars.length()));
        String sectorChar = String.valueOf(c);

        Random r = new Random();
        int Low = 1;
        int High = 20;
        int result = r.nextInt(High - Low) + Low;

        return sectorChar + " " + result;
    }

    public static int getRandomFloor() {
        Random r = new Random();
        int Low = 1;
        int High = 5;
        return r.nextInt(High - Low) + Low;
    }

    @InboundChannelAdapter(channel = Camera.OUTPUT, poller = @Poller(fixedDelay = "1000"))
    public String sendAnalysisResult() throws Exception {

        Random r = new Random();
        int Low = 1;
        int High = 1000;
        int result = r.nextInt(High - Low) + Low;

        //create random camera infomation
        ImageAnalysisResult analysisResult = new ImageAnalysisResult();
        analysisResult.setFloor(getRandomFloor());
        analysisResult.setParkingSector(getRandomParkingSector());

        //some sector is empty, and some sector is parking
        if (Math.random() < 0.5) {
            analysisResult.setLicense("AMANO-86-" + result);
        }

        String s = new ObjectMapper().writeValueAsString(analysisResult);
        logger.info("Send : " + s);
        return s;
    }

    @StreamListener
    public void receiveAnalysisResult(@Input(Camera.INPUT) Flux<String> inbound) {
        inbound
                .log()
                .subscribeOn(Schedulers.single())
                .subscribe(value -> {
                    try {
                        ImageAnalysisResult analysisResult = new ObjectMapper().readValue(value, ImageAnalysisResult.class);
                        ParkingStatus parkingStatus = new ParkingStatus();
                        parkingStatus.setLicense(analysisResult.getLicense());
                        parkingStatus.setFloor(analysisResult.getFloor());
                        parkingStatus.setParkingSector(analysisResult.getParkingSector());

                        parkingStatusService.updateParkingStatus(parkingStatus)
                                .subscribeOn(Schedulers.single())
                                .subscribe();

                    } catch (Exception ex) {
                        //throw new RuntimeException("Conversation failed");
                    }
                }, error -> System.err.println("CAUGHT " + error));
    }
}


