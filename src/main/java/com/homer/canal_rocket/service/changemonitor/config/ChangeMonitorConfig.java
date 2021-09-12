package com.homer.canal_rocket.service.changemonitor.config;

import com.homer.canal_rocket.service.changemonitor.domain.canal.CanalClient;
import com.homer.canal_rocket.service.changemonitor.domain.canal.CanalMonitor;
import com.homer.canal_rocket.service.changemonitor.domain.canal.CanalServer;
import com.homer.canal_rocket.service.changemonitor.domain.deal.VersionManager;
import com.homer.canal_rocket.service.changemonitor.exception.ChangeMonitorException;
import com.homer.canal_rocket.service.changemonitor.types.CanalAheadConfig;
import com.homer.canal_rocket.service.changemonitor.types.CanalConfig;
import com.homer.canal_rocket.service.changemonitor.types.CanalServerConf;
import com.homer.canal_rocket.service.changemonitor.types.Gtid;
import com.homer.canal_rocket.service.changemonitor.types.InstanceConfig;
import com.homer.canal_rocket.util.ValidationUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author CPYF-YI MAO
 * @date 2021/8/31 18:45
 */
@Slf4j
@Configuration
@ConditionalOnExpression("${monitor.database.canal.enable:false}")
public class ChangeMonitorConfig {

  @Value("${monitor.database.canal.home}")
  private String canalHome;
  /**
   * 过滤不需要监听的数据表
   */
  private static final List<String> FILTER_TABLES_REGEX = Arrays.asList();

  /**
   * 预读配置信息
   * 方便统一获取部分配置数据
   *
   * @return CanalAheadConfig
   */
  @Bean
  public CanalAheadConfig canalAheadConfig() {
    CanalAheadConfig aheadConfig = new CanalAheadConfig();
    aheadConfig.setDbSchema("canal_test");
    aheadConfig.setDbIp("127.0.0.1");
    aheadConfig.setDbPort("3306");
    aheadConfig.setDbUsername("root");
    aheadConfig.setDbPassword("123456");
    //校验必要参数
//    ValidationUtils.checkAll(aheadConfig).throwError((Function<String, ? extends RuntimeException>) new RuntimeException("校验不通过"));
    return aheadConfig;
  }


  @Bean
  public CanalServer canalServer() {
    final CanalAheadConfig aheadConfig = canalAheadConfig();
    CanalConfig canalConfig = new CanalConfig("127.0.0.1", aheadConfig.getDbSchema());
    InstanceConfig instanceConfig = new InstanceConfig(
      new InetSocketAddress(aheadConfig.getDbIp(), Integer.parseInt(aheadConfig.getDbPort())),
      aheadConfig.getDbUsername(),
      aheadConfig.getDbPassword(),
      //监听指定数据库实例的指定库 | schema\\..*
      String.format("%s\\..*", aheadConfig.getDbSchema()),
      //过滤部分不需要监听的表 | schema.table1,schema.table2,schema.prefix_*
      FILTER_TABLES_REGEX.stream()
        .map(table -> aheadConfig.getDbSchema() + "." + table)
        .collect(Collectors.joining(",")));
    return new CanalServer(canalHome, canalConfig, instanceConfig);
  }

  @Bean
  public CanalClient canalClient() {
    final CanalAheadConfig aheadConfig = canalAheadConfig();
    return new CanalClient(new CanalServerConf(
      new InetSocketAddress("127.0.0.1", 11111), aheadConfig.getDbSchema()));
  }

  @Bean
  public CanalMonitor canalMonitor() {
    return new CanalMonitor(canalClient());
  }

//  @Bean
//  public VersionManager versionManager() {
//    //系统变量的最大GTID
//    final Optional<SystemVariable> variable =
//      systemVariableService.getVariable(SysVarTypeEnum.Define, DefSysVarEnum.MAX_GTID);
//    //增量表记录的最大版本
//    final Long maxVersionNo = modifyRecordInter.queryMaxVersionNo();
//    //若变量和表记录都不存在时
//    if (maxVersionNo == null && !variable.isPresent()) {
//      return new VersionManager(null, Collections.emptySet());
//    }
//    final SystemVariable maxGtid = variable.orElseThrow(() -> new ChangeMonitorException("系统变量max_gtid不存在"));
//    final Gtid gtid = new Gtid(maxGtid.getValue());
//    //系统变量与表最大记录不同，取最大值作为基准
//    if (maxVersionNo != null && gtid.getVersion() != maxVersionNo) {
//      log.error("系统变量max_gtid[{}]与表最大记录[{}]不同", gtid.getVersion(), maxVersionNo);
//      throw new ChangeMonitorException("系统变量max_gtid与表最大记录不同");
//    }
//    final List<String> entityIds = modifyRecordInter.queryEntityIdByVersion(gtid.getVersion());
//    return new VersionManager(gtid, new HashSet<>(entityIds));
//  }


}
