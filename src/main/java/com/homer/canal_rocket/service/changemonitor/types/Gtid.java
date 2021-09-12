package com.homer.canal_rocket.service.changemonitor.types;

import com.homer.canal_rocket.service.changemonitor.exception.ChangeMonitorException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 全局事务标识符
 *
 * @author CPYF-YI MAO
 * @date 2021/8/31 14:41
 */
@Slf4j
@Getter
public class Gtid implements Comparable<Gtid> {

  /**
   * GTID格式为：590b498c-3b75-11eb-ae36-309c23e3e10d:1 或 590b498c-3b75-11eb-ae36-309c23e3e10d:1-7
   */
  private final String gtid;
  /**
   * 590b498c-3b75-11eb-ae36-309c23e3e10d标识mysql实例id
   */
  private final String server_uuid;
  /**
   * 取最后尾数标识当前版本号
   * 例如：
   * 590b498c-3b75-11eb-ae36-309c23e3e10d:1   取1为当前版本号
   * 590b498c-3b75-11eb-ae36-309c23e3e10d:1-7 取7为当前版本号
   */
  private final long version;

  public Gtid(String gtid) {
    if (StringUtils.isBlank(gtid) || !Constans.GTID_REGEX.matcher(gtid).matches()) {
      throw new ChangeMonitorException("GTID格式异常");
    }
    this.gtid = gtid;
    final String[] split1 = this.gtid.split(":");
    server_uuid = split1[0];
    final String[] split2 = split1[1].split("-");
    version = split2.length == 1 ? Long.parseLong(split2[0]) : Long.parseLong(split2[1]);
  }

  @Override
  public String toString() {
    return gtid;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Gtid gtid1 = (Gtid) o;
    return Objects.equals(gtid, gtid1.gtid);
  }

  @Override
  public int hashCode() {
    return Objects.hash(gtid);
  }

  @Override
  public int compareTo(Gtid o2) {
    if (this.equals(o2)) {
      return 0;
    }
    if (!this.server_uuid.equals(o2.server_uuid)) {
      log.error("非同一实例的GTID[{},{}]比较", this, o2);
      throw new ChangeMonitorException("非同一实例的GTID比较");
    }
    return this.version - o2.version > 0 ? 1 : -1;
  }
}
