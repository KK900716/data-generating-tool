package com.sics.tool.metadata;

import java.sql.PreparedStatement;

/**
 * Bind.
 *
 * @author liangjc
 * @version 2023/03/21
 */
@FunctionalInterface
public interface Bind {
  void bind(PreparedStatement stmt, int index, Object content) throws Exception;
}
