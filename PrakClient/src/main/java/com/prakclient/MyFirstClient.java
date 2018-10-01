package com.prakclient;

import javax.ws.rs.*;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


public class MyFirstClient {

  private int countRequests = 0;

  private synchronized void incrementRequest(){
    countRequests+=1;
  }

  public synchronized int getCountRequests(){

    return countRequests;
  }

  private Client client = ClientBuilder.newClient();

//client.target("http://ec2-54-187-12-74.us-west-2.compute.amazonaws.com:8080/AnotherProject_war/rest/myfirstapp");

  private WebTarget webTarget = client.target("https://2b2hnsrqp6.execute-api.us-west-2.amazonaws.com/prakstage");

  public Response postText(Object requestEntity) throws ClientErrorException {

    this.incrementRequest();
    System.out.println("Req entity "+requestEntity.toString());
    System.out.println(getCountRequests());
    return webTarget.request(MediaType.APPLICATION_JSON)
        .post(Entity.entity(requestEntity, MediaType.APPLICATION_JSON), Response.class);
  }

  //    return webTarget.request(MediaType.APPLICATION_JSON)
  //  .post(javax.ws.rs.client.Entity.entity(requestEntity, MediaType.APPLICATION_JSON),
  // Response.class);


  public String getStatus() throws ClientErrorException {

    this.incrementRequest();

    return webTarget.request(javax.ws.rs.core.MediaType.TEXT_PLAIN).get(String.class);
  }

}
