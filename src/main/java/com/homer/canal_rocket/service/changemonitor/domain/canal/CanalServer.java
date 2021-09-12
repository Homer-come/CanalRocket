package com.homer.canal_rocket.service.changemonitor.domain.canal;

import com.homer.canal_rocket.pojo.DefineThreadFactory;
import com.homer.canal_rocket.service.changemonitor.exception.ChangeMonitorException;
import com.homer.canal_rocket.service.changemonitor.types.CanalConfig;
import com.homer.canal_rocket.service.changemonitor.types.InstanceConfig;
import com.homer.canal_rocket.service.changemonitor.types.OutputStreamToStrHandler;
import com.homer.canal_rocket.service.changemonitor.types.ServerDirectory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ShutdownHookProcessDestroyer;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * canal服务端
 *
 * @author CPYF-YI MAO
 * @date 2021/8/25 14:29
 */
@Slf4j
public class CanalServer {
  /**
   * canal服务端的jps进程名称
   */
  private static final String CANAL_JPS_NAME = "CanalLauncher";
  private final ServerDirectory directory;
  private final CanalConfig canalConfig;
  private final InstanceConfig instanceConfig;
  /**
   * 单线程池,用于执行canal server
   */
  private final ExecutorService executor = new ThreadPoolExecutor(1, 1,
    0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
    new DefineThreadFactory("CanalServer", true));

  public CanalServer(String serverDir, CanalConfig canalConfig, InstanceConfig instanceConfig) {
    this.directory = ServerDirectory.from(serverDir);
    this.canalConfig = canalConfig;
    this.instanceConfig = instanceConfig;
  }

  /**
   * 启动canal
   */
  public void start() {
    this.stopCanal();
    this.setup();
    executor.execute(this::runCanal);
    try {
      //异步启动线程执行时，延时一秒，避免进程未完全启动
      TimeUnit.SECONDS.sleep(1);
    } catch (InterruptedException e) {
      log.warn("CanalServer启动延时异常", e);
    }
  }

  /**
   * 停止canal服务
   */
  private void stopCanal() {
    //检查canal服务是否已经存在
    CommandLine commandLine = new CommandLine("jps");
    DefaultExecutor executor = new DefaultExecutor();
    StringWriter sw = new StringWriter();
    executor.setStreamHandler(new OutputStreamToStrHandler(sw));
    try {
      executor.execute(commandLine);
    } catch (IOException e) {
      throw new ChangeMonitorException("检查canal服务端jps命令执行异常", e);
    }
    final String result = sw.toString();
    for (String line : result.split("\r\n")) {
      //关闭已存在的canal服务
      if (line.contains(CANAL_JPS_NAME)) {
        final String port = line.substring(0, line.indexOf(" "));
        commandLine = new CommandLine(this.isWindows() ? "tskill" : "kill");
        commandLine.addArgument(port);
        executor = new DefaultExecutor();
        try {
          if (executor.execute(commandLine) == 0) {
            //关闭成功时，延时一秒，避免进程未完全关闭
            TimeUnit.SECONDS.sleep(1);
          }
        } catch (IOException | InterruptedException e) {
          log.warn("关闭canal进程[{}]异常", port, e);
        }
        break;
      }
    }
  }


  /**
   * 启动canal服务端
   */
  private void runCanal() {
    String fileName = this.isWindows() ? "startup.bat" : "startup.sh";
    File binFile = Paths.get(directory.getDirectory().getPath()).resolve("bin").toFile();
    CommandLine commandLine;
    if (this.isWindows()) {
      commandLine = new CommandLine("cmd");
      commandLine.addArgument("/c");
      commandLine.addArgument(String.format("cd /d %s && %s", binFile.getAbsolutePath(), fileName));
    } else {
      commandLine = new CommandLine(binFile.getAbsolutePath());
    }
    DefaultExecutor executor = new DefaultExecutor();
    executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
    try {
      log.info("执行canal启动命令:{}", commandLine);
      executor.execute(commandLine);
    } catch (IOException e) {
      throw new ChangeMonitorException("启动canal服务端异常", e);
    }
  }

  /**
   * 根据系统设置canal服务端的配置文件
   */
  private void setup() {
    final Path confPath = directory.getDirectory().toPath().resolve("conf");
    //处理canal.properties配置文件
    final File canalPropFile = confPath.resolve("canal.properties").toFile();
    final Properties canalProp = this.loadProperties(canalPropFile);
    canalProp.setProperty("canal.ip", canalConfig.getIp());
    canalProp.setProperty("canal.destinations", canalConfig.getDestination());
    //关闭自动监听instance变化功能
    canalProp.setProperty("canal.auto.scan", "false");
    this.storeProperties(canalProp, canalPropFile);

    //处理destination对应的instance.properties配置文件
    final File instPropFile = confPath.resolve(canalConfig.getDestination()).resolve("instance.properties").toFile();
    if (!instPropFile.exists()) {
      try {
        Files.createDirectories(instPropFile.toPath().getParent());
        Files.copy(confPath.resolve("example/instance.properties"), instPropFile.toPath());
      } catch (IOException e) {
        throw new ChangeMonitorException("canal实例配置创建异常", e);
      }
    }
    final Properties instProp = this.loadProperties(instPropFile);
    instProp.setProperty("canal.instance.master.address", instanceConfig.getMasterAddr().toString().replace("/", ""));
    //开启GTID模式
    instProp.setProperty("canal.instance.gtidon", "true");
    instProp.setProperty("canal.instance.dbUsername", instanceConfig.getDbUsername());
    instProp.setProperty("canal.instance.dbPassword", instanceConfig.getDbPassword());
    //监听的指定库表
    instProp.setProperty("canal.instance.filter.regex", instanceConfig.getFilterRegex());
    //过滤的指定库表
    instProp.setProperty("canal.instance.filter.black.regex", instanceConfig.getFilterBackRegex());
    this.storeProperties(instProp, instPropFile);

    //处理logback.xml日志配置文件
    final File logConfigFile = confPath.resolve("logback.xml").toFile();
    final Document logConfigDoc;
    try (FileInputStream logFis = new FileInputStream(logConfigFile)) {
      logConfigDoc = SAXReader.createDefault().read(logFis);
      final Node logHomeNode = logConfigDoc.selectSingleNode("//property[@name='LOG_HOME']");
      if (logHomeNode != null) {
        Element ele = (Element) logHomeNode;
        ele.attribute("value").setValue(directory.getDirectory().getAbsolutePath().replace("\\", "/"));
      }
    } catch (DocumentException | IOException e) {
      throw new ChangeMonitorException("logback配置读取异常", e);
    }
    try (FileOutputStream logFos = new FileOutputStream(logConfigFile);
         OutputStreamWriter logOsw = new OutputStreamWriter(logFos)) {
      logConfigDoc.write(logOsw);
    } catch (IOException e) {
      throw new ChangeMonitorException("logback配置写入异常", e);
    }
  }

  private Properties loadProperties(File prop) {
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(prop));
      return properties;
    } catch (IOException e) {
      throw new ChangeMonitorException("指定的properties配置文件失败", e);
    }
  }

  private void storeProperties(Properties prop, File file) {
    try {
      prop.store(
        new OutputStreamWriter(
          new FileOutputStream(file), StandardCharsets.UTF_8), "System Auto Update");
    } catch (IOException e) {
      throw new ChangeMonitorException("保存指定的properties配置文件失败", e);
    }
  }

  private boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("windows");
  }
}
