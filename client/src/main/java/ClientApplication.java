import client.ClientThread;
import config.GlobalConfig;
import gui.GuiFrame;

public class ClientApplication {

    public static void main(String[] args) {
        GuiFrame guiFrame = new GuiFrame("ClientGui", GlobalConfig.GUI_SIZE);

        Thread clientThread = new ClientThread(guiFrame);
        clientThread.start();
    }
}
