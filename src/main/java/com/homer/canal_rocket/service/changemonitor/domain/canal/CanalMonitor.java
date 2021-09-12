package com.homer.canal_rocket.service.changemonitor.domain.canal;

import com.alibaba.otter.canal.client.CanalConnector;
import com.alibaba.otter.canal.protocol.CanalEntry;
import com.alibaba.otter.canal.protocol.Message;
import com.homer.canal_rocket.service.changemonitor.exception.ChangeMonitorException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

/**
 * @author CPYF-YI MAO
 * @date 2021/8/27 15:46
 */
@Slf4j
@Getter
public class CanalMonitor {

  /**
   * 单批次获取数量
   */
  private int batchSize = 100;
  /**
   * 客户端
   */
  private final CanalClient canalClient;
  /**
   * 服务端返回的批次ID，用于确认ack
   */
  private long currBatchId;
  /**
   * 0：未获取批次数据  1：已获取未提交  2：已获取提交失败
   */
  private int currBatchState = 0;
  /**
   * 锁定增量获取过程的Key
   */
  private static final String MONITOR_LOCK = "CanalMonitor-LockKey";

  public CanalMonitor(CanalClient canalClient) {
    this.canalClient = canalClient;
  }

  public void setBatchSize(int batchSize) {
    this.batchSize = batchSize;
  }

  /**
   * 获取批次的增量数据
   *
   * @return 增量数据
   */
  public List<CanalEntry.Entry> executeChange() {
    //若获取时发现存在提交失败的先执行提交操作
    if (currBatchState == 2) {
      this.confirm();
    }
    if (currBatchState != 0) {
      throw new ChangeMonitorException("批次尚未提交无法再次获取更新");
    }
    final CanalConnector connector = canalClient.getConnector();
    // 获取指定数量的数据
    Message message = connector.getWithoutAck(batchSize);
    //获取批量的数量
    int size = message.getEntries().size();
    //如果没有数据
    if (size <= 0) {
      currBatchId = -1;
      return Collections.emptyList();
    }
    //获取批量ID
    currBatchId = message.getId();
    currBatchState = 1;
    //如果有数据,处理数据
    return message.getEntries();
  }

  /**
   * 确认批次
   */
  public void confirm() {
    try {
      canalClient.getConnector().ack(currBatchId);
      currBatchState = 0;
      currBatchId = -1;
    } catch (Exception e) {
      currBatchState = 2;
      log.error("canal确认批次处理回复ack异常", e);
    }
  }

  /**
   * 回滚批次
   */
  public void rollback() {
    try {
      canalClient.getConnector().rollback(currBatchId);
      currBatchState = 0;
      currBatchId = -1;
    } catch (Exception e) {
      log.error("canal回滚批次处理响应rollback异常", e);
    }
  }
}
