package nl.pelagic.audio.musicTree.util;

import java.io.File;
import java.io.IOException;

import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConfiguration;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;
import nl.pelagic.util.file.ExtensionUtils;
import nl.pelagic.util.file.FileUtils;

/**
 * Helpers for the music tree
 */
public class MusicTreeHelpers {

  /**
   * Convert a flac file (from below the base directory of the flac files tree)
   * to a mp3 file (below the directory in which the tree with flac files must
   * be converted as mp3 files). Replaces a .flac extension by a .mp3 extension
   * but leaves other extensions alone.
   * 
   * @param musicTreeConfiguration the music tree configuration
   * @param flacFile the flac file. If null, then the base directory of the flac
   *          files tree is converted.
   * @return null if the flac file is not below the base directory of the flac
   *         files tree or when a path can't be resolved, the converted file
   *         otherwise
   */
  static public File flacFileToMp3File(MusicTreeConfiguration musicTreeConfiguration, File flacFile) {
    assert (musicTreeConfiguration != null);
    assert (musicTreeConfiguration.validate(false) == null);
    assert (flacFile != null);

    if (!FileUtils.isFileBelowDirectory(musicTreeConfiguration.getFlacBaseDir(), flacFile, true)) {
      return null;
    }

    String flacBaseDirPath;
    String flacFilePath;
    try {
      flacBaseDirPath = musicTreeConfiguration.getFlacBaseDir().getParentFile().getCanonicalPath();
      flacFilePath = flacFile.getCanonicalPath();
    }
    catch (IOException e) {
      /* can't be covered by a test */
      return null;
    }

    String relativeFlacFile = flacFilePath.substring(flacBaseDirPath.length() + File.separator.length());

    String[] relativeFlacFileSplit = ExtensionUtils.split(relativeFlacFile, true);
    if (relativeFlacFileSplit[1].equals(MusicTreeConstants.FLACEXTENSION)) {
      relativeFlacFile = relativeFlacFileSplit[0] + MusicTreeConstants.MP3EXTENSION;
    }

    return new File(musicTreeConfiguration.getMp3BaseDir(), relativeFlacFile);
  }
}
