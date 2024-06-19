package io.gentjankolicaj.app.client.config;

import io.gentjankolicaj.app.commons.util.HostUtils;
import java.awt.Dimension;

public class GlobalConfig {

  public static final String HOSTNAME = HostUtils.getHostname();
  public static final int PORT = 8888;
  public static final Dimension GUI_SIZE = new Dimension(600, 400);
  public static final long GUI_THREAD_SLEEP = 30; //millis

  private GlobalConfig() {
  }

}
