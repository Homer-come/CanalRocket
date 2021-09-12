package com.homer.canal_rocket.service.changemonitor.application;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.homer.canal_rocket.domain.changemonitor.ModifyRecord;
import com.homer.canal_rocket.domain.changemonitor.ModifyRecordItem;
import com.homer.canal_rocket.service.changemonitor.config.ChangeMonitorConfig;
import com.homer.canal_rocket.service.changemonitor.domain.canal.CanalMonitor;
import com.homer.canal_rocket.service.changemonitor.domain.deal.DataDeal;
import com.homer.canal_rocket.service.changemonitor.domain.deal.VersionManager;
import com.homer.canal_rocket.service.changemonitor.dto.ColumnChangeDto;
import com.homer.canal_rocket.service.changemonitor.dto.RowChangeDto;
import com.homer.canal_rocket.service.changemonitor.types.CanalAheadConfig;
import com.homer.canal_rocket.service.changemonitor.types.CanalTransactionEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import javax.annotation.Resource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 数据库监听服务的外部入口
 */
@Slf4j
@Service
@ConditionalOnBean(value = {ChangeMonitorConfig.class})
public class ChangeMonitorService {

  @Resource
  private CanalMonitor canalMonitor;
  @Resource
  private CanalAheadConfig canalAheadConfig;
  @Resource
  private ApplicationEventPublisher applicationEventPublisher;

  /**
   * 执行一次变化的数据处理任务
   */
  @Transactional(rollbackFor = Exception.class)
  public void execOnceChangeDeal() {
    log.debug("进入Canal消费方法[ChangeMonitorService -> execOnceChangeDeal()]");
    DataDeal deal = new DataDeal();
    List<CanalEntry.Entry> entries = canalMonitor.executeChange();
    if (!entries.isEmpty()) {
      //提交事件
      applicationEventPublisher.publishEvent(new CanalTransactionEvent(canalMonitor.getCurrBatchId()));
      log.debug("===>已发布事务事件通知");
    }
    //获取数据变更的数据类型
    entries = entries.stream()
      .filter(entry -> CanalEntry.EntryType.ROWDATA == entry.getEntryType())
      .collect(Collectors.toList());
    log.debug("===>已获取变更数据量{}", entries.size());
    if (entries.isEmpty()) {
      return;
    }
    List<RowChangeDto> rowChanges = deal.getRowChangeList(entries);
    log.debug("===>已转换数据量{}", rowChanges.size());
    Timestamp now = Timestamp.valueOf(LocalDateTime.now());
    //处理并转换存储
    List<ModifyRecord> modifyRecords = new ArrayList<>();
    List<ModifyRecordItem> modifyRecordItems = new ArrayList<>();
    String maxGtid = null;

    //指定的监听的库
    final String dbSchema = canalAheadConfig.getDbSchema();
    for (RowChangeDto rowChange : rowChanges) {
      //检查库名是否正确 | 避免消费到非本库的数据造成紊乱
      final String schema = rowChange.getSchema();
      if (!StringUtils.equals(dbSchema, schema)) {
        log.warn("数据库变化监听到非指定的库的信息,【{}-{}】", dbSchema, schema);
        continue;
      }
      ModifyRecord modifyRecord = new ModifyRecord();
      modifyRecord.setId(UUID.randomUUID().toString());
      modifyRecord.setEntityId(rowChange.getTableId());
      modifyRecord.setEntityName(rowChange.getTableName());
      modifyRecord.setType(rowChange.getChangeType().name());
      modifyRecord.setTime(rowChange.getTime());
      modifyRecord.setCtime(now);
      for (ColumnChangeDto column : rowChange.getColumns()) {
        ModifyRecordItem recordItem = new ModifyRecordItem();
        recordItem.setId(UUID.randomUUID().toString());
        recordItem.setModifyRecordId(modifyRecord.getId());
        recordItem.setName(column.getName());
        recordItem.setBeforeValue(column.getBeforeValue());
        recordItem.setAfterValue(column.getAfterValue());
        recordItem.setType(column.getType());
        modifyRecordItems.add(recordItem);
      }
      //通过版本控制器获取版本号
//      final long version = versionManager.getVersion(rowChange.getGTid(), rowChange.getTableId());
//      if (version == -1) {
//        log.debug("===>版本号-1取值忽略：{}", rowChange);
//        continue;
//      }
//      modifyRecord.setVersionNo(version);
      modifyRecords.add(modifyRecord);
      maxGtid = rowChange.getGTid().getGtid();
    }
    log.debug("===>已封装数据量{}", modifyRecords.size());
    log.debug("===>已执行存储");
  }

  /**
   * 监听提交ACK事件
   *
   * @param event ack事件
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void changeDealCommit(CanalTransactionEvent event) {
    log.debug("------> canal change deal event commit , event data [{}]", event);
    canalMonitor.confirm();
    log.debug("------> canal change deal event commit end");
  }

  /**
   * 监听事务回滚事件
   *
   * @param event ack事件
   */
  @TransactionalEventListener(phase = TransactionPhase.AFTER_ROLLBACK)
  public void changeDealRollback(CanalTransactionEvent event) {
    log.debug("------> canal change deal event rollback , event data [{}]", event);
    canalMonitor.rollback();
    log.debug("------> canal change deal event rollback end");
  }
}
