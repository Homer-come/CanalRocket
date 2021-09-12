package com.homer.canal_rocket.domain.user.mapper;


import com.homer.canal_rocket.domain.user.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author wangl
 * @Date 2021/1/28 17:28
 * @Version 1.0
 */

@Mapper
public interface UserMapper {

  User Sel(int id);

  int insert(User record);

}
