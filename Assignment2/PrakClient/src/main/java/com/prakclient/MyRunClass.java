package com.prakclient;

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

  MyRunClass(int day, int testsPerPhase, String phase,
             int startInterval, int endInterval, int userPopulation,
             String url, BlockingQueue<MyMetrics> queue, WebTarget webTarget){

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
  }

  private void addToQueue(long beforeTimestamp, long afterTimestamp, long latency){
    double beforeMilliseconds = (beforeTimestamp/1.0);
    double afterMilliseconds = (afterTimestamp/1.0);
    double latencyMilliseconds = (latency/1.0);
//    System.out.println("inside before add to queue, queue size is "+ blockingQueue.size());
    blockingQueue.add(new MyMetrics(beforeMilliseconds, afterMilliseconds, latencyMilliseconds));
//    System.out.println("insize add to queue, queue size is "+ blockingQueue.size());
//    System.out.println("adding to queue");
  }

  @Override
  public void run() {

    MyFirstClient firstClient = new MyFirstClient(webTarget);

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
      Response response1 = firstClient.postStepCount(userid1, day, timeInt1, stepCount1);
      int postStatus1 = response1.readEntity(Integer.class);
      long after1 = System.currentTimeMillis();
      addToQueue(before1, after1, (after1-before1));
      response1.close();

      long before2 = System.currentTimeMillis();
      Response response2 = firstClient.postStepCount(userid2, day, timeInt2, stepCount2);
      int postStatus2 = response2.readEntity(Integer.class);
      long after2 = System.currentTimeMillis();
      addToQueue(before2, after2, (after2-before2));
      response2.close();

      long before3 = System.currentTimeMillis();
      Response response3 = firstClient.getRecentStepCount(userid1);
      int currentStepCount = response3.readEntity(Integer.class);
      long after3 = System.currentTimeMillis();
      addToQueue(before3, after3, (after3-before3));
      response3.close();

      long before4 = System.currentTimeMillis();
      Response response4 = firstClient.getUserStepCountByDay(userid2, day);
      int stepCountDay = response4.readEntity(Integer.class);
      long after4 = System.currentTimeMillis();
      addToQueue(before4, after4, (after4-before4));
      response4.close();

      long before5 = System.currentTimeMillis();
      Response response5 = firstClient.postStepCount(userid3, day, timeInt3, stepCount3);
      int postStatus3 = response5.readEntity(Integer.class);
      long after5 = System.currentTimeMillis();
      addToQueue(before5, after5, (after5-before5));
      response5.close();

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
