package com.anotherproject;

import java.beans.PropertyVetoException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import javax.sql.DataSource;

public class NewConnectionClass {
  private static ComboPooledDataSource cpds = null;
  static{
    try{
      cpds = new ComboPooledDataSource();
      cpds.setDriverClass("com.mysql.jdbc.Driver");
      cpds.setJdbcUrl("jdbc:mysql://prakschema.ctg53dqnivdw.us-west-2.rds.amazonaws.com:3306/prakschema");
      cpds.setUser("prakriti");
      cpds.setPassword("prakriti");
      // Optional Settings
      cpds.setMinPoolSize(50);
      cpds.setInitialPoolSize(50);
      cpds.setAcquireIncrement(25);
      cpds.setMaxPoolSize(248);
      cpds.setMaxStatementsPerConnection(8);
      cpds.setStatementCacheNumDeferredCloseThreads(1);
//      cpds.setMaxStatements(8);
//      cpds.setNumHelperThreads(10);
//      cpds.setTestConnectionOnCheckin(true);
//      cpds.setTestConnectionOnCheckout(false);
//      cpds.setMaxConnectionAge(28);

    }catch (Exception e){
      e.printStackTrace();
    }
  }

  public static ComboPooledDataSource getDataSource(){
    return cpds;
  }

}
