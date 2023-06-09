package com.sics.tool.conf;

import com.mysql.cj.conf.PropertyKey;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;
import javax.sql.DataSource;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author liangjc
 * @version 2023/03/21
 */
@Component
@ConfigurationProperties(prefix = "spring.datasource")
@Data
public class MyDataSource implements DataSource {
  private String driverClassName;
  private String url;
  private String username;
  private String password;

  @Override
  public Connection getConnection() throws SQLException {
    Properties info = new Properties();
    info.setProperty(PropertyKey.USER.getKeyName(), username);
    info.setProperty(PropertyKey.PASSWORD.getKeyName(), password);
    info.setProperty(PropertyKey.useServerPrepStmts.getKeyName(), Boolean.toString(true));
    info.setProperty(PropertyKey.databaseTerm.getKeyName(), "SCHEMA");
    info.setProperty(PropertyKey.useSSL.getKeyName(), Boolean.toString(false));
    Connection conn = DriverManager.getConnection(url, info);
    conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
    conn.setAutoCommit(true);
    return conn;
  }

  @Override
  public Connection getConnection(String username, String password) throws SQLException {
    return null;
  }

  @Override
  public PrintWriter getLogWriter() throws SQLException {
    return null;
  }

  @Override
  public void setLogWriter(PrintWriter out) throws SQLException {}

  @Override
  public void setLoginTimeout(int seconds) throws SQLException {}

  @Override
  public int getLoginTimeout() throws SQLException {
    return 0;
  }

  @Override
  public Logger getParentLogger() throws SQLFeatureNotSupportedException {
    return null;
  }

  @Override
  public <T> T unwrap(Class<T> iface) throws SQLException {
    return null;
  }

  @Override
  public boolean isWrapperFor(Class<?> iface) throws SQLException {
    return false;
  }
}
