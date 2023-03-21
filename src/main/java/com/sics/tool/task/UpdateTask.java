package com.sics.tool.task;

import com.sics.tool.metadata.ConverterUtils;
import java.sql.PreparedStatement;
import java.util.List;
import javax.sql.DataSource;

public class UpdateTask extends AbstractTask {

  public UpdateTask(
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
    converterUtils.bindUpdateData(stmt);
  }

  @Override
  public String getSql() {
    return String.format(
        "UPDATE `%s`.`%s` SET %s WHERE %s",
        schema,
        table,
        getSqlPart(new Placeholder("= ?", setColumnName), setColumnName.size(), ","),
        getSqlPart(new Placeholder("= ?", whereColumnName), whereColumnName.size(), " AND "));
  }
}
