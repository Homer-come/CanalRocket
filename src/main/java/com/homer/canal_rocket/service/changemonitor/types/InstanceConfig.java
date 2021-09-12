package com.homer.canal_rocket.service.changemonitor.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * 监听的mysql实例配置
 *
 * @author CPYF-YI MAO
 * @date 2021/8/25 17:50
 */
@Getter
@AllArgsConstructor
public class InstanceConfig {

  /**
   * mysql主库链接地址
   */
  private final InetSocketAddress masterAddr;
  /**
   * mysql数据库帐号
   */
  private final String dbUsername;
  /**
   * mysql数据库密码
   */
  private final String dbPassword;
  /**
   * mysql数据解析关注的表
   * Perl正则表达式.多个正则之间以逗号(,)分隔，转义符需要双斜杠(\) 。注意：此过滤条件只针对row模式的数据有效。
   */
  private final String filterRegex;
  /**
   * mysql数据解析排除的表
   * canal将会过滤那些不符合要求的table，这些table的数据将不会被解析和传送
   */
  private final String filterBackRegex;
}
