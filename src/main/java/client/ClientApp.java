package client;

import gui.GuiFrame;

import java.awt.*;

public class ClientApp {
    static final Dimension GUI_SIZE = new Dimension(700, 500);

    public static void main(String[] args) {
        GuiFrame guiFrame = new GuiFrame("ClientGui", GUI_SIZE);

        Thread clientThread = new ClientThread(guiFrame);
        clientThread.start();
    }
}
