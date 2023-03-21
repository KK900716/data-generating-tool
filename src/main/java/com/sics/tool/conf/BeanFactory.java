package com.sics.tool.conf;

import java.util.Random;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * BeanFactory.
 *
 * @author 44380
 */
@Configuration
public class BeanFactory {
  @Value("${parameter.pool.corePoolSize}")
  private int corePoolSize;

  @Value("${parameter.pool.maximumPoolSize}")
  private int maximumPoolSize;

  @Value("${parameter.pool.keepAliveTime}")
  private long keepAliveTime;

  @Value("${parameter.pool.blockingQueue}")
  private int blockingQueue;

  @Bean
  public ThreadPoolExecutor getExecutors() {
    AtomicInteger index = new AtomicInteger(0);
    return new ThreadPoolExecutor(
        corePoolSize,
        maximumPoolSize,
        keepAliveTime,
        TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(blockingQueue),
        r -> new Thread(r, "User thread " + index.incrementAndGet()),
        new ThreadPoolExecutor.CallerRunsPolicy());
  }

  @Bean
  public Random getRandom() {
    return new Random();
  }
}
