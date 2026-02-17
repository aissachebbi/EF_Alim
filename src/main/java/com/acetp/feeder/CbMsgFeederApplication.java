package com.acetp.feeder;

import com.acetp.feeder.config.FeederProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(FeederProperties.class)
public class CbMsgFeederApplication {

    public static void main(String[] args) {
        SpringApplication.run(CbMsgFeederApplication.class, args);
    }
}
