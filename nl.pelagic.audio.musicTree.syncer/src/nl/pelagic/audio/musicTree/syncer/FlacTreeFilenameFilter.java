package nl.pelagic.audio.musicTree.syncer;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import nl.pelagic.audio.musicTree.syncer.i18n.Messages;
import nl.pelagic.util.file.ExtensionUtils;

/**
 * Filter file names based on a number of configured (lower-cased) extensions
 * and filenames. Always accepts directories if so configured.
 */
public class FlacTreeFilenameFilter implements FilenameFilter {
  /** if true then accept directories */
  private boolean acceptDirectories = true;

  /** the extensions to accept */
  private Set<String> extensions = null;

  /** the file names to accept */
  private Set<String> fileNames = null;

  /**
   * Constructor
   * 
   * @param extensions the extensions to accept; all must include the dot and
   *          are lower-cased before processing (optional)
   * @param fileNames the file names to accept (optional)
   * @param acceptDirectories if true then accept directories
   */
  public FlacTreeFilenameFilter(Set<String> extensions, Set<String> fileNames, boolean acceptDirectories) {
    super();

    if ((extensions != null) && (extensions.size() > 0)) {
      Locale locale = Locale.getDefault();
      this.extensions = new TreeSet<>();
      for (String extension : extensions) {
        if ((extension.length() == 0) || (extension.charAt(0) != '.')) {
          throw new ExceptionInInitializerError(String.format(Messages.getString("FlacTreeFilenameFilter.0"), //$NON-NLS-1$
              extension));
        }
        this.extensions.add(extension.toLowerCase(locale));
      }
    }

    if ((fileNames != null) && (fileNames.size() > 0)) {
      this.fileNames = new TreeSet<>();
      for (String fileName : fileNames) {
        if (fileName.length() == 0) {
          throw new ExceptionInInitializerError(String.format(Messages.getString("FlacTreeFilenameFilter.1"), fileName)); //$NON-NLS-1$
        }
        this.fileNames.add(fileName);
      }
    }

    this.acceptDirectories = acceptDirectories;
  }

  @Override
  public boolean accept(File dir, String name) {
    if ((dir == null) || (name == null)) {
      return false;
    }

    /* accept directories when enabled */
    if (acceptDirectories && new File(dir, name).isDirectory()) {
      return true;
    }

    /* accept if the extension is in the configured extensions */
    if ((extensions != null)
        && extensions.contains(ExtensionUtils.split(name, true)[1].toLowerCase(Locale.getDefault()))) {
      return true;
    }

    /* accept if the file name is in the configured file names */
    if ((fileNames != null) && fileNames.contains(name)) {
      return true;
    }

    /* do not accept in other case */
    return false;
  }
}
