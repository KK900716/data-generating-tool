package com.sics.tool.file;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Report {
  private int batch;
  private int metric;
}
