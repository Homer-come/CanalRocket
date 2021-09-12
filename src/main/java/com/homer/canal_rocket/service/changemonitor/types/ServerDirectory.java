package com.homer.canal_rocket.service.changemonitor.types;

import com.homer.canal_rocket.service.changemonitor.exception.ChangeMonitorException;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

/**
 * canal server地址
 *
 * @author CPYF-YI MAO
 * @date 2021/8/25 14:57
 */
@Getter
@AllArgsConstructor
public class ServerDirectory {

  /**
   * canal server的主目录地址
   * 例如：E:\canal-test\canal.deployer-1.1.5
   */
  private final File directory;

  public static ServerDirectory from(String path) {
    File dir = new File(path);
    if (!dir.isDirectory()) {
      throw new ChangeMonitorException("错误的canal服务端目录");
    }
    return new ServerDirectory(dir);
  }
}
