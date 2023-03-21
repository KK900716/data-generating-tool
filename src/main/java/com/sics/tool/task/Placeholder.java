package com.sics.tool.task;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Placeholder {
  private String placeholder;
  private List<String> columnName;
}
