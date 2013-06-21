package nl.pelagic.util.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import nl.pelagic.util.i18n.Messages;

/**
 * File utilities
 */
public class FileUtils {
  /**
   * Determine if a file is the same as or below a directory (both files are
   * first resolved as canonical paths).
   * 
   * @param directory the directory. When null or not an existing directory then
   *          false is returned.
   * @param file the file. When null then false is returned.
   * @param allowEqual true to also return true when directory and file are
   *          equal
   * @return true when file is equal to directory or when file is below
   *         directory
   */
  public static boolean isFileBelowDirectory(File directory, File file, boolean allowEqual) {
    if ((directory == null) || !directory.isDirectory() || (file == null)) {
      return false;
    }

    try {
      String directoryPath = directory.getCanonicalPath();
      String filePath = file.getCanonicalPath();
      return ((allowEqual && directoryPath.equals(filePath))) || filePath.startsWith(directoryPath + File.separator);
    }
    catch (IOException e) {
      /* swallow, can't be covered by a test */
      return false;
    }
  }

  /**
   * Deletes the specified file. Directories are recursively deleted.<br>
   * Throws exception if any of the files could not be deleted.
   * 
   * @param file file to be deleted
   * @throws IllegalArgumentException when file is a root directory
   * @throws IOException if the file (or contents of a folder) could not be
   *           deleted
   */
  public static void deleteWithException(File file) throws IllegalArgumentException, IOException {
    File f = file.getAbsoluteFile();
    if (!f.exists()) {
      return;
    }
    if (f.getParentFile() == null) {
      throw new IllegalArgumentException(Messages.getString("FileUtils.0")); //$NON-NLS-1$
    }

    boolean wasDeleted = true;
    if (f.isDirectory()) {
      File[] subs = f.listFiles();
      for (File sub : subs) {
        try {
          deleteWithException(sub);
        }
        catch (IOException e) {
          /* can't be covered by a test */
          wasDeleted = false;
        }
      }
    }

    boolean fDeleted = f.delete();
    if (!fDeleted || !wasDeleted) {
      /* can't be covered by a test */
      throw new IOException(Messages.getString("FileUtils.1") + f.getPath()); //$NON-NLS-1$
    }
  }

  /**
   * Deletes the specified file. Directories are recursively deleted. If file(s)
   * cannot be deleted, no feedback on the specific error condition is provided.
   * 
   * @param file file to be deleted
   * @return true when deletion was performed successfully, false otherwise
   */
  public static boolean delete(File file) {
    try {
      deleteWithException(file);
      return true;
    }
    catch (IOException e) {
      /* can't be covered by a test */
      return false;
    }
  }

  /**
   * Copy an input file to an output file and creates directories leading up to
   * dst if needed.
   * <ul>
   * <li>If src is a file then it is simply copied into the destination file dst
   * </li>
   * <li>If src is a directory:</li>
   * <ul>
   * <li>dst must be a directory when it exists</li>
   * <li>dst is created as a directory when it doesn't exist</li>
   * <li>all files and directories in src are then recursively copied into dst</li>
   * </ul>
   * </ul>
   * 
   * @param src the input file
   * @param dst the output file
   * @throws IOException upon a create, read or write error
   * @throws FileAlreadyExistsException if the parent directory of dst could not
   *           be created (for example because it already exists as a file), or
   *           when the destination directory is equal to/below the source
   *           directory (when copying directories)
   * @throws FileNotFoundException when src does not exist or when dst exists
   *           but is a directory rather than a regular file, does not exist but
   *           cannot be created
   */
  public static void copy(File src, File dst) throws IOException, FileAlreadyExistsException, FileNotFoundException {
    if (src.isFile()) {
      File dstDir = dst.getParentFile();
      boolean dstDirCreated = false;
      try {
        dstDirCreated = DirUtils.mkdir(dstDir);
        dstDirCreated = true;
      }
      catch (Exception e) {
        /* swallow */
      }
      if (!dstDirCreated) {
        throw new FileAlreadyExistsException(String.format(Messages.getString("FileUtils.2"), //$NON-NLS-1$
            dstDir.getPath(), dst.getName()));
      }

      try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(src));
          BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(dst));) {
        byte[] buffer = new byte[256 * 1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
          out.write(buffer, 0, bytesRead);
        }
      }

      return;
    }

    if (src.isDirectory()) {
      if (isFileBelowDirectory(src, dst, true)) {
        throw new FileAlreadyExistsException(String.format(Messages.getString("FileUtils.5"), //$NON-NLS-1$
            dst.getPath(), src.getPath()));
      }

      File srcDirFiles[] = src.listFiles();
      if (srcDirFiles.length == 0) {
        if (!DirUtils.mkdir(dst)) {
          /* can't be covered by a test */
          throw new FileAlreadyExistsException(String.format(Messages.getString(Messages.getString("FileUtils.4")), //$NON-NLS-1$
              dst.getPath()));
        }
      } else {
        for (File srcDirFile : srcDirFiles) {
          copy(srcDirFile, new File(dst, srcDirFile.getName()));
        }
      }

      return;
    }

    throw new FileNotFoundException(String.format(Messages.getString("FileUtils.3"), src.getPath())); //$NON-NLS-1$
  }
}
