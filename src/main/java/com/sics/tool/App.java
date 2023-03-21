package com.sics.tool;

import com.sics.tool.task.TaskConfig;
import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * Application.{@link Main#main(String[])}
 *
 * @author liangjc
 */
@Component
@Slf4j
public class App {
  @Resource private ThreadPoolExecutor pool;
  @Resource private TaskConfig taskConfig;

  @Value("${parameter.size.batch}")
  private int batch;

  public void start(ConfigurableApplicationContext context) throws Exception {
    log.info("start application!");
    setDeamon();
    for (int i = 0; i < batch; i++) {
      pool.execute(taskConfig.newInsertTask());
    }
    log.info("finish!");
    pool.shutdown();
    System.exit(0);
  }

  private void setDeamon() {
    pool.execute(
        () -> {
          Scanner scanner = new Scanner(System.in);
          String s = scanner.nextLine();
          if ("q".equals(s)) {
            log.info("finish!");
            pool.shutdown();
            System.exit(0);
          }
        });
  }
}
