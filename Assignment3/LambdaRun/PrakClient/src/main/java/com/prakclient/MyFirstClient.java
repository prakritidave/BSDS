package com.prakclient;

import org.glassfish.jersey.client.ClientProperties;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;

public class MyFirstClient {

  private static volatile long countRequests = 0;
  private static volatile long failedRequests = 0;
  private WebTarget webTarget;

  public MyFirstClient(WebTarget webTarget){
    this.webTarget = webTarget;
  }

  public synchronized void incrementRequest(){
    countRequests += 1;
  }

  public synchronized void incrementFailedRequest(){
    failedRequests += 1;
  }

  public static long getCountRequests(){
    return countRequests;
  }

  public static long getFailedRequests(){
    return failedRequests;
  }

  public Response postStepCount(int userId, int day, int timeInt, int steps) throws ClientErrorException {

    incrementRequest();
    try{
      return
        webTarget.path("/"+userId+"/"+day+"/"+timeInt+"/"+steps)
        .request(MediaType.TEXT_PLAIN)
        .post(javax.ws.rs.client.Entity.text(" "));
    }catch (Exception e){
      incrementFailedRequest();
      e.printStackTrace();
    }
    return null;
  }

  public Response getRecentStepCount(int userid){

    incrementRequest();
    try{
      return webTarget.path("/current/"+userid)
        .request(MediaType.TEXT_PLAIN).get();
    }catch (Exception e){
      incrementFailedRequest();
      e.printStackTrace();
    }
    return null;
  }

  public Response getUserStepCountByDay(int userid, int day){

    incrementRequest();
    try{
       return webTarget.path("/single/"+ userid +"/"+day)
         .request(MediaType.TEXT_PLAIN).get();
    }catch (Exception e){
      incrementFailedRequest();
      e.printStackTrace();
    }
    return null;
  }

  public ArrayList getStepCountForDays(int userid, int startday, int numdays){
    incrementRequest();
    try{
      return webTarget.path("/range/"+userid+"/"+ startday +"/"+numdays)
        .request(MediaType.TEXT_PLAIN).get(ArrayList.class);
    }
    catch(Exception e){
      incrementFailedRequest();
      e.printStackTrace();
    }
    return  null;
  }

  public String getStatus() throws ClientErrorException {
    return webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
  }

  public Response postText(Object requestEntity) throws ClientErrorException {
    return webTarget.request(MediaType.TEXT_PLAIN).post(javax.ws.rs.client.Entity.entity(requestEntity, MediaType.TEXT_PLAIN), Response.class);
  }

//  public void closeMyFirstClient(){
//    this.client.close();
//  }
}

//client.target("http://ec2-54-187-12-74.us-west-2.compute.amazonaws.com:8080/AnotherProject_war/rest/myfirstapp");
//private WebTarget webTarget = client.target("https://ugrkcch2xj.execute-api.us-west-2.amazonaws.com/mystage/myresource");
//private String URL = "http://ec2-34-220-90-202.us-west-2.compute.amazonaws.com:8080/AnotherProject_war/rest/myfirstapp";
//private String URL = "http://localhost:8080/rest/myfirstapp";
//    return webTarget.request(MediaType.TEXT_PLAIN).post(javax.ws.rs.client.Entity.entity(requestEntity, MediaType.TEXT_PLAIN), Integer.class);
//private String URL = "http://ec2-34-212-112-214.us-west-2.compute.amazonaws.com:8080/AnotherProject_war/rest/myfirstapp";
