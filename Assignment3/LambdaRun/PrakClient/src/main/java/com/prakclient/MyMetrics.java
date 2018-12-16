package com.prakclient;

import javafx.util.Pair;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;

public class MyMetrics{

  private double startTime, endTime, latency;

  public MyMetrics(double startTime, double endTime, double latency){
    this.startTime = startTime;
    this.endTime = endTime;
    this.latency = latency;
  }

  public double getStartTime() {
    return startTime;
  }

  public void setStartTime(double startTime) {
    this.startTime = startTime;
  }

  public double getEndTime() {
    return endTime;
  }

  public void setEndTime(double endTime) {
    this.endTime = endTime;
  }

  public double getLatency() {
    return latency;
  }

  public void setLatency(double latency) {
    this.latency = latency;
  }


}
