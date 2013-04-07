package nl.pelagic.util.file;

import java.io.File;
import java.nio.file.FileAlreadyExistsException;

import nl.pelagic.util.i18n.Messages;

/**
 * Directory utilities
 */
public class DirUtils {
  /**
   * Create a directory (and all directories leading up to it).
   * 
   * @param directory the directory to create
   * @return true when the directory already exists or when successfully
   *         created, false otherwise
   * @throws FileAlreadyExistsException if directory already exists as a file
   */
  public static boolean mkdir(File directory) throws FileAlreadyExistsException {
    if (directory == null) {
      return false;
    }

    if (directory.exists()) {
      if (directory.isDirectory()) {
        return true;
      }

      throw new FileAlreadyExistsException(String.format(
          Messages.getString("DirUtils.0"), directory.getPath())); //$NON-NLS-1$
    }

    return directory.mkdirs();
  }
}
