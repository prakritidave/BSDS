package com.prakclient;

import com.sun.org.apache.xpath.internal.operations.Bool;
import javafx.util.Pair;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;

public class PrakMain {
// 128 threads, 567999 rows
// 256 threads, 1119076 rows

  private static BlockingQueue<MyMetrics> blockingQueue = new LinkedBlockingQueue<>();;
  private static BufferedWriter writer;
  private static Thread fileThread;
  private static boolean isrunning = true;

  private static void executePhase(int numOfThreads, String phase, int day,
                                   int testsPerPhase, int start, int end, int userPopulation,
                                   String url, WebTarget webTarget){

    System.out.println("Running phase "+ phase);
    System.out.println("number of threads submitted"+ numOfThreads);

      ExecutorService executorService = Executors.newFixedThreadPool(numOfThreads);
      for (int i = 0; i < numOfThreads; i++) {
        executorService.submit(new MyRunClass(day, testsPerPhase, phase, start, end, userPopulation, url, blockingQueue, webTarget));
      }
      executorService.shutdown();
      try {
        executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

  }


  private static void runWriteToFileThread(){

    fileThread = new Thread(){
      public void run(){
        try{
          while(isrunning) {
            while (!blockingQueue.isEmpty()) {
              MyMetrics metric = blockingQueue.poll();
              try {
//                System.out.println("wrtiting to file "+ metric.toString());
                writer.write(String.valueOf(metric.getStartTime()) + ","
                  + String.valueOf(metric.getEndTime()) + ","
                  + String.valueOf(metric.getLatency()) + "\n");
              } catch (IOException e) {
                e.printStackTrace();
              }
            }
          }
        }catch (Exception e){
          e.printStackTrace();
        }
      }
    };
  }

  private static void setFileName(String fileName) throws IOException {
    try {
      writer = new BufferedWriter(new FileWriter(fileName));
      writer.write("StartTimestamp,EndTimestamp,Latency\n");
    }catch (IOException e){
      e.printStackTrace();
    }
  }

  private static void stopFileWriter(){
    try{
      while(!blockingQueue.isEmpty()){

      }
      isrunning = false;

      try{
        writer.write("Total_Requests " + String.valueOf(MyFirstClient.getCountRequests()) + ","
          + " " + "," + " " + "\n");
        writer.write("Failed_Requests " + String.valueOf(MyFirstClient.getCountRequests()) + ","
          + " " + "," + " " + "\n");
      }catch (Exception e){
        e.printStackTrace();
      }

      writer.close();
      System.out.println("Closed File");
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }

  public static void main(String args[]) {

    System.out.println("inside : " + Thread.currentThread().getName());
//    String fileName = "C:/Users/prakriti/Documents/BSDS/prakMetrics.csv";
    String fileName = "/home/ec2-user/prakMetrics.csv";

//    System.out.println("blocking queue size " + blockingQueue.size());

    try{
      setFileName(fileName);
    }catch (IOException e){
      e.printStackTrace();
    }

    runWriteToFileThread();
    try{
      if(args.length<5){
        throw new IllegalArgumentException();
      }
    }catch (IllegalArgumentException e){
      e.printStackTrace();
    }
    fileThread.start();

    int numOfThreads = Integer.parseInt(args[0]);
    String serverURL = args[1];
    int day = Integer.parseInt(args[2]);
    int userPopulation = Integer.parseInt(args[3]);
    int testsPerPhase = Integer.parseInt(args[4]);

    Client client = ClientBuilder.newClient();
    WebTarget webTarget = client.target(serverURL);;


    //--------WARM UP PHASE---------------
    int warmupthreads = (int)(0.1*numOfThreads);
    System.out.println("number of warmup threads "+ warmupthreads);
    executePhase(warmupthreads, "Warmup", day, testsPerPhase, 0, 2, userPopulation, serverURL, webTarget);

    System.out.println(MyFirstClient.getCountRequests());

    //--------LOADING PHASE---------------
    int loadingThreads = (int)(numOfThreads*0.5);
    System.out.println("number of loadingThreads "+ loadingThreads);
    executePhase(loadingThreads, "Loading", day, testsPerPhase, 3, 7, userPopulation, serverURL, webTarget);

    System.out.println(MyFirstClient.getCountRequests());

    //-------PEAK PHASE-------------------
    System.out.println("number of peak threads "+ numOfThreads);
    executePhase(numOfThreads, "Peak", day, testsPerPhase, 8, 18, userPopulation, serverURL, webTarget);

    System.out.println(MyFirstClient.getCountRequests());

    //-------COOL DOWN PHASE---------------------
    int coolDownThreads = (int)(0.25*numOfThreads);
    System.out.println("number of coolDownThreads "+ coolDownThreads);
    executePhase(coolDownThreads,"Cooldown", day, testsPerPhase, 19, 23, userPopulation, serverURL, webTarget);

    System.out.println("==================================================");
    System.out.println("Total Requests " + MyFirstClient.getCountRequests());
    System.out.println("Failed Requests " + MyFirstClient.getFailedRequests());

    stopFileWriter();

  }
}



