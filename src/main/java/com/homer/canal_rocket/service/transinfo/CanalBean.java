package com.homer.canal_rocket.service.transinfo;

import com.homer.canal_rocket.domain.user.User;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author wangl
 * @Date 2021/9/14 16:39
 * @Version 1.0
 */
@Data
public class CanalBean implements Serializable {
  //数据
  private List<User> data;
  //数据库名称
  private String database;
  private long es;
  //递增，从1开始
  private int id;
  //是否是DDL语句
  private boolean isDdl;
  //表结构的字段类型
  private MysqlType mysqlType;
  //UPDATE语句，旧数据
  private List<User> old;
  //主键名称
  private List<String> pkNames;
  //sql语句
  private String sql;
  private SqlType sqlType;
  //表名
  private String table;

  private long ts;
  //(新增)INSERT、(更新)UPDATE、(删除)DELETE、(删除表)ERASE等等
  private String type;
}