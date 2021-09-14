package com.homer.canal_rocket.service.rocketmq;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.ConsumeMode;
import org.apache.rocketmq.spring.annotation.MessageModel;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.springframework.stereotype.Component;

/**
 * 设置消息监听
 * 1.监听组(consumerGroup)：监听topic(topic)：监听tag(selectorExpression)(默认监听topic下所有)
 * 2.监听消费模式(messageModel):默认负载均衡：CLUSTERING（每一个消息只发给一个消费者）、广播模式：BROADCASTING（发送给所有消费者）
 * 3.设置顺序消息处理模式(consumeMode)（默认是所有线程可以处理同一个消息队列（ConsumeMode.CONCURRENTLY）,当前消息没有线程在执行时其他线程才能够执行（ConsumeMode.ORDERLY）。
 * ps:一个线程顺序执行一个队列表时消息监听必须使用负载均衡messageModel = MessageModel.BROADCASTING）
 */
@Component
@RocketMQMessageListener(consumerGroup = "${rocketmq.consumer.group}", topic = "${rocketmq.consumer.topic}",
  selectorExpression = "${rocketmq.consumer.tags}", messageModel = MessageModel.CLUSTERING,
  consumeMode = ConsumeMode.ORDERLY)
@Slf4j
public class TestConsumer implements RocketMQListener<String> {
  @Override
  public void onMessage(String message) {
    log.info(message);

    try {
      Thread.sleep(10 * 1000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
