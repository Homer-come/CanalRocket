package com.homer.canal_rocket.service.changemonitor.dto;

import lombok.Data;

/**
 * 列变更数据
 *
 * @author CPYF-YI MAO
 * @date 2021/8/30 19:58
 */
@Data
public class ColumnChangeDto {

  private String name;
  private String beforeValue;
  private String afterValue;
  private int type;
}
