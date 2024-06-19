package io.gentjankolicaj.app.commons.util;

import java.io.File;

public class FileUtils {

  private FileUtils() {
  }

  public static String getResourcePath(String resourceName) {
    String dirPath = HostUtils.getUserDir();
    String resourcePath =
        dirPath + HostUtils.getPathSeparator() + "src" + HostUtils.getPathSeparator() + "main"
            + HostUtils.getPathSeparator() + "resources" + HostUtils.getPathSeparator()
            + resourceName;
    return resourcePath;
  }

  public static File getResource(String resourceName) {
    String dirPath = HostUtils.getUserDir();
    String resourcePath =
        dirPath + HostUtils.getPathSeparator() + "src" + HostUtils.getPathSeparator() + "main"
            + HostUtils.getPathSeparator() + "resources" + HostUtils.getPathSeparator()
            + resourceName;
    File file = new File(resourcePath);
    if (file.exists()) {
      return file;
    } else {
      throw new RuntimeException("File : " + resourceName + " not found at : " + resourcePath);
    }
  }

}
