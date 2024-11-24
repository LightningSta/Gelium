package org.example.files;

import org.example.files.Logic.FilesLogic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@EnableDiscoveryClient
@CrossOrigin
public class FilesApplication {

    public static void main(String[] args) {
        FilesLogic.clearCache();
        SpringApplication.run(FilesApplication.class, args);
    }

}
