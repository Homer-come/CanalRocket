package com.homer.canal_rocket.exception;


/**
 * 运行时消息异常
 */
public class RunMsgException extends RuntimeException {

  public RunMsgException(String msg) {
    super(msg);
  }

  public RunMsgException(String msg, Throwable t) {
    super(msg, t);
  }

}
