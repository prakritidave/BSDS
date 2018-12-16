package com.anotherproject;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

@Path("myfirstapp")
public class MyFirstApp {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getStatus() {
    return ("alive");
  }

  @GET
  @Path("/single/{userID}/{day}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public int getUserStepCountByDay(@PathParam("userID") int userID,
                                 @PathParam("day") int day) {

    int totalSteps = -1;
    UserDao userDao = new UserDao();
      totalSteps = userDao.getStepCountByDay(userID, day);

    return totalSteps;
  }

  @GET
  @Path("/current/{userID}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public int getLatestStepCount(@PathParam("userID") int userID) {

    int totalSteps = -1;
    UserDao userDao = new UserDao();
    totalSteps = userDao.getMostRecentDaySteps(userID);
    return totalSteps;
  }

  @POST
  @Path("/{userID}/{day}/{timeInterval}/{stepCount}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public int postStepCountByHour(@PathParam("userID") int userID,
                                 @PathParam("day") int day,
                                 @PathParam("timeInterval") int timeInterval,
                                 @PathParam("stepCount") int stepCount) {
    int result=-1;
    User newUser = new User(userID, day, timeInterval, stepCount);
    UserDao userDao = new UserDao();
    result = userDao.addNewUser(newUser);
    return result;
  }

  @GET
  @Path("/range/{userID}/{startDay}/{numDays}")
  @Consumes(MediaType.TEXT_PLAIN)
  @Produces(MediaType.TEXT_PLAIN)
  public String getStepCountInRange(@PathParam("userID") int userID,
                                           @PathParam("startDay") int startDay,
                                           @PathParam("numDays") int numDays) {

    int stepsInRange[];
    UserDao userDao = new UserDao();
    stepsInRange = userDao.getStepCountForDays(userID, startDay, numDays);
    return Arrays.toString(stepsInRange);
  }

  @POST
  @Consumes(MediaType.TEXT_PLAIN)
  public int postText(String content) {
    return (content.length());
  }

}

//  private static CopyOnWriteArrayList<User> list = new CopyOnWriteArrayList<>();
//    if(list.size() == 1000) {
//      //call the write method passing list
//      list = new CopyOnWriteArrayList<>();
//  }
