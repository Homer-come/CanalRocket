package com.homer.canal_rocket.service.changemonitor.dto;


import com.homer.canal_rocket.service.changemonitor.types.ChangeTypeEnum;
import com.homer.canal_rocket.service.changemonitor.types.Gtid;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

/**
 * 行变更数据
 */
@Data
public class RowChangeDto {

  private String schema;
  private String tableId;
  private String tableName;
  private ChangeTypeEnum changeType;
  private List<ColumnChangeDto> columns;
  private Timestamp time;
  private Gtid gTid;
}
