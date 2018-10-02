package com.prakclient;

import javax.rmi.ssl.SslRMIClientSocketFactory;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;


public class PrakMain {

  private static long startTime, endTime;
  private static long totalRequests, successfulRequests;
  private static double meanLatency, medianLatency, throughput, wallTime, totalLatency;
  private static List<Double> storeLatency;

  private static void computeThroughput(){
    throughput = totalRequests/wallTime;
  }

  private static void computeMeanLatency(){
    meanLatency = totalLatency/totalRequests;
  }

  private static synchronized void incrementSuccessfulRequest(){
    successfulRequests+=1;
  }

  private static synchronized void addLatency(Long timeDiff){

    double timeDiffMilliSeconds = (timeDiff/1000000.0);
    storeLatency.add(timeDiffMilliSeconds);
    totalLatency = totalLatency + timeDiffMilliSeconds;
  }

  private static void computeLatencyMedian(){
    int len = storeLatency.size();
    if(len == 0)return;

    if(len % 2 == 0){
      double temp = storeLatency.get((len/2)-1) + storeLatency.get(len/2);
      medianLatency = (temp/2);
    }else{
      medianLatency = (storeLatency.get(len/2));
    }
  }

  private static void PerformTesting(int numofthreads, final int iterations,final MyFirstClient firstClient, String phase){

    ExecutorService executorService = Executors.newFixedThreadPool(numofthreads);

    final Runnable runPhase = new Runnable() {

      public void run() {

        for(int i=0; i<iterations; i++){
          String status ="";
          String entityString = "";

//          System.out.println(Thread.currentThread().getName() + " " + i);
          long beforeGet = System.nanoTime();
          status = firstClient.getStatus();
          long afterGet = System.nanoTime();
//          System.out.println(status);
//          response is "alive" thus length is 7
          if(status.length() == 7){
            incrementSuccessfulRequest();
          }
          //POST
          long beforePost = System.nanoTime();
          Response response = firstClient.postText("\"prakriti\"");
          long afterPost = System.nanoTime();

          entityString = response.readEntity(String.class);
          response.close();
//          System.out.println(entityString);

          if(entityString.equals("8") ){
            incrementSuccessfulRequest();
          }

          //store the latencies
          addLatency(afterGet-beforeGet);
          addLatency(afterPost - beforePost);
        }
      }
    };

    if(phase.equals("Warmup")){
      System.out.println("Client starting..... Time: " + (System.nanoTime())/1000000000.0 + " seconds \n");
    }

    System.out.println( phase + " phase: All threads running ....\n");

    long phaseStart = System.nanoTime();

    for(int i=0; i<numofthreads; i++){
      executorService.submit(runPhase);
    }
    executorService.shutdown();

    try{
      executorService.awaitTermination(Long.MAX_VALUE,TimeUnit.SECONDS);
    }catch (InterruptedException e){
      throw new RuntimeException(e);
    }

    long phaseEnd = System.nanoTime();

    if(phase.equals("Warmup")){
      startTime = phaseStart;
    }

    if(phase.equals("Cooldown")){
      endTime = phaseEnd;
    }

    double phaseTime = ((phaseEnd-phaseStart)/1000000000.0);
    System.out.println( phase + " phase complete: "+ phaseTime + " seconds");

  }


  public static void main(String args[]) {

    System.out.println("inside : " + Thread.currentThread().getName());

    try{
      if(args.length<2){
        throw new IllegalArgumentException();
      }
    }catch (IllegalArgumentException e){
      e.printStackTrace();
    }

    final MyFirstClient firstClient = new MyFirstClient();
    int numOfThreads = Integer.parseInt(args[0]);
    final int iterations = Integer.parseInt(args[1]);

    System.out.println("Max num of threads " + numOfThreads);
    System.out.println("Iterations " + iterations);

    storeLatency = Collections.synchronizedList(new ArrayList<Double>());

    //--------WARM UP PHASE---------------
    int warmupthreads = (int)(0.1*numOfThreads);
    PerformTesting(warmupthreads, iterations, firstClient, "Warmup");

//    --------LOADING PHASE---------------
    int loadingThreads = (int)(numOfThreads*0.5);
    PerformTesting(loadingThreads, iterations, firstClient, "Loading");

    //-------PEAK PHASE-------------------
    PerformTesting(numOfThreads, iterations, firstClient, "Peak");

    //-------COOL DOWN PHASE---------------------
    int coolDownThreads = (int)(0.25*numOfThreads);
    PerformTesting(coolDownThreads, iterations, firstClient, "Cooldown");

    System.out.println("================================================== \n");

    totalRequests = firstClient.getCountRequests();
    System.out.println("Total number of requests sent:" + totalRequests);
    System.out.println("Total number of successful responses: " + successfulRequests);

    wallTime = ((endTime-startTime)/1000000000.0);
    System.out.println("Test Wall Time: "+ wallTime + " seconds \n");

    computeThroughput();
    System.out.println("Overall Throughput: "+ throughput + " requests per second ");

    Collections.sort(storeLatency);

    System.out.println("Overall Latency: "+totalLatency+" milliseconds \n");

    computeLatencyMedian();
    System.out.println("Median Latency: "+ medianLatency + " milliseconds \n");

    computeMeanLatency();
    System.out.println("Mean Latency: " + meanLatency + " milliseconds \n");

    double percentile99th = 0.0;
    int index1 = (int)(storeLatency.size()*0.99);
    if(index1 < storeLatency.size()){
      percentile99th = storeLatency.get(index1);
    }

    double percentile95th = 0.0;
    int index2 = (int)(storeLatency.size()*0.95);

    if(index2<storeLatency.size()){
      percentile95th = storeLatency.get(index2);
    }

    System.out.println("95th percentile latency:  " + percentile95th + " milliseconds \n");
    System.out.println("99th percentile latency:  "+ percentile99th + " milliseconds \n" );

  }

}
