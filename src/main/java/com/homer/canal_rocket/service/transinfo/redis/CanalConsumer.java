package com.homer.canal_rocket.service.transinfo.redis;

/**
 * @Author wangl
 * @Date 2021/9/13 13:47
 * @Version 1.0
 */

import com.alibaba.fastjson.JSON;
import com.homer.canal_rocket.domain.user.User;
import com.homer.canal_rocket.service.transinfo.CanalBean;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yzy
 */
@Slf4j
@Component
@RocketMQMessageListener(topic = "canaltopic", consumerGroup = "CanalConsumerGroup")
public class CanalConsumer implements RocketMQListener<String> {

  @Override
  public void onMessage(String msg) {
    log.info("ConsumerGroup:Consumer收到" + msg);
    CanalBean canalBean = JSON.parseObject(String.valueOf(msg), CanalBean.class);
    String table = canalBean.getTable();
    System.out.println(table);
    String type = canalBean.getType();
    System.out.println(type);
    List<User> data = canalBean.getData();
    data.stream().forEach(tbTest -> {
      System.out.println(tbTest.toString());
      if ("UPDATE".equals(type) && "user".equals(table)) {
//        Optional<EsCmsArticle> article = cmsArticleRepository.findById(tbTest.getCourseId());
        //删除缓存
        //操作es
//        if (article.isPresent()) {
//          EsCmsArticle cmsArticle = article.get();
//          BeanUtils.copyProperties(tbTest, cmsArticle);
//          cmsArticleRepository.save(cmsArticle);
//          logger.info("id = {} 编辑es成功", cmsArticle.getCourseId());
//        } else {
//          BeanUtils.copyProperties(tbTest, esCmsArticle);
//          cmsArticleRepository.save(esCmsArticle);
//          logger.info("id = {} 添加es成功", esCmsArticle.getCourseId());
//        }
      } else if ("INSERT".equals(type) && "cms_article".equals(table)) {
//        BeanUtils.copyProperties(tbTest, esCmsArticle);
        //添加缓存
        //操作es
//        cmsArticleRepository.save(esCmsArticle);
//        logger.info("id = {} 添加es成功", esCmsArticle.getCourseId());
      }
    });
  }
}