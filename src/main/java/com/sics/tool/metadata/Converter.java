package com.sics.tool.metadata;

import java.math.BigDecimal;
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
  SET_INT((stmt, index, content) -> stmt.setInt(index, (int) content)),
  SET_STRING((stmt, index, content) -> stmt.setString(index, (String) content)),
  SET_NUMBER((stmt, index, content) -> stmt.setBigDecimal(index, (BigDecimal) content));

  private Bind bind;

  Converter(Bind bind) {
    this.bind = bind;
  }

  public void executeBind(PreparedStatement stmt, int index, Object content) throws Exception {
    bind.bind(stmt, index, content);
  }
}
