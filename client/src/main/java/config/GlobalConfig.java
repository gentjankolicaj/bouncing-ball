package config;

import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class GlobalConfig {

    public static final String HOSTNAME = getHostname();
    public static final int PORT = 8000;
    public static final Dimension GUI_SIZE = new Dimension(600, 400);

    public static final long GUI_THREAD_SLEEP = 30; //millis
    public static final long SOCKET_MANAGER_THREAD_SLEEP = 5000; //millis
    public static final int SERVER_SOCKET_TIMEOUT = 2000; //millis


    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            return "";
        }
    }


}
