package com.wjw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.wjw.mongo.*")
@ServletComponentScan("com.wjw.mongo.*")
public class RemoteMongoApplication extends SpringBootServletInitializer {

	public static void main(String[] args) {
		SpringApplication.run(RemoteMongoApplication.class, args);
	}

}
