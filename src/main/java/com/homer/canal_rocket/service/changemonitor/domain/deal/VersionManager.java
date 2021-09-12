package com.homer.canal_rocket.service.changemonitor.domain.deal;

import com.homer.canal_rocket.service.changemonitor.exception.ChangeMonitorException;
import com.homer.canal_rocket.service.changemonitor.types.Gtid;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Set;

/**
 * 版本号管理
 *
 * @author CPYF-YI MAO
 * @date 2021/8/31 14:13
 */
@Slf4j
public class VersionManager {

  /**
   * 当前最新的全局事务标识
   */
  private Gtid currGtid;
  /**
   * 事务标识对应的已处理的实体集合
   */
  private final Set<String> entityIds = new HashSet<>();

  public VersionManager(Gtid currGtid, Set<String> entityIds) {
    this.currGtid = currGtid;
    if (entityIds != null && !entityIds.isEmpty()) {
      this.entityIds.addAll(entityIds);
    }
  }

  /**
   * 获取版本号
   *
   * @param gtid     全局事务标识
   * @param entityId 实体ID
   * @return 版本号
   */
  public long getVersion(Gtid gtid, String entityId) {
    if (gtid == null) {
      throw new ChangeMonitorException("GTID格式异常");
    }
    synchronized (entityIds) {
      long version = gtid.getVersion();
      if (currGtid != null) {
        final int compare = currGtid.compareTo(gtid);
        if (compare == 0) {
          //已存在同版本同实体ID的返回-1
          if (entityIds.contains(entityId)) {
            version = -1;
          }
          //当前版本大于传入版本时返回-1
        } else if (compare > 0) {
          version = -1;
        }
      }
      if (currGtid == null || !currGtid.equals(gtid)) {
        currGtid = gtid;
        entityIds.clear();
      }
      if (version != -1) {
        entityIds.add(entityId);
      }
      return version;
    }
  }
}
