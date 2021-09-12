package com.homer.canal_rocket.config;

import com.homer.canal_rocket.service.changemonitor.domain.canal.CanalServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;
import java.util.Optional;


/**
 * 启动之后，加载类，加载一些程序运行必须加载的一些值；
 * 启动时，必要配置项的检查；
 * Created by developer_hyaci on 2019/12/13.
 */
public class StartupListener implements ApplicationListener<ContextRefreshedEvent> {

  private static final Logger LOGGER = LoggerFactory.getLogger(StartupListener.class);

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {
    ApplicationContext context = event.getApplicationContext();
    long start;
    long end;
    final Map<String, CanalServer> serverMap = context.getBeansOfType(CanalServer.class);
    final Optional<CanalServer> canalServer = serverMap.values().stream().findFirst();
    if (canalServer.isPresent()) {
      LOGGER.info("===================>>>Start CanalServer start");
      start = System.currentTimeMillis();
//      canalServer.get().start();
      end = System.currentTimeMillis();
      LOGGER.info("===================>>>End CanalServer started, cast[{}]MS。", end - start);
    }
  }
}
