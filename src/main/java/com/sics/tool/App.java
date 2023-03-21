package com.sics.tool;

import com.sics.tool.file.ReportUtil;
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
  @Resource private ReportUtil reportUtil;

  @Value("${parameter.size.parallel}")
  private int parallel;

  @Value("${parameter.size.batch}")
  private int batch;

  @Value("${parameter.size.transaction}")
  private int transaction;

  @Value("${parameter.size.proportion}")
  private String proportion;

  public void start(ConfigurableApplicationContext context) throws Exception {
    log.info("start application!");
    setDeamon();
    for (int i = 1; i <= batch; i++) {
      List<Future<Long>> execute = execute(i);
      analyse(execute, i);
      log.info("-".repeat(100));
    }
    reportUtil.store();
    log.info("wait finish!");
  }

  private void analyse(List<Future<Long>> execute, int batch)
      throws ExecutionException, InterruptedException {
    long count = 0;
    for (Future<Long> longFuture : execute) {
      log.debug("{}", longFuture);
      Long ri = longFuture.get();
      if (ri > count) {
        count = ri;
      }
    }
    double res = (parallel * transaction) / (count * 1.0 / 1000);
    reportUtil.put(batch, (int) res);
    log.info("第{}批次(batch)，平均执行速度为 {} 行/s.", batch, (int) res);
  }

  private List<Future<Long>> execute(int batch) {
    List<Future<Long>> res = new ArrayList<>();
    String[] split = proportion.split(":");
    int insert = Integer.parseInt(split[0]);
    int update = Integer.parseInt(split[1]);
    int delete = Integer.parseInt(split[2]);
    if (insert + update + delete != parallel) {
      throw new RuntimeException("insert + update + delete != parallel");
    }
    if (batch == 1) {
      for (int i = 0; i < parallel; i++) {
        Future<Long> submit = pool.submit(taskConfig.newInsertTask());
        res.add(submit);
      }
    } else if (batch == 2) {
      for (int i = 0; i < parallel / 2; i++) {
        Future<Long> submit = pool.submit(taskConfig.newInsertTask());
        res.add(submit);
      }
      for (int i = 0; i < parallel / 2; i++) {
        Future<Long> submit = pool.submit(taskConfig.newUpdateTask());
        res.add(submit);
      }
    } else {
      for (int i = 0; i < insert; i++) {
        Future<Long> submit = pool.submit(taskConfig.newInsertTask());
        res.add(submit);
      }
      for (int i = 0; i < update; i++) {
        Future<Long> submit = pool.submit(taskConfig.newUpdateTask());
        res.add(submit);
      }
      for (int i = 0; i < delete; i++) {
        Future<Long> submit = pool.submit(taskConfig.newDeleteTask());
        res.add(submit);
      }
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
