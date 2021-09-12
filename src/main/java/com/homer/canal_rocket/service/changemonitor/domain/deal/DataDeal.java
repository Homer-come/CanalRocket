package com.homer.canal_rocket.service.changemonitor.domain.deal;

import com.alibaba.otter.canal.protocol.CanalEntry;
import com.homer.canal_rocket.service.changemonitor.dto.ColumnChangeDto;
import com.homer.canal_rocket.service.changemonitor.dto.RowChangeDto;
import com.homer.canal_rocket.service.changemonitor.exception.ChangeMonitorException;
import com.homer.canal_rocket.service.changemonitor.types.ChangeTypeEnum;
import com.homer.canal_rocket.service.changemonitor.types.Gtid;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author CPYF-YI MAO
 * @date 2021/8/27 16:08
 */
@Slf4j
public class DataDeal {

  public List<RowChangeDto> getRowChangeList(List<CanalEntry.Entry> entries) {
    List<RowChangeDto> rowChanges = new ArrayList<>();
    for (CanalEntry.Entry entry : entries) {
      //非行数据的实体类型，跳过
      if (entry.getEntryType() != CanalEntry.EntryType.ROWDATA) {
        continue;
      }
      //RowChange对象，包含了一行数据变化的所有特征;
      //比如isDdl 是否是ddl变更操作 sql 具体的ddl sql beforeColumns afterColumns 变更前后的数据字段等等
      CanalEntry.RowChange rowChange;
      try {
        rowChange = CanalEntry.RowChange.parseFrom(entry.getStoreValue());
      } catch (Exception e) {
        log.debug("Canal event parse error,data:{}", entry.toString(), e);
        throw new ChangeMonitorException("Canal event parse error", e);
      }
      //获取操作类型：insert/update/delete类型
      CanalEntry.EventType eventType = rowChange.getEventType();
      //打印Header信息
      final CanalEntry.Header entryHeader = entry.getHeader();
      final String gtid = entryHeader.getGtid();
      if (StringUtils.isBlank(gtid)) {
//        throw new ChangeMonitorException("Canal event parse not find GTID");
//        continue;
      }
      log.debug("RowChange header ==> binlog[{}:{}],name[{}:{}],eventType[{}],gtid[{}]",
        entryHeader.getLogfileName(), entryHeader.getLogfileOffset(),
        entryHeader.getSchemaName(), entryHeader.getTableName(),
        eventType, gtid);
      //判断是否是DDL语句
      if (rowChange.getIsDdl()) {
        log.debug("RowChange is DDL,SQL:{}", rowChange.getSql());
        continue;
      }
      //获取RowChange对象里的每一行数据
      for (CanalEntry.RowData rowData : rowChange.getRowDatasList()) {
        //当前只处理更新UPDATE事件
        if (eventType == CanalEntry.EventType.UPDATE) {
          RowChangeDto rowChangeDto = new RowChangeDto();
          rowChangeDto.setSchema(entryHeader.getSchemaName());
          rowChangeDto.setTableName(entryHeader.getTableName());
          rowChangeDto.setTime(new Timestamp(entryHeader.getExecuteTime()));
//          rowChangeDto.setGTid(new Gtid(gtid));

          rowChangeDto.setChangeType(ChangeTypeEnum.UPDATE);
          rowChangeDto.setColumns(this.getColumnChanges(rowData, rowChangeDto));
          rowChanges.add(rowChangeDto);
        }
      }
    }
    log.debug("RowChange parse event data:{}", rowChanges);
    rowChanges.sort(Comparator.comparing(RowChangeDto::getGTid));
    return rowChanges;
  }

  /**
   * 获取变更的字段信息
   *
   * @param rowData      数据行
   * @param rowChangeDto 行转换对象
   * @return 变更字段信息
   */
  private List<ColumnChangeDto> getColumnChanges(CanalEntry.RowData rowData, RowChangeDto rowChangeDto) {
    //获取更新过的字段名称
    Map<String, ColumnChangeDto> updateColumnMap = new HashMap<>();
    for (CanalEntry.Column afterColumn : rowData.getAfterColumnsList()) {
      //设置行数据ID
      if (afterColumn.getIndex() == 0) {
        if (!afterColumn.getIsKey()) {
          throw new ChangeMonitorException("Canal event parse rowData not find id");
        }
        rowChangeDto.setTableId(afterColumn.getValue());
      }
      //更新过的字段
      if (afterColumn.getUpdated()) {
        ColumnChangeDto columnChangeDto = new ColumnChangeDto();
        columnChangeDto.setName(afterColumn.getName());
        columnChangeDto.setAfterValue(afterColumn.getValue());
        columnChangeDto.setType(afterColumn.getSqlType());
        updateColumnMap.put(afterColumn.getName(), columnChangeDto);
      }
    }
    //获取被更新字段 更新前的 值
    for (CanalEntry.Column beforeColumn : rowData.getBeforeColumnsList()) {
      final ColumnChangeDto changeDto = updateColumnMap.get(beforeColumn.getName());
      if (changeDto != null) {
        changeDto.setBeforeValue(beforeColumn.getValue());
      }
    }
    return new ArrayList<>(updateColumnMap.values());
  }
}
