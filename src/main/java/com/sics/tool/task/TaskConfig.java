package com.sics.tool.task;

import com.sics.tool.metadata.ConverterUtils;
import java.util.List;
import javax.annotation.Resource;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * conf
 *
 * @author liangjc
 * @version 2023/03/21
 */
@Component
@Slf4j
public class TaskConfig {
  @Value("${parameter.structure.schema}")
  private String schema;

  @Value("${parameter.structure.table}")
  private String table;

  @Value("#{'${parameter.structure.content}'.split(',')}")
  private List<String> content;

  @Value("#{'${parameter.structure.length}'.split(',')}")
  private List<Integer> length;

  @Value("${parameter.size.transaction}")
  private int transaction;

  @Resource private DataSource dataSource;
  @Resource private ConverterUtils converterUtils;

  public InsertTask newInsertTask() {
    log.debug("schema:{}.", schema);
    log.debug("table:{}.", table);
    log.debug("content:{}.", content);
    log.debug("length:{}.", length);
    log.debug("transaction:{}.", transaction);
    return new InsertTask(schema, table, content, length, transaction, dataSource, converterUtils);
  }
}
