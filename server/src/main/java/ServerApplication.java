import config.GlobalConfig;
import gui.GuiFrame;
import server.ServerThread;

public class ServerApplication {

    public static void main(String[] args) {
        GuiFrame guiFrame = new GuiFrame("ServerGui", GlobalConfig.GUI_SIZE);

        Thread serverThread = new ServerThread(guiFrame);
        serverThread.start();
    }


}
