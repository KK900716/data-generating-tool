package com.sics.tool;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main.
 *
 * @author Ljc
 */
@Slf4j
@SpringBootApplication
public class Main {
  public static void main(String[] args) {
    ((App) SpringApplication.run(Main.class, args).getBean("app")).start();
  }
}
