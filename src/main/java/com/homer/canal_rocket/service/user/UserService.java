package com.homer.canal_rocket.service.user;

import com.homer.canal_rocket.domain.user.User;
import com.homer.canal_rocket.domain.user.mapper.UserMapper;
import com.homer.canal_rocket.pojo.AjaxReturnBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author wangl
 * @Date 2021/9/6 10:26
 * @Version 1.0
 */
@Service
@Slf4j
public class UserService {

  @Resource
  private UserMapper userMapper;

  public AjaxReturnBean<?> getUser(Integer id) {
    User user = userMapper.Sel(id);
    return AjaxReturnBean.createSuccess(user);
  }

  public User getUserJson(Integer id) {
    User user = userMapper.Sel(id);
    return user;
  }

  @Async
  public void testMemory(Integer id) {
    log.info(Thread.currentThread().getName());
    int i = 10 / 0;
  }
}
