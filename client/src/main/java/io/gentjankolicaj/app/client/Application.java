package io.gentjankolicaj.app.client;

import io.gentjankolicaj.app.client.client.ClientThread;
import io.gentjankolicaj.app.client.config.GlobalConfig;
import io.gentjankolicaj.app.commons.gui.GuiFrame;

public class Application {

  private static final String FRAME_TITLE = "ClientGui-";

  public static void main(String[] args) {
    long pid = ProcessHandle.current().pid();
    GuiFrame guiFrame = new GuiFrame(FRAME_TITLE + pid, GlobalConfig.GUI_SIZE);

    Thread clientThread = new ClientThread(guiFrame);
    clientThread.start();
  }
}
