package com.homer.canal_rocket.domain.changemonitor;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 表变更记录明细
 *
 * @author Administrator
 */
@Data
@EqualsAndHashCode
public class ModifyRecordItem {
  private String id;
  private String modifyRecordId;
  private String name;
  private String beforeValue;
  private String afterValue;
  private Integer type;
}
