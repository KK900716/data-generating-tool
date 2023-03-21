package com.sics.tool.task;

import com.sics.tool.metadata.ConverterUtils;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.concurrent.Callable;
import javax.sql.DataSource;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * InsertTask
 *
 * @author liangjc
 * @version 2023/03/21
 */
@Slf4j
@AllArgsConstructor
public class InsertTask implements Callable<Long> {
  private static final String SQL = "INSERT INTO `%s`.`%s` VALUES (%s)";
  private static final int BATCH = 100;
  private String schema;
  private String table;
  private List<String> content;
  private List<Integer> length;
  private int transaction;
  private DataSource dataSource;
  private ConverterUtils converterUtils;

  private void addBatch(PreparedStatement stmt, int batch) throws Exception {
    for (int j = 0; j < batch; j++) {
      converterUtils.bindData(content, length, stmt);
      stmt.addBatch();
    }
    stmt.executeBatch();
    stmt.clearBatch();
  }

  private String getPlaceholder() {
    StringBuilder pl = new StringBuilder();
    for (int i = 0; i < content.size() - 1; i++) {
      pl.append("?").append(",");
    }
    pl.append("?");
    return pl.toString();
  }

  @Override
  public Long call() throws Exception {
    String sql = String.format(SQL, schema, table, getPlaceholder());
    long start = System.currentTimeMillis();
    log.info("sql is {}.", sql);
    Connection conn = dataSource.getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql);
    log.info("start execute.");
    int batch = transaction / BATCH;
    for (int i = 0; i < batch; i++) {
      addBatch(stmt, BATCH);
    }
    int last = transaction % BATCH;
    addBatch(stmt, last);
    return System.currentTimeMillis() - start;
  }
}
