package com.anotherproject;
import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
  private ResultSet rs = null;
  private String sqlstatement = null;

  int addNewUser(User user){
    int result = -1;
    sqlstatement = "INSERT IGNORE INTO users(userid,day,timeInterval,stepCount) values(?,?,?,?)";
    try(Connection conn = NewConnectionClass.getDataSource().getConnection();
       PreparedStatement prepStatement = conn.prepareStatement(sqlstatement))
    {
      prepStatement.setInt(1, user.getUserId());
      prepStatement.setInt(2, user.getDay());
      prepStatement.setInt(3, user.getTimeInterval());
      prepStatement.setInt(4, user.getStepCount());
      result = prepStatement.executeUpdate();
    }
    catch (Exception e){
      e.printStackTrace();
    }
    return result;
  }

  int getStepCountByDay(int userId, int day){
    sqlstatement = "SELECT SUM(stepCount) as totalStepCount FROM users WHERE userid=? AND day=?";
    int totalSteps=-1;

    try(
      Connection conn = NewConnectionClass.getDataSource().getConnection();
      PreparedStatement prepStatement = conn.prepareStatement(sqlstatement))
    {
      prepStatement.setInt(1,userId);
      prepStatement.setInt(2, day);
      rs = prepStatement.executeQuery();
      while(rs.next()){
        totalSteps = rs.getInt("totalStepCount");
      }
      rs.close();
    }catch (Exception e){
      e.printStackTrace();
    }

    return totalSteps;
  }

  int getMostRecentDaySteps(int userid){
    int steps=-1;
    sqlstatement = "SELECT SUM(stepCount) as totalStepCount FROM users WHERE userid=? AND " +
      "day=(select day from users where userid=? order by day desc limit 1)";

    try(Connection conn = NewConnectionClass.getDataSource().getConnection();
        PreparedStatement prepStatement = conn.prepareStatement(sqlstatement))
    {
      prepStatement.setInt(1,userid);
      prepStatement.setInt(2, userid);
      rs = prepStatement.executeQuery();
      while(rs.next()){
        steps = rs.getInt("totalStepCount");
      }
      rs.close();
    }catch (Exception e){
      e.printStackTrace();
    }

//    System.out.println("step count in most recent days in userDao"+ steps);
    return steps;

  }

  int[] getStepCountForDays(int userid, int startDay, int numDays){

    sqlstatement = "SELECT SUM(stepCount) as totalStepCount FROM users WHERE userid=? AND day=?";
    int steps=0;
    int totalSteps=0;
    int stepsInRange[] = new int[numDays+1];

    try(
      Connection conn = NewConnectionClass.getDataSource().getConnection();
      PreparedStatement prepStatement = conn.prepareStatement(sqlstatement))
    {
      for(int j=startDay, i=0; j<(startDay+numDays); j++, i++){
        prepStatement.setInt(1,userid);
        prepStatement.setInt(2, j);
        rs = prepStatement.executeQuery();
        while(rs.next()){
          steps = rs.getInt(1);
          System.out.println(steps);
          stepsInRange[i] = steps;
          totalSteps += steps;
        }
      }
      stepsInRange[(startDay+numDays)-1] = totalSteps;
      rs.close();
    }catch (Exception e){
      e.printStackTrace();
    }
    return stepsInRange;

  }

}
