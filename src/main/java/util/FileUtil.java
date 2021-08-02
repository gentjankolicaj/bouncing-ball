package util;

import java.io.File;

public class FileUtil {

    public static String getResourcePath(String resourceName) {
        String dirPath = HostUtil.getUserDir();
        String resourcePath = dirPath + HostUtil.getPathSeparator() + "src" + HostUtil.getPathSeparator() + "main" + HostUtil.getPathSeparator() + "resources" + HostUtil.getPathSeparator() + resourceName;
        return resourcePath;
    }

    public static File getResource(String resourceName) {
        String dirPath = HostUtil.getUserDir();
        String resourcePath = dirPath + HostUtil.getPathSeparator() + "src" + HostUtil.getPathSeparator() + "main" + HostUtil.getPathSeparator() + "resources" + HostUtil.getPathSeparator() + resourceName;
        File file = new File(resourcePath);
        if (file.exists())
            return file;
        else
            throw new RuntimeException("File : " + resourceName + " not found at : " + resourcePath);
    }

}
