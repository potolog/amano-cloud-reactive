package com.amano.cloud;

import com.amano.cloud.fakedoor.VehicleInProcessor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication(exclude = EmbeddedMongoAutoConfiguration.class)
@EnableDiscoveryClient
@RestController
@ComponentScan
@Configuration
@EnableMongoAuditing
public class VehicleApplication {

    private final Log logger = LogFactory.getLog(getClass());

    @RequestMapping("/health")
    public String health() {
        return "";
    }

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = new SpringApplicationBuilder(VehicleApplication.class).run(args);
    }

    @Bean
    public VehicleInProcessor testDoor() {
        return new VehicleInProcessor();
    }
}
