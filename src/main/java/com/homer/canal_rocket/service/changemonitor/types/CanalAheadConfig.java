package com.homer.canal_rocket.service.changemonitor.types;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

/**
 * 预读配置信息
 *
 * @author CPYF-YI MAO
 * @date 2021/9/1 15:44
 */
@Data
public class CanalAheadConfig {

  /**
   * 数据库实例IP
   */
  @NotEmpty
  private String dbIp;
  /**
   * 数据库实例端口
   */
  @NotEmpty
  private String dbPort;
  /**
   * 数据库实例库名
   */
  @NotEmpty
  private String dbSchema;
  /**
   * 数据库实例用户名
   */
  @NotEmpty
  private String dbUsername;
  /**
   * 数据库实例密码
   */
  @NotEmpty
  private String dbPassword;
}
