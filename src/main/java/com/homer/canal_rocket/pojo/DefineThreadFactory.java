package com.homer.canal_rocket.pojo;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 自定义线程工厂
 * Created by developer_hyaci
 *
 * @author CPYF-YI MAO
 */
public class DefineThreadFactory implements ThreadFactory {
  /**
   * 原子叠加编号
   */
  private final AtomicInteger threadNumber = new AtomicInteger(1);
  /**
   * 线程名前缀
   */
  private final String namePrefix;
  /**
   * 线程是否为守护线程
   */
  private final boolean isDaemon;

  /**
   * 构造方法
   */
  public DefineThreadFactory(String name) {
    this(name, false);
  }

  /**
   * 构造方法
   */
  public DefineThreadFactory(String name, boolean isDaemon) {
    this.namePrefix = "pool-" + name + "-";
    this.isDaemon = isDaemon;
  }

  @Override
  public Thread newThread(Runnable r) {
    Thread t = new Thread(r, namePrefix + threadNumber.getAndIncrement());
    t.setDaemon(isDaemon);
    if (t.getPriority() != Thread.NORM_PRIORITY) {
      t.setPriority(Thread.NORM_PRIORITY);
    }
    return t;
  }
}
