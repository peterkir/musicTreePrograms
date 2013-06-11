package nl.pelagic.audio.musicTree.configuration.api;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import nl.pelagic.audio.musicTree.configuration.api.i18n.Messages;
import nl.pelagic.util.file.FileUtils;

/**
 * The configuration of the music tree
 */
public class MusicTreeConfiguration {
  /** the default flac base directory */
  public static final String DEFAULT_FLAC_BASE_DIRECTORY = "Music"; //$NON-NLS-1$

  /** the default mp3 base directory */
  public static final String DEFAULT_MP3_BASE_DIRECTORY = "from.flac"; //$NON-NLS-1$

  /** the flac base directory */
  private File flacBaseDir = new File(DEFAULT_FLAC_BASE_DIRECTORY);

  /** the mp3 base directory */
  private File mp3BaseDir = new File(DEFAULT_MP3_BASE_DIRECTORY);

  /**
   * Default Constructor
   */
  public MusicTreeConfiguration() {
    super();
  }

  /**
   * Constructor
   * 
   * @param flacBaseDir the flac base directory
   * @param mp3BaseDir the mp3 base directory
   */
  public MusicTreeConfiguration(File flacBaseDir, File mp3BaseDir) {
    super();

    this.flacBaseDir = flacBaseDir;
    this.mp3BaseDir = mp3BaseDir;
  }

  /**
   * @return the flacBaseDir
   */
  public File getFlacBaseDir() {
    return flacBaseDir;
  }

  /**
   * @param flacBaseDir the flacBaseDir to set
   */
  public void setFlacBaseDir(File flacBaseDir) {
    this.flacBaseDir = flacBaseDir;
  }

  /**
   * @return the mp3BaseDir
   */
  public File getMp3BaseDir() {
    return mp3BaseDir;
  }

  /**
   * @param mp3BaseDir the mp3BaseDir to set
   */
  public void setMp3BaseDir(File mp3BaseDir) {
    this.mp3BaseDir = mp3BaseDir;
  }

  /**
   * Validate: ensure that
   * <ul>
   * <li>the flac base directory is set AND is a directory</li>
   * <li>the mp3 base directory is set</li>
   * <li>the mp3 base directory is a directory (optional)</li>
   * <li>the mp3 base directory is NOT a sub-directory of the flac base
   * directory</li>
   * </ul>
   * 
   * @param validateMp3BaseDir if true, then also validate that mp3BaseDir is a
   *          directory.
   * 
   * @return null when validated, a list with error strings otherwise
   */
  public List<String> validate(boolean validateMp3BaseDir) {
    List<String> result = new LinkedList<>();

    /* check flac base directory exists */
    if ((flacBaseDir == null) || !flacBaseDir.isDirectory()) {
      result.add(String.format(
          Messages.getString("MusicTreeConfiguration.0"), flacBaseDir == null ? "null" : flacBaseDir.getPath())); //$NON-NLS-1$ //$NON-NLS-2$
    }

    /* check mp3 base directory exists */
    if (mp3BaseDir == null) {
      result.add(Messages.getString("MusicTreeConfiguration.3")); //$NON-NLS-1$
    }

    if (validateMp3BaseDir) {
      /* check mp3 base directory exists */
      if ((mp3BaseDir != null) && !mp3BaseDir.isDirectory()) {
        result.add(String.format(Messages.getString("MusicTreeConfiguration.1"), mp3BaseDir.getPath())); //$NON-NLS-1$
      }
    }

    /* check mp3 directory is NOT a sub directory of flac base directory */
    if (FileUtils.isFileBelowDirectory(flacBaseDir, mp3BaseDir)) {
      result.add(String.format(Messages.getString("MusicTreeConfiguration.2"), //$NON-NLS-1$
          mp3BaseDir.getPath(), flacBaseDir.getPath()));
    }

    if (result.size() == 0) {
      return null;
    }

    return result;
  }
}