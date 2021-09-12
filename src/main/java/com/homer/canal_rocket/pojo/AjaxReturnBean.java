package com.homer.canal_rocket.pojo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * 异步请求返回实体
 *
 * @author YF-zhuf
 * @date 2020/4/24 18:38
 */
@ApiModel
@Getter
@ToString
public final class AjaxReturnBean<T> extends ReturnBean{

  /**
   * 标识码，需要特别维护来源于 enum Code
   */
  @ApiModelProperty(value = "编码")
  private int code;
  /**
   * 提示信息
   */
  @ApiModelProperty(value = "提示信息")
  private String msg;
  /**
   * 返回数据
   */
  @ApiModelProperty(value = "数据")
  private T data;
  /**
   * 分页时总条数，不分页时默认返回0
   */
  @ApiModelProperty(value = "总数|分页时可用!")
  private long count;

  private AjaxReturnBean(int code, String msg, T data, long count) {
    this.code = code;
    this.msg = msg;
    this.data = data;
    this.count = count;
  }

  /**
   * 成功
   *
   * @return 异步请求返回实体
   */
  public static <T> AjaxReturnBean<T> createSuccess() {
    return new AjaxReturnBean<>(Code.SUCCESS.valueOf(), "", null, 0);
  }

  /**
   * 成功
   *
   * @param data 数据
   * @return 异步请求返回实体
   */
  public static <T> AjaxReturnBean<T> createSuccess(T data) {
    return new AjaxReturnBean<>(Code.SUCCESS.valueOf(), "SUCCESS", data, 0);
  }

  /**
   * 成功
   *
   * @param msg 提示信息
   * @return 异步请求返回实体
   */
  public static <T> AjaxReturnBean<T> createSuccess(String msg) {
    return new AjaxReturnBean<>(Code.SUCCESS.valueOf(), msg, null, 0);
  }

  /**
   * 成功
   *
   * @param msg  提示信息
   * @param data 返回数据
   * @return 异步请求返回实体
   */
  public static <T> AjaxReturnBean<T> createSuccess(String msg, T data) {
    return new AjaxReturnBean<>(Code.SUCCESS.valueOf(), msg, data, 0);
  }

  /**
   * 成功
   *
   * @param msg   提示信息
   * @param data  返回数据
   * @param count 总页数
   * @return 异步请求返回实体
   */
  public static <T> AjaxReturnBean<T> createSuccess(String msg, T data, long count) {
    return new AjaxReturnBean<>(Code.SUCCESS.valueOf(), msg, data, count);
  }

  /**
   * 失败
   *
   * @param msg 提示信息
   * @return 异步请求返回实体
   */
  public static <T> AjaxReturnBean<T> createError(String msg) {
    return new AjaxReturnBean<>(Code.ERROR.valueOf(), msg, null, 0);
  }

  /**
   * 自定错误码异常 Code
   * @param msg 提示信息
   * @return 异步请求返回实体
   */
  public static <T> AjaxReturnBean<T> createError(Code code, String msg) {
    return new AjaxReturnBean<>(code.valueOf(), msg, null, 0);
  }

  /**
   * 自定错误码异常 HttpStatus
   * @param msg 提示信息
   * @return 异步请求返回实体
   */
  public static <T> AjaxReturnBean<T> createError(HttpStatus code, String msg) {
    return new AjaxReturnBean<>(code.value(), msg, null, 0);
  }

  /**
   * 失败
   *
   * @param msg 提示信息
   * @return 异步请求返回实体
   */
  public static <T> AjaxReturnBean<T> createError(String msg, T data) {
    return new AjaxReturnBean<>(Code.ERROR.valueOf(), msg, data, 0);
  }

  /**
   * 判定成功
   *
   * @return 是否
   */
  public boolean success() {
    return Code.SUCCESS.valueOf() == this.code;
  }



  @Override
  public Map<String, Object> toMap() {
    Map<String, Object> map = new HashMap<>(4);
    map.put("code", this.code);
    map.put("msg", this.msg);
    map.put("data", this.data);
    map.put("count", this.count);
    return map;
  }

  /**
   * 标识码
   * 返回正确预期结果 "0",非预期错误“9999”
   * 返回错误预期结果 编码格式 错误类型（1位）+错误编号（3位）共4位，
   * 错误类型分为：数据校验错误（6XXX）、逻辑出错（7XXX）、系统内部出错（8XXX）、外部设备/接口出错（9XXX）
   */
  public enum Code {

    /**
     * 成功
     */
    SUCCESS(0),
    /**
     * 登陆状态失效
     */
    INVALID(1),
    /**
     * 失败
     */
    ERROR(-1);

    private int value;

    Code(int value) {
      this.value = value;
    }

    public int valueOf() {
      return this.value;
    }

  }
}