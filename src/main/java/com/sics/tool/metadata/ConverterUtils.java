package com.sics.tool.metadata;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
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

  private AtomicInteger atomicInteger = new AtomicInteger(0);

  private final CopyOnWriteArrayList<List<Object>> update = new CopyOnWriteArrayList<>();
  private final CopyOnWriteArrayList<List<Object>> delete = new CopyOnWriteArrayList<>();

  private final ReentrantLock lock = new ReentrantLock();

  private void bind(
      PreparedStatement stmt, int i, boolean b, List<Object> list, Operation operation)
      throws Exception {
    switch (content.get(i - 1)) {
      case "int":
        switch (operation) {
          case INSERT:
            Converter.SET_INT.executeBind(stmt, i, getInsertIntObj(b, list));
            break;
          case UPDATE:
            Converter.SET_INT.executeBind(stmt, i, getUpdateObj(i, list));
            break;
          default:
            throw new RuntimeException(operation + " is not support!");
        }
        break;
      case "string":
        switch (operation) {
          case INSERT:
            Converter.SET_STRING.executeBind(stmt, i, getInsertStringObj(i, b, list));
            break;
          case UPDATE:
            Converter.SET_INT.executeBind(stmt, i, getUpdateObj(i, list));
            break;
          default:
            throw new RuntimeException(operation + " is not support!");
        }
        break;
      case "number":
        switch (operation) {
          case INSERT:
            Converter.SET_STRING.executeBind(stmt, i, getInsertNumberObj(b, list));
            break;
          case UPDATE:
            Converter.SET_INT.executeBind(stmt, i, getUpdateObj(i, list));
            break;
          default:
            throw new RuntimeException(operation + " is not support!");
        }
        break;
      default:
        throw new RuntimeException(content.get(i - 1) + " is not support!");
    }
  }

  private Object getInsertNumberObj(boolean b, List<Object> list) {
    Object obj = String.valueOf(random.nextDouble());
    if (b) {
      list.add(obj);
    }
    return obj;
  }

  private Object getUpdateObj(int i, List<Object> list) {
    return list.get(i - 1);
  }

  private Object getInsertStringObj(int i, boolean b, List<Object> list) {
    Object obj;
    obj = RandomStringUtils.randomAlphabetic(length.get(i - 1));
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
    List<Object> list = new ArrayList<>();
    for (int i = 1; i <= content.size(); i++) {
      bind(stmt, i, where.contains(i), list, Operation.INSERT);
    }
    update.add(list);
  }

  public void bindUpdateData(PreparedStatement stmt) throws Exception {
    List<Object> list;
    lock.lock();
    try {
      int i = (int) (Math.random() * update.size());
      list = update.get(i);
      update.remove(i);
      delete.add(list);
    } finally {
      lock.unlock();
    }
    for (Integer integer : set) {
      bind(stmt, integer, false, null, Operation.INSERT);
    }
    for (Integer integer : where) {
      bind(stmt, integer, true, list, Operation.UPDATE);
    }
  }

  public void bindDeleteData(PreparedStatement stmt) throws Exception {
    List<Object> list;
    lock.lock();
    try {
      int i = (int) (Math.random() * delete.size());
      list = delete.get(i);
      delete.remove(i);
    } finally {
      lock.unlock();
    }
    for (Integer integer : where) {
      bind(stmt, integer, true, list, Operation.UPDATE);
    }
  }
}
