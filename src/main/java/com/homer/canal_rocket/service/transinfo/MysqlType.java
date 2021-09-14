package com.homer.canal_rocket.service.transinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author wangl
 * @Date 2021/9/14 16:40
 * @Version 1.0
 */

@Data
public class MysqlType implements Serializable {
  private String id;
  private String commodity_name;
  private String commodity_price;
  private String number;
  private String description;
}
