package com.sics.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main.
 *
 * @author Ljc
 */
@Slf4j
@SpringBootApplication
public class Main {
  public static void main(String[] args) throws Exception {
    ConfigurableApplicationContext context = SpringApplication.run(Main.class, args);
    context.getBean("app", App.class).start(context);
  }
}
