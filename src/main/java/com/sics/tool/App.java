package com.sics.tool;

import com.sics.tool.task.TaskConfig;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
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

  @Value("${parameter.size.parallel}")
  private int parallel;

  @Value("${parameter.size.batch}")
  private int batch;

  @Value("${parameter.size.transaction}")
  private int transaction;

  public void start(ConfigurableApplicationContext context) throws Exception {
    log.info("start application!");
    setDeamon();
    for (int i = 1; i <= batch; i++) {
      List<Future<Long>> execute = execute();
      analyse(execute, i);
    }
    log.info("wait finish!");
  }

  private void analyse(List<Future<Long>> execute, int i)
      throws ExecutionException, InterruptedException {
    long count = 0;
    for (Future<Long> longFuture : execute) {
      Long ri = longFuture.get();
      if (ri > count) {
        count = ri;
      }
    }
    double res = (parallel * transaction) * 1.0 / (count * 1000);
    log.info("第{}批次(batch)，插入速度为 {} 行/s", i, (int) res);
  }

  private List<Future<Long>> execute() {
    List<Future<Long>> res = new ArrayList<>();
    for (int i = 0; i < parallel; i++) {
      Future<Long> submit = pool.submit(taskConfig.newInsertTask());
      res.add(submit);
    }
    return res;
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
