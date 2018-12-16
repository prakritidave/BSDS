package com.prakclient;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.lambda.AWSLambda;
import com.amazonaws.services.lambda.AWSLambdaClientBuilder;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.lambda.model.InvokeRequest;
import com.amazonaws.services.lambda.model.InvokeResult;

import java.nio.ByteBuffer;

public class ClientForLambda {
  private static volatile long countRequests = 0, failedRequests = 0;
  private WebTarget webTarget;
  private static AWSLambda awsLambda=null;

  public ClientForLambda(AWSLambda awsLambdaVar){
    awsLambda = awsLambdaVar;
  }

  public Integer postStepCount(int userId, int day, int timeInt, int steps) throws ClientErrorException {
    String strPayload = String.format("{\"userId\":\"%d\",\"day\":\"%d\",\"timeInterval\":\"%d\",\"stepCount\":\"%d\"}", userId, day, timeInt, steps);
    String functionName = "AddNewUserRequestHandler";
    return invokeRequestForLambda(functionName, strPayload);
  }

  public Integer getRecentStepCount(int userid){
    String strPayload = String.format("{\"userId\":\"%d\"}", userid);
    String functionName = "GetLatestStepCountRequestHandler";
    return invokeRequestForLambda(functionName, strPayload);
  }

  public Integer getUserStepCountByDay(int userid, int day){
    String strPayload = String.format("{\"userId\":\"%s\",\"day\":\"%s\"}", userid, day);
    String function = "GetStepCountByDayRequestHandler";
    return invokeRequestForLambda(function, strPayload);
  }

  public static Integer invokeRequestForLambda(String strFunction, String strPayload){

    incrementRequest();
    int status = -2;

    try {
      InvokeRequest invokeRequest = new InvokeRequest()
        .withFunctionName(strFunction)
        .withPayload(strPayload);

      InvokeResult invokeResult = awsLambda.invoke(invokeRequest);
      ByteBuffer byteBuffer = invokeResult.getPayload();
      String rawJson = new String(byteBuffer.array(), "UTF-8");
      status = Integer.parseInt(rawJson);
      //System.out.println("postStepCount status " + status);
    }
    catch (Exception e) {
      incrementFailedRequest();
      e.printStackTrace();
    }

    return status;
  }

  private static synchronized void incrementRequest(){
    countRequests += 1;
  }
  private static synchronized void incrementFailedRequest(){
    failedRequests += 1;
  }
  public static long getCountRequests(){
    return countRequests;
  }
  public static long getFailedRequests(){
    return failedRequests;
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

}



//    AWSLambda awsLambda = AWSLambdaClientBuilder.standard()
//      .withRegion(Regions.US_WEST_2)
//      .withCredentials(new AWSStaticCredentialsProvider(awsCreds)).build();

