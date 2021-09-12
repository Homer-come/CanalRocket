package com.homer.canal_rocket.domain.user;



import lombok.Data;

import java.io.Serializable;

/**
 * @Author wangl
 * @Date 2021/1/28 17:07
 * @Version 1.0
 */
@Data
public class User implements Serializable {
  private static final long serialVersionUID = 5842295845640275831L;
  private Integer id;
  private String userName;
  private String passWord;
  private String realName;
}
