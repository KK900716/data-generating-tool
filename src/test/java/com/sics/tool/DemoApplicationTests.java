package com.sics.tool;

import java.util.concurrent.*;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationTests {

  @Test
  void contextLoads() throws InterruptedException, ExecutionException {
    ExecutorService executor = Executors.newFixedThreadPool(10);
    CompletionService<Integer> cs = new ExecutorCompletionService<>(executor);
    cs.submit(
        () -> {
          System.out.println("xxx1");
          return 1;
        });
    cs.submit(
        () -> {
          System.out.println("xxx2");
          return 2;
        });
    for (int i = 0; i < 2; i++) {
      Future<Integer> take = cs.take();
      executor.execute(
          () -> {
            try {
              System.out.println(take.get());
            } catch (InterruptedException | ExecutionException e) {
              throw new RuntimeException(e);
            }
          });
    }
  }
}
