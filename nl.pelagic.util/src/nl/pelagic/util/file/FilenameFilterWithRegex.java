package nl.pelagic.util.file;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * <p>
 * A filename filter that can use a base path and/or a compiled regular
 * expression
 * </p>
 * <p>
 * The following functionality is implemented:
 * 
 * <pre>
 * basePath  regexPattern    Filter result
 * ----------------------------------------------------------------------------
 * A   null          null    accept all directories and files, in all
 *                           directories
 * B   null         !null    accept only directories and files that match the
 *                           regexPattern, in any directory
 * C  !null          null    accept all directories and files, in the basePath
 *                           directory and all directories below that
 * D  !null         !null    accept only directories and files that match the
 *                           regexPattern, in the basePath directory and all
 *                           directories below that
 * </pre>
 * 
 * </p>
 */
public class FilenameFilterWithRegex implements FilenameFilter {
  /** the base path */
  protected final File basePathFile;

  /** the base (canonical) path */
  protected final String basePath;

  /** the compiled regular expression */
  protected final Pattern regexPattern;

  /** the actual filename filter that is executed */
  private final FilenameFilter worker;

  /**
   * Constructor
   * 
   * @param base the base path
   * @param pattern the compiled regular expression
   * @throws IOException when a file could not be resolved as a canonical file
   */
  public FilenameFilterWithRegex(File base, Pattern pattern) throws IOException {
    super();
    basePathFile = base;
    regexPattern = pattern;

    if (basePathFile == null) {
      basePath = null;
    } else {
      basePath = basePathFile.getCanonicalPath();
    }

    /*
     * Create mode-dependent filename filters
     */

    if (basePath == null) {
      if (regexPattern == null) {
        /*
         * A
         */
        worker = new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {
            return true;
          }
        };
      } else {
        /*
         * B
         */
        worker = new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {
            return regexPattern.matcher(name).matches();
          }
        };
      }
    } else /* (basePath != null) */{
      if (regexPattern == null) {
        /*
         * C
         */
        worker = new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {
            return FileUtils.isFileBelowDirectory(basePathFile, new File(dir, name));
          }
        };
      } else {
        /*
         * D
         */
        worker = new FilenameFilter() {
          @Override
          public boolean accept(File dir, String name) {

            if (!FileUtils.isFileBelowDirectory(basePathFile, new File(dir, name))) {
              return false;
            }

            /* dir is equal to or below basePath */

            return regexPattern.matcher(name).matches();
          }
        };
      }
    }
  }

  @Override
  public boolean accept(File dir, String name) {
    return worker.accept(dir, name);
  }
}
