package com.prakclient;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class WarmUpPhase implements Runnable {
  private int iterations;

  WarmUpPhase(int iterations){
    this.iterations = iterations;
  }

  public void run(){
    MyFirstClient firstClient = new MyFirstClient();

    for(int i=0; i<this.iterations; i++){
    System.out.println(Thread.currentThread().getName());
      System.out.println(firstClient.getStatus());
    }

  }

}
