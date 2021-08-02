package server;

import config.GlobalConfig;
import gui.GuiFrame;

public class ServerApp {

    public static void main(String[] args) {
        GuiFrame guiFrame = new GuiFrame("ServerGui", GlobalConfig.GUI_SIZE);

        Thread serverThread = new ServerThread(guiFrame);
        serverThread.start();
    }


}
