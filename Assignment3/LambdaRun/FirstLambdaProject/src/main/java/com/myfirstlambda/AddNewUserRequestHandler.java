package com.myfirstlambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class AddNewUserRequestHandler implements RequestHandler<User, Integer> {
  private static Connection conn = null;
  static {
    try{
      conn = DriverManager.getConnection("jdbc:mysql://prakschema.ctg53dqnivdw.us-west-2.rds.amazonaws.com:3306/prakschema","prakriti", "prakriti");
    }catch (Exception e){
      e.printStackTrace();
    }
  }


  @Override
  public Integer handleRequest(User user, Context context) {

    int result = -1;
    String sqlstatement = "INSERT IGNORE INTO users(userid,day,timeInterval,stepCount) values(?,?,?,?)";

    if(conn != null){
      try
      {
        PreparedStatement prepStatement = conn.prepareStatement(sqlstatement);
        prepStatement.setInt(1, user.getUserId());
        prepStatement.setInt(2, user.getDay());
        prepStatement.setInt(3, user.getTimeInterval());
        prepStatement.setInt(4, user.getStepCount());
        result = prepStatement.executeUpdate();
        prepStatement.close();
      }
      catch (Exception e){
        e.printStackTrace();
      }
    }

    return result;

  }
}
