package com.sics.tool.metadata;

import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * utils.
 *
 * @author liangjc
 * @version 2023/03/21
 */
@Component
@Slf4j
public class ConverterUtils {
  @Resource private Random random;

  @Value("#{'${parameter.structure.content}'.split(',')}")
  private List<String> content;

  @Value("#{'${parameter.structure.length}'.split(',')}")
  private List<Integer> length;

  @Value("#{'${parameter.update.where}'.split(',')}")
  private List<Integer> where;

  @Value("#{'${parameter.update.set}'.split(',')}")
  private List<Integer> set;

  @Value("#{'${parameter.structure.rule}'.split(',')}")
  private List<String> originRule;

  private Map<Integer, String[]> rule;

  private final CopyOnWriteArrayList<SoftReference<List<Object>>> update =
      new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<SoftReference<List<Object>>> delete =
      new CopyOnWriteArrayList<>();

  private final ReentrantLock lock = new ReentrantLock();

  private final AtomicInteger atomicInteger = new AtomicInteger(0);

  private final AtomicLong serial = new AtomicLong(Long.MIN_VALUE);

  private void bind(
      PreparedStatement stmt,
      int seat,
      boolean b,
      List<Object> list,
      Operation operation,
      Integer index)
      throws Exception {
    switch (content.get(seat - 1)) {
      case "int":
        switch (operation) {
          case INSERT:
            Converter.SET_INT.executeBind(stmt, index, getInsertIntObj(b, list));
            break;
          case UPDATE:
            Converter.SET_INT.executeBind(stmt, index, getUpdateObj(seat, list));
            break;
          default:
            throw new RuntimeException(operation + " is not support!");
        }
        break;
      case "string":
        switch (operation) {
          case INSERT:
            Converter.SET_STRING.executeBind(stmt, index, getInsertStringObj(seat, b, list));
            break;
          case UPDATE:
            Converter.SET_STRING.executeBind(stmt, index, getUpdateObj(seat, list));
            break;
          default:
            throw new RuntimeException(operation + " is not support!");
        }
        break;
      case "number":
        switch (operation) {
          case INSERT:
            Converter.SET_NUMBER.executeBind(stmt, index, getInsertNumberObj(b, list));
            break;
          case UPDATE:
            Converter.SET_NUMBER.executeBind(stmt, index, getUpdateObj(seat, list));
            break;
          default:
            throw new RuntimeException(operation + " is not support!");
        }
        break;
      default:
        throw new RuntimeException(content.get(seat - 1) + " is not support!");
    }
  }

  private Object getInsertNumberObj(boolean b, List<Object> list) {
    Object obj = BigDecimal.valueOf(random.nextDouble() * 100);
    if (b) {
      list.add(obj);
    }
    return obj;
  }

  private Object getUpdateObj(int i, List<Object> list) {
    return list.get(where.indexOf(i));
  }

  private Object getInsertStringObj(int i, boolean b, List<Object> list) {
    Object obj;
    String[] strings = rule.get(i);
    if (strings == null) {
      obj = RandomStringUtils.randomAlphabetic(length.get(i - 1));
    } else {
      switch (strings[0]) {
        case "$serial":
          obj = String.valueOf(serial.incrementAndGet());
          break;
        default:
          obj = strings[(int) (Math.random() * strings.length)];
      }
    }
    if (b) {
      list.add(obj);
    }
    return obj;
  }

  private Object getInsertIntObj(boolean b, List<Object> list) {
    Object obj;
    synchronized (this) {
      obj = atomicInteger.getAndIncrement();
    }
    //    obj = random.nextInt();
    if (b) {
      list.add(obj);
    }
    return obj;
  }

  public void bindInsertData(PreparedStatement stmt) throws Exception {
    if (rule == null) {
      rule = new HashMap<>(originRule.size());
      for (String s : originRule) {
        String[] split = s.split(":");
        rule.put(Integer.valueOf(split[0]), split[1].split("、"));
      }
    }
    List<Object> list = new ArrayList<>();
    for (int i = 1; i <= content.size(); i++) {
      bind(stmt, i, where.contains(i), list, Operation.INSERT, i);
    }
    update.add(new SoftReference<>(list));
  }

  public void bindUpdateData(PreparedStatement stmt) throws Exception {
    List<Object> list;
    lock.lock();
    try {
      list = getObjects(update);
      delete.add(new SoftReference<>(list));
    } finally {
      lock.unlock();
    }
    int t = 1;
    for (Integer i : set) {
      bind(stmt, i, false, null, Operation.INSERT, t++);
    }
    for (Integer i : where) {
      bind(stmt, i, true, list, Operation.UPDATE, t++);
    }
  }

  private List<Object> getObjects(CopyOnWriteArrayList<SoftReference<List<Object>>> update) {
    List<Object> list;
    int i = (int) (Math.random() * update.size());
    while (update.get(i).get() == null) {
      update.remove(i);
      i = (int) (Math.random() * update.size());
    }
    list = update.get(i).get();
    update.remove(i);
    return list;
  }

  public void bindDeleteData(PreparedStatement stmt) throws Exception {
    List<Object> list;
    lock.lock();
    try {
      list = getObjects(delete);
    } finally {
      lock.unlock();
    }
    int t = 1;
    for (Integer i : where) {
      bind(stmt, i, true, list, Operation.UPDATE, t++);
    }
  }
}
