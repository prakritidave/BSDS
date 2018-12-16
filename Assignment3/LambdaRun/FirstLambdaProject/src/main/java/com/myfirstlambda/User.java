package com.myfirstlambda;

public class User {
  private int userId;
  private int day;
  private int timeInterval;
  private int stepCount;

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

  public User(){

  }

  public User(int userId, int day, int time, int step){
    this.userId = userId;
    this.day = day;
    this.timeInterval = time;
    this.stepCount = step;
  }

  public User(int userId, int day){
    this.userId = userId;
    this.day = day;
    this.timeInterval = 0;
    this.stepCount = 1;
  }

  public User(int userId){
    this.userId = userId;
    this.day = 1;
    this.timeInterval = 1;
    this.stepCount = 1;
  }



}
