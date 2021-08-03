import config.GlobalConfig;
import gui.GuiFrame;
import thread.ServerThread;

public class ServerApplication {

    public static void main(String[] args) {
        long pid = ProcessHandle.current().pid();
        GuiFrame guiFrame = new GuiFrame("ServerGui-" + pid, GlobalConfig.GUI_SIZE);

        Thread serverThread = new ServerThread(guiFrame);
        serverThread.start();
    }


}
