import client.ClientThread;
import config.GlobalConfig;
import gui.GuiFrame;

public class ClientApplication {

    public static void main(String[] args) {
        long pid = ProcessHandle.current().pid();
        GuiFrame guiFrame = new GuiFrame("ClientGui-" + pid, GlobalConfig.GUI_SIZE);

        Thread clientThread = new ClientThread(guiFrame);
        clientThread.start();
    }
}
