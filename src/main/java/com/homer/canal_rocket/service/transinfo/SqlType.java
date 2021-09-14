package com.homer.canal_rocket.service.transinfo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author wangl
 * @Date 2021/9/14 16:40
 * @Version 1.0
 */
@Data
public class SqlType implements Serializable {
  private int id;
  private int commodity_name;
  private int commodity_price;
  private int number;
  private int description;
}
