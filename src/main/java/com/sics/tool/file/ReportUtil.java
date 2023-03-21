package com.sics.tool.file;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ReportUtil {
  @Value("${parameter.report.path}")
  private String path;

  private final List<Report> reports = new ArrayList<>();

  public void put(int batch, int metric) {
    reports.add(new Report(batch, metric));
  }

  public void store() throws IOException {
    try (FileWriter fw = new FileWriter(path)) {
      fw.write("## 报告\n");
      fw.write("|插入批次 batch|指标 行/s|\n");
      fw.write("|:--:|:--:|\n");
      for (Report it : reports) {
        fw.write("|" + it.getBatch() + "|" + it.getMetric() + "|\n");
      }
    }
  }
}
