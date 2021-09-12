package com.homer.canal_rocket.web;

/**
 * @Author wangl
 * @Date 2021/9/7 16:02
 * @Version 1.0
 */

import com.alibaba.druid.pool.DruidDataSource;
import com.homer.canal_rocket.pojo.AjaxReturnBean;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
//@RequestMapping("/mqMessageController")
public class MqMessageController {
  private static final Logger log = LoggerFactory.getLogger(MqMessageController.class);
//  @Resource
//  private DruidDataSource druidDataSource;

  @Autowired
  private RocketMQTemplate rocketMQTemplate;

  @RequestMapping("/push")
  public AjaxReturnBean get(@RequestParam("id") int id) {
//    rocketMQTemplate.convertAndSend("first-topic", "你好,Java旅途" + id);
//    druidDataSource.getUsername();
    return AjaxReturnBean.createSuccess();
  }

}