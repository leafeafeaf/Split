package com.ssafy.Split;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SplitApplication {

  public static void main(String[] args) {
    SpringApplication.run(SplitApplication.class, args);
  }

}
