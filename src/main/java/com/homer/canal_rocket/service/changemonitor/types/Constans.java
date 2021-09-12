package com.homer.canal_rocket.service.changemonitor.types;

import java.util.regex.Pattern;

/**
 * @author CPYF-YI MAO
 * @date 2021/8/31 14:47
 */
public class Constans {

  /**
   * GTID全局事务标识符格式
   */
  public static final Pattern GTID_REGEX = Pattern.compile("\\S+(:)\\d+(-\\d+)?");
}
