package org.ozwillo.dcexporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableCaching
@ComponentScan(basePackages = {"org.oasis_eu.spring", "org.ozwillo.dcexporter"})
public class OzwilloDcExporterApplication {

    public static void main(String[] args) {
        SpringApplication.run(OzwilloDcExporterApplication.class, args);
    }
}
