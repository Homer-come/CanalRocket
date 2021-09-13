package com.homer.canal_rocket.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author wangl
 * @Date 2021/9/8 10:00
 * @Version 1.0
 */
@Slf4j
@Configuration
public class AysncConfig implements AsyncConfigurer {

  @Override
  @Bean
  public Executor getAsyncExecutor() {
    ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(4);
    executor.setMaxPoolSize(8);
    executor.setQueueCapacity(500);
    executor.setKeepAliveSeconds(60);
    executor.setThreadNamePrefix("Async-Homer-");
    //拒绝策略 - 若等待队列满则由主线程执行
    executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
    //线程池 关闭 的时候，等待所有任务都完成后，再继续 销毁 其他的Bean
    executor.setWaitForTasksToCompleteOnShutdown(true);
    //设置线程池中 任务的等待时间，如果超过这个时间还没有销毁就 强制销毁，以确保应用最后能够被关闭
    executor.setAwaitTerminationSeconds(60);
    return executor;
  }

  @Override
  public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
    return (throwable, method, objects) -> {
      String msg = StringUtils.EMPTY;
      if (ArrayUtils.isNotEmpty(objects) && objects.length > 0) {
        msg = StringUtils.join(msg, "参数是：");
        for (int i = 0; i < objects.length; i++) {
          msg = StringUtils.join(msg, objects[i]);
        }
      }
      if (Objects.nonNull(method)) {
        msg = StringUtils.join(msg, "方法为：" + method.getName());
      }
      if (Objects.nonNull(throwable)) {
        msg = StringUtils.join(msg, "错误信息为：" + throwable.getMessage());
      }
      log.error(msg);
    };
  }
}
