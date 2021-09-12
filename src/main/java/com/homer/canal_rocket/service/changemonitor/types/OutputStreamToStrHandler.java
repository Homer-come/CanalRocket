package com.homer.canal_rocket.service.changemonitor.types;

import lombok.AllArgsConstructor;
import org.apache.commons.exec.PumpStreamHandler;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

/**
 * @author CPYF-YI MAO
 * @date 2021/8/27 10:06
 */
@AllArgsConstructor
public class OutputStreamToStrHandler extends PumpStreamHandler {

  StringWriter sw;

  @Override
  public void setProcessOutputStream(InputStream is) {
    try {
      IOUtils.copy(is, sw);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
