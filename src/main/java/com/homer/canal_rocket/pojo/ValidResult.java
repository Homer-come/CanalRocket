package com.homer.canal_rocket.pojo;

import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * @author CPYF-YI MAO
 * @date 2020/11/6 11:15
 */
@Data
public class ValidResult {

  private boolean success;
  private List<String> messages;

  public ValidResult() {
  }

  public void throwError(Function<String, ? extends RuntimeException> exception) {
    if (!success) {
      throw exception.apply(String.join(" | ", messages));
    }
  }

  public static ValidResult fail(String message) {
    return fail(Collections.singletonList(message));
  }

  public static ValidResult fail(List<String> messages) {
    ValidResult result = new ValidResult();
    result.success = false;
    result.messages = messages;
    return result;
  }

  public static ValidResult success() {
    ValidResult result = new ValidResult();
    result.success = true;
    return result;
  }
}
