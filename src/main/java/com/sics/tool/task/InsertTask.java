package com.sics.tool.task;

import com.sics.tool.metadata.ConverterUtils;
import java.sql.PreparedStatement;
import java.util.Collections;
import java.util.List;
import javax.sql.DataSource;

/**
 * InsertTask
 *
 * @author liangjc
 * @version 2023/03/21
 */
public class InsertTask extends AbstractTask {

  public InsertTask(
      String schema,
      String table,
      List<String> content,
      int transaction,
      DataSource dataSource,
      ConverterUtils converterUtils,
      List<String> whereColumnName,
      List<String> setColumnName) {
    super(
        schema,
        table,
        content,
        transaction,
        dataSource,
        converterUtils,
        whereColumnName,
        setColumnName);
  }

  @Override
  public void innerbind(PreparedStatement stmt) throws Exception {
    converterUtils.bindInsertData(stmt);
  }

  @Override
  public String getSql() {
    return String.format(
        "INSERT INTO `%s`.`%s` VALUES (%s)",
        schema,
        table,
        getSqlPart(new Placeholder("?", Collections.emptyList()), content.size(), ","));
  }
}
