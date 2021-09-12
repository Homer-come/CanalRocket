package com.homer.canal_rocket.util;

import com.homer.canal_rocket.pojo.ValidResult;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * 校验工具类
 *
 * @author CPYF-YI MAO
 * @date 2020/11/6 11:09
 */
public final class ValidationUtils {

  /**
   * 开启快速结束模式 failFast (true)
   */
  private static final Validator FAIL_FAST_VALIDATOR = Validation.byProvider(HibernateValidator.class)
    .configure()
    .failFast(true)
    .buildValidatorFactory().getValidator();

  /**
   * 全部校验
   */
  private static final Validator VALIDATOR = Validation.buildDefaultValidatorFactory().getValidator();

  /**
   * 注解验证参数(快速失败模式)
   *
   * @param obj ValidResult
   */
  public static <T> ValidResult fastFail(T obj) {
    Set<ConstraintViolation<T>> constraintViolations = FAIL_FAST_VALIDATOR.validate(obj);
    //返回异常result
    if (constraintViolations.size() > 0) {
      return ValidResult.fail(constraintViolations.iterator().next().getMessage());
    }
    return ValidResult.success();
  }

  /**
   * 注解验证参数(全部校验)
   *
   * @param obj ValidResult
   */
  public static <T> ValidResult checkAll(T obj) {
    Set<ConstraintViolation<T>> constraintViolations = VALIDATOR.validate(obj);
    //返回异常result
    if (constraintViolations.size() > 0) {
      List<String> errorMessages = new LinkedList<>();
      for (ConstraintViolation<T> violation : constraintViolations) {
        errorMessages.add(String.format("%s:%s", violation.getPropertyPath().toString(), violation.getMessage()));
      }
      return ValidResult.fail(errorMessages);
    }
    return ValidResult.success();
  }
}
