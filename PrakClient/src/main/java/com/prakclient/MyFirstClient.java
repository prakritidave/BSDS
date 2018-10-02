package com.prakclient;

import org.json.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class MyFirstClient {

  private int countRequests = 0;

  private synchronized void incrementRequest(){
    countRequests= countRequests+1;
  }

  public synchronized int getCountRequests(){

    return countRequests;
  }

  private Client client = ClientBuilder.newClient();

//client.target("http://ec2-54-187-12-74.us-west-2.compute.amazonaws.com:8080/AnotherProject_war/rest/myfirstapp");

  private WebTarget webTarget = client.target("https://ugrkcch2xj.execute-api.us-west-2.amazonaws.com/mystage/myresource");

  public Response postText(Object requestEntity) throws ClientErrorException {

    this.incrementRequest();

        return webTarget.request(MediaType.TEXT_PLAIN)
      .post(javax.ws.rs.client.Entity.entity(requestEntity, MediaType.TEXT_PLAIN),
     Response.class);

  }

  //    return webTarget.request(MediaType.APPLICATION_JSON)
  //  .post(javax.ws.rs.client.Entity.entity(requestEntity, MediaType.APPLICATION_JSON),
  // Response.class);


  public String getStatus() throws ClientErrorException {

    this.incrementRequest();

    return webTarget.request(MediaType.TEXT_PLAIN).get(String.class);
  }

}
