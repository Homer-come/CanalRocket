package com.homer.canal_rocket.web;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.fastjson.JSONObject;
import com.homer.canal_rocket.domain.user.User;
import com.homer.canal_rocket.pojo.AjaxReturnBean;
import com.homer.canal_rocket.service.changemonitor.application.ChangeMonitorService;
import com.homer.canal_rocket.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author wangl
 * @Date 2021/9/6 10:26
 * @Version 1.0
 */
@RestController
@Slf4j
public class DemoController {

  @Resource
  private UserService userService;
  @Autowired
  private DruidDataSource druidDataSource;
  @Resource
  private ChangeMonitorService changeMonitorService;

  @GetMapping("/get/user/{id}")
  public AjaxReturnBean<?> getUser(@PathVariable Integer id) {
//    changeMonitorService.execOnceChangeDeal();
    log.info(druidDataSource.getPassword());
    return userService.getUser(id);
  }

  @GetMapping("/get/user/{id}")
  public void getUserJSON(@PathVariable Integer id, HttpServletRequest request, HttpServletResponse response) {
    User user = userService.getUserJson(id);
    String jsonString = JSONObject.toJSONString(user);
    try {
      PrintWriter writer = response.getWriter();
      writer.println(jsonString);
    } catch (IOException e) {
      log.error(e.getMessage());
    }
  }

  @GetMapping("/test/{id}")
  public void testMemory(@PathVariable Integer id) {
    log.info(Thread.currentThread().getName() + "正在执行任务");
    for (int i = 0; i < id; i++) {
      userService.testMemory(id);
    }
  }

}
