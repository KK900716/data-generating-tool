package com.sics.tool;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class App {
  @Resource private ThreadPoolExecutor pool;

  public void start() {
    log.info("start application!");
    for (int i = 0; i < 16; i++) {
      pool.execute(
          () -> {
            try {
              log.info(Thread.currentThread().getName());
              TimeUnit.SECONDS.sleep(1000);
            } catch (InterruptedException e) {
              throw new RuntimeException(e);
            }
          });
    }
    log.info("wait");
  }
}
