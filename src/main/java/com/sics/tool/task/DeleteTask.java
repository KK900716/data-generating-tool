package com.sics.tool.task;

import com.sics.tool.metadata.ConverterUtils;
import java.sql.PreparedStatement;
import java.util.List;
import javax.sql.DataSource;

public class DeleteTask extends AbstractTask {

  public DeleteTask(
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
    converterUtils.bindDeleteData(stmt);
  }

  @Override
  public String getSql() {
    return String.format(
        "DELETE FROM `%s`.`%s` WHERE %s",
        schema,
        table,
        getSqlPart(new Placeholder("= ?", whereColumnName), whereColumnName.size(), ","));
  }
}
