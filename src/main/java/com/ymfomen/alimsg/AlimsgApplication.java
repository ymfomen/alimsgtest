package com.ymfomen.alimsg;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;


@SpringBootApplication
@EntityScan(basePackageClasses = {AlimsgApplication.class})
public class AlimsgApplication {

	public static void main(String[] args) {
		SpringApplication.run(AlimsgApplication.class, args);
	}

}
