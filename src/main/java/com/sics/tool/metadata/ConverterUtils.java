package com.sics.tool.metadata;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Random;
import javax.annotation.Resource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

/**
 * utils.
 *
 * @author liangjc
 * @version 2023/03/21
 */
@Component
public class ConverterUtils {
  @Resource private Random random;

  public void bindData(List<String> content, List<Integer> length, PreparedStatement stmt)
      throws Exception {
    for (int i = 0; i < content.size(); i++) {
      switch (content.get(i)) {
        case "int":
          Converter.SET_INT.executeBind(stmt, i, random.nextInt());
          break;
        case "string":
          Converter.SET_STRING.executeBind(
              stmt, i, RandomStringUtils.randomAlphabetic(length.get(i)));
          break;
        default:
          throw new RuntimeException(content.get(i) + " is not support!");
      }
    }
  }
}
