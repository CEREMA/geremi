package fr.cerema.dsi.geremi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"fr.cerema.dsi.commons", "fr.cerema.dsi.geremi"})
public class App {

  public static void main(String[] args) {
    SpringApplication.run(App.class, args);
  }

}


