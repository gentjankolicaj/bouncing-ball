package io.gentjankolicaj.app.server;

import io.gentjankolicaj.app.commons.gui.GuiFrame;
import io.gentjankolicaj.app.server.config.GlobalConfig;
import io.gentjankolicaj.app.server.thread.ServerThread;

public class Application {

  private static final String FRAME_TITLE = "ServerGui-";

  public static void main(String[] args) {
    long pid = ProcessHandle.current().pid();
    GuiFrame guiFrame = new GuiFrame(FRAME_TITLE + pid, GlobalConfig.GUI_SIZE);

    Thread serverThread = new ServerThread(guiFrame);
    serverThread.start();
  }


}
