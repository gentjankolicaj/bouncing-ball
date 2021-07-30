package client;

import gui.GuiFrame;

public class ClientThread extends Thread {
    private final GuiFrame guiFrame;

    public ClientThread(GuiFrame guiFrame) {
        super();
        this.guiFrame = guiFrame;
    }

    public void run() {
        while (true) {
            guiFrame.moveBall();

            guiFrame.repaintFrame();

            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }


}
