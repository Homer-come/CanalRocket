package com.homer.canal_rocket.service.changemonitor.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * @author CPYF-YI MAO
 * @date 2021/8/27 15:35
 */
@Getter
@AllArgsConstructor
public class CanalServerConf {

  /**
   * canal server部署的地址信息
   */
  private final InetSocketAddress canalAddr;
  /**
   * canal server配置的数据库实例名称对应
   */
  private final String destination;
}
