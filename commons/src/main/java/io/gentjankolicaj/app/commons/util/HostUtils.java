package io.gentjankolicaj.app.commons.util;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class HostUtils {

    private HostUtils() {
    }

    public static String getHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException uhe) {
            return StringUtils.EMPTY;
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
