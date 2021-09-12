package com.homer.canal_rocket.service.changemonitor.types;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * canal.properties配置
 *
 * @author CPYF-YI MAO
 * @date 2021/8/25 17:50
 */
@Getter
@AllArgsConstructor
public class CanalConfig {

  /**
   * canal server绑定的本地IP信息，如果不配置，默认选择一个本机IP进行启动服务
   */
  private final String ip;
  /**
   * 当前server上部署的instance列表【数据库实例-名称】
   * 配置后当前程序会自动在canal.conf.dir对应的目录下建立同名的文件夹供canal server读取使用
   */
  private final String destination;
}
