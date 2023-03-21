package com.sics.tool.metadata;

import java.sql.PreparedStatement;
import lombok.extern.slf4j.Slf4j;

/**
 * converter.
 *
 * @author liangjc
 * @version 2023/03/21
 */
@Slf4j
enum Converter {
  SET_INT(
      (stmt, index, content) -> {
        log.debug("{}", content);
        stmt.setInt(index + 1, (int) content);
      }),
  SET_STRING(
      (stmt, index, content) -> {
        log.debug("{}", content);
        stmt.setString(index + 1, (String) content);
      });

  private Bind bind;

  Converter(Bind bind) {
    this.bind = bind;
  }

  public void executeBind(PreparedStatement stmt, int index, Object content) throws Exception {
    bind.bind(stmt, index, content);
  }
}
