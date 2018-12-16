  package com.myfirstlambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import java.sql.*;
import java.sql.Connection;
import java.sql.PreparedStatement;


public class GetStepCountByDayRequestHandler implements RequestHandler<User, Integer> {

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

    int totalSteps=-1;
    ResultSet rs = null;

    String sqlstatement = "SELECT SUM(stepCount) as totalStepCount FROM users WHERE userid=? AND day=?";

    if(conn!=null){
      try
      {
        PreparedStatement prepStatement = conn.prepareStatement(sqlstatement);
        prepStatement.setInt(1,user.getUserId());
        prepStatement.setInt(2, user.getDay());
        rs = prepStatement.executeQuery();
        if(rs.next()){
          totalSteps = rs.getInt("totalStepCount");
        }
        prepStatement.close();
        rs.close();
      }catch (Exception e){
        e.printStackTrace();
      }
    }

    return totalSteps;
  }
}
