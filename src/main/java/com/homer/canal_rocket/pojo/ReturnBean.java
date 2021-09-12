package com.homer.canal_rocket.pojo;


import lombok.Data;

import java.util.Map;

/**
 * 所有Controller返回值的父类
 */
@Data
public abstract class ReturnBean {

    abstract public Map<String, Object> toMap();
}
