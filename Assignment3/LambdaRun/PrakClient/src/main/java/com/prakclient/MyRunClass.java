package com.prakclient;

import com.amazonaws.services.lambda.AWSLambda;
import com.sun.security.sasl.ntlm.FactoryImpl;
import javafx.util.Pair;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.util.concurrent.*;

public class MyRunClass implements Runnable {

  private int day=1, testsPerPhase, startInterval, endInterval, intervalLength, numOfIterations, userPopulation;
  private String serverURL;
  private BlockingQueue<MyMetrics> blockingQueue;
  private WebTarget webTarget;
  private AWSLambda awsLambda;

  MyRunClass(int day, int testsPerPhase, String phase,
             int startInterval, int endInterval, int userPopulation,
             String url, BlockingQueue<MyMetrics> queue, WebTarget webTarget, AWSLambda awsLambdaVar){

    this.testsPerPhase = testsPerPhase;
    this.startInterval = startInterval;
    this.endInterval = endInterval;
    this.intervalLength = (endInterval-startInterval)+1;
    this.numOfIterations = testsPerPhase * intervalLength;
    this.userPopulation = userPopulation;
    this.day = day;
    this.serverURL = url;
    blockingQueue = queue;
    this.webTarget = webTarget;
    this.awsLambda = awsLambdaVar;
  }

  private void addToQueue(long beforeTimestamp, long afterTimestamp, long latency){
    double beforeMilliseconds = (beforeTimestamp/1.0);
    double afterMilliseconds = (afterTimestamp/1.0);
    double latencyMilliseconds = (latency/1.0);
    blockingQueue.add(new MyMetrics(beforeMilliseconds, afterMilliseconds, latencyMilliseconds));
  }

  @Override
  public void run() {

    ClientForLambda lambdaClient = new ClientForLambda(this.awsLambda);;

    for(int i=0; i<numOfIterations; i++){

      int userid1 = ThreadLocalRandom.current().nextInt(1,userPopulation+1);
      int userid2 = ThreadLocalRandom.current().nextInt(1, userPopulation+1);
      int userid3 = ThreadLocalRandom.current().nextInt(1,userPopulation+1);

      int timeInt1 = ThreadLocalRandom.current().nextInt(startInterval,endInterval+1);
      int timeInt2 = ThreadLocalRandom.current().nextInt(startInterval,endInterval+1);
      int timeInt3 = ThreadLocalRandom.current().nextInt(startInterval,endInterval+1);

      int stepCount1 = ThreadLocalRandom.current().nextInt(0,5001);
      int stepCount2 = ThreadLocalRandom.current().nextInt(0,5001);
      int stepCount3 = ThreadLocalRandom.current().nextInt(0,5001);

//    System.out.println(Thread.currentThread().getName());

      long before1 = System.currentTimeMillis();
      int postStatus1 = lambdaClient.postStepCount(userid1, day, timeInt1, stepCount1);
//      System.out.println("postStatus1 "+ postStatus1);
      long after1 = System.currentTimeMillis();
      addToQueue(before1, after1, (after1-before1));


      long before2 = System.currentTimeMillis();
      int postStatus2 = lambdaClient.postStepCount(userid2, day, timeInt2, stepCount2);
//      System.out.println("postStatus2 "+ postStatus2);
      long after2 = System.currentTimeMillis();
      addToQueue(before2, after2, (after2-before2));


      long before3 = System.currentTimeMillis();
      int currentStepCount = lambdaClient.getRecentStepCount(userid1);
      long after3 = System.currentTimeMillis();
//      System.out.println("currentStepCount "+ currentStepCount);
      addToQueue(before3, after3, (after3-before3));


      long before4 = System.currentTimeMillis();
      int stepCountDay = lambdaClient.getUserStepCountByDay(userid2, day);
      long after4 = System.currentTimeMillis();
//      System.out.println("stepCountDay "+ stepCountDay);
      addToQueue(before4, after4, (after4-before4));


      long before5 = System.currentTimeMillis();
      int postStatus3 = lambdaClient.postStepCount(userid3, day, timeInt3, stepCount3);
//      System.out.println("postStatus3 "+ postStatus3);
      long after5 = System.currentTimeMillis();
      addToQueue(before5, after5, (after5-before5));

    }

  }

  public int getTestsPerPhase() {
    return testsPerPhase;
  }

  public int getStartInterval() {
    return startInterval;
  }

  public int getEndInterval() { return endInterval; }

  public int getIntervalLength() {
    return intervalLength;
  }

  public int getNumOfIterations() {
    return numOfIterations;
  }

}
//      Response response1 = lambdaClient.postStepCount(userid1, day, timeInt1, stepCount1);
//      int postStatus1 = response1.readEntity(Integer.class);
//response2.close();
