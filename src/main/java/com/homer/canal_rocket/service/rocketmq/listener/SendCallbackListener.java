package com.homer.canal_rocket.service.rocketmq.listener;

/**
 * @Author wangl
 * @Date 2021/9/14 14:53
 * @Version 1.0
 */

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;

/**
 * rocketmq异步回调监听
 * @author homer
 */
@Slf4j
public class SendCallbackListener implements SendCallback {

  private int id;

  public SendCallbackListener(int id) {
    this.id = id;
  }

  @Override
  public void onSuccess(SendResult sendResult) {
    log.info("CallBackListener on success : " + JSONObject.toJSONString(sendResult));
  }

  @Override
  public void onException(Throwable throwable) {
    log.error("CallBackListener on exception : ", throwable);
  }
}

