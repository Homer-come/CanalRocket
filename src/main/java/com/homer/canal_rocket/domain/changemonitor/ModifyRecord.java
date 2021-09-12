package com.homer.canal_rocket.domain.changemonitor;


import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 表变更记录
 * @author Administrator
 */
@Data
@EqualsAndHashCode
public class ModifyRecord {
  private String id;
  private String entityId;
  private String entityName;
  private String type;
  private Long versionNo;
  private Timestamp time;
  private Timestamp ctime;
}
