package com.homer.canal_rocket.service.changemonitor.exception;


import com.homer.canal_rocket.exception.RunMsgException;

/**
 * 模块自定异常
 *
 * @author CPYF-YI MAO
 * @date 2021/8/25 15:06
 */
public class ChangeMonitorException extends RunMsgException {

  public ChangeMonitorException(String msg) {
    super(msg);
  }

  public ChangeMonitorException(String msg, Throwable t) {
    super(msg, t);
  }
}
