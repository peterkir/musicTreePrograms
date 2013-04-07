package nl.pelagic.util.file;

/**
 * File extension utilities
 */
public class ExtensionUtils {
  /** an empty string */
  public static final String emptyString = ""; //$NON-NLS-1$

  /**
   * Replace an extension
   * 
   * @param filename the filename
   * @param newExtension the new extension (do not forget to include the dot!)
   * @return null when filename is null, the filename with the new extension
   *         (null is ignored). Note: if the filename did not have an extension
   *         then the new extension is not appended to it
   */
  public static String replaceExtension(String filename, String newExtension) {
    if (filename == null) {
      return null;
    }

    String[] spl = split(filename, true);
    String file = spl[0];
    if ((newExtension == null) || spl[1].isEmpty()) {
      return file;
    }

    return file + newExtension;
  }

  /**
   * Split a filename and its extension
   * 
   * @param filename the filename to split
   * @param withDot if true then include the dot in the returned extension
   * @return the filename without extension in array index 0 (null when the
   *         filename is null) and extension in array index 1 (empty when the
   *         filename has no extension)
   */
  public static String[] split(String filename, boolean withDot) {
    String[] result = new String[2];
    result[0] = filename;
    result[1] = emptyString;

    if (filename == null) {
      return result;
    }

    int extensionDotPosition = filename.lastIndexOf('.');
    if (extensionDotPosition < 0) {
      return result;
    }

    /* filename without extension */
    result[0] = filename.substring(0, extensionDotPosition);

    /* extension */
    if (!withDot) {
      extensionDotPosition++;
    }
    result[1] = filename.substring(extensionDotPosition);

    return result;
  }
}
