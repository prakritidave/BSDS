package com.anotherproject;

public class User {
  private int userId;
  private int day;
  private int timeInterval;
  private int stepCount;

  User(){
    this.userId =0;
    this.day = 1;
    this.stepCount = 0;
  }

  User(int userId, int day, int interval, int step){
    this.userId = userId;
    this.day = day;
    this.timeInterval = interval;
    this.stepCount = step;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  public int getDay() {
    return day;
  }

  public void setDay(int day) {
    this.day = day;
  }

  public int getTimeInterval() {
    return timeInterval;
  }

  public void setTimeInterval(int timeInterval) {
    this.timeInterval = timeInterval;
  }

  public int getStepCount() {
    return stepCount;
  }

  public void setStepCount(int stepCount) {
    this.stepCount = stepCount;
  }

}
