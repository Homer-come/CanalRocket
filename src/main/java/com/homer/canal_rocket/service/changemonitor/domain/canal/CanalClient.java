package com.homer.canal_rocket.service.changemonitor.domain.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.client.CanalConnectors;
import com.homer.canal_rocket.service.changemonitor.types.CanalServerConf;
import lombok.extern.slf4j.Slf4j;

/**
 * canal客户端
 *
 * @author CPYF-YI MAO
 * @date 2021/8/25 14:32
 */
@Slf4j
public class CanalClient {

  private final CanalServerConf serverConf;
  private CanalConnector connector = null;

  public CanalClient(CanalServerConf serverConf) {
    this.serverConf = serverConf;
  }

  /**
   * 获取客户端连接
   *
   * @return 连接
   */
  public CanalConnector getConnector() {
    if (this.connector == null || !connector.checkValid()) {
      disconnect();
      connect();
    }
    return this.connector;
  }

  /**
   * 创建连接
   */
  private void connect() {
    // 创建链接
    CanalConnector connector = CanalConnectors.newSingleConnector(
      serverConf.getCanalAddr(), serverConf.getDestination(), "", "");
    //打开连接
    connector.connect();
    //订阅数据库表
    connector.subscribe();
    //回滚到未进行ack的地方，下次fetch的时候，可以从最后一个没有ack的地方开始拿
    connector.rollback();

    this.connector = connector;
  }

  /**
   * 断开连接
   */
  public void disconnect() {
    if (this.connector != null) {
      try {
        connector.disconnect();
      } catch (Exception e) {
        log.warn("canal客户端连接关闭异常", e);
      }
    }
  }
}
