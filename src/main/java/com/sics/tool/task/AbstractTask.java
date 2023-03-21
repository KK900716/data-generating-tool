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
 * @author 44380
 */
@Slf4j
@AllArgsConstructor
public abstract class AbstractTask implements Callable<Long> {
  protected final int batchBind = 100;
  protected String schema;
  protected String table;
  protected List<String> content;
  protected int transaction;
  protected DataSource dataSource;
  protected ConverterUtils converterUtils;
  protected List<String> whereColumnName;
  protected List<String> setColumnName;

  @Override
  public Long call() throws Exception {
    String sql = getSql();
    log.info("sql:{}", sql);
    long start = System.currentTimeMillis();
    Connection conn = dataSource.getConnection();
    PreparedStatement stmt = conn.prepareStatement(sql);
    log.info("start execute.");
    int batch = transaction / this.batchBind;
    for (int i = 0; i < batch; i++) {
      addBatch(stmt, this.batchBind);
    }
    int last = transaction % this.batchBind;
    addBatch(stmt, last);
    conn.close();
    return System.currentTimeMillis() - start;
  }

  protected String getSqlPart(Placeholder placeholder, int size, String separator) {
    StringBuilder pl = new StringBuilder();
    int i;
    for (i = 0; i < size - 1; i++) {
      String pla = "";
      if (!placeholder.getColumnName().isEmpty()) {
        pla += getMark(placeholder.getColumnName().get(i));
      }
      pl.append(pla).append(" ").append(placeholder.getPlaceholder()).append(separator);
    }
    String pla = "";
    if (!placeholder.getColumnName().isEmpty()) {
      pla += getMark(placeholder.getColumnName().get(i));
    }
    pl.append(pla).append(" ").append(placeholder.getPlaceholder());
    return pl.toString();
  }

  protected String getMark(String s) {
    return "`" + s + "`";
  }

  protected void addBatch(PreparedStatement stmt, int batch) throws Exception {
    for (int j = 0; j < batch; j++) {
      innerbind(stmt);
      stmt.addBatch();
    }
    stmt.executeBatch();
    stmt.clearBatch();
  }

  public abstract void innerbind(PreparedStatement stmt) throws Exception;

  public abstract String getSql();
}
