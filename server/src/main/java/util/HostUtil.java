package util;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostUtil {

    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            return "";
        }
    }

    public static String getUserHomeDir() {
        return System.getProperty("user.home");
    }

    public static String getUserDir() {
        return System.getProperty("user.dir");
    }

    public static String getPathSeparator() {
        return File.separator;
    }

    public static String getOsName() {
        return System.getProperty("os.name");
    }


}
