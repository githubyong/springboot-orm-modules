package org.example.sdj.mutidb;

import org.example.sdj.mutidb.config.EnableDbConfig;
import org.example.sdj.mutidb.config.OrderDbConfig;
import org.example.sdj.mutidb.config.UserDbConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDbConfig({UserDbConfig.class, OrderDbConfig.class})
@SpringBootApplication
public class SdjMutidbApplication {

    public static void main(String[] args) {
        SpringApplication.run(SdjMutidbApplication.class, args);
    }

}
