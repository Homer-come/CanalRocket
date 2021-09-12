package com.homer.canal_rocket.service.changemonitor.types;

import org.springframework.context.ApplicationEvent;

/**
 * @author CPYF-YI MAO
 * @date 2021/8/31 18:40
 */
public class CanalTransactionEvent extends ApplicationEvent {
  /**
   * Create a new {@code ApplicationEvent}.
   *
   * @param source the object on which the event initially occurred or with
   *               which the event is associated (never {@code null})
   */
  public CanalTransactionEvent(Object source) {
    super(source);
  }
}
