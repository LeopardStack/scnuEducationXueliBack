package com.scnujxjy.backendpoint;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author leopard
 */
@SpringBootApplication
@EnableScheduling
public class BackEndpointApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackEndpointApplication.class, args);
	}

}
