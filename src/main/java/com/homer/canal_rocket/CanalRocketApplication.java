package com.homer.canal_rocket;

import com.homer.canal_rocket.config.StartupListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CanalRocketApplication {

  public static void main(String[] args) {
    SpringApplication springApp = new SpringApplication(CanalRocketApplication.class);
    springApp.addListeners(new StartupListener());
    springApp.run(args);
  }

}
