package nl.pelagic.audio.musicTree.syncer;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;
import nl.pelagic.util.file.ExtensionUtils;

/**
 * Container to hold a list of file and directories by category (directories,
 * music files, covers, and other files).
 */
public class FileListSplit {
  /** the directory for which the split is performed */
  File directory = null;

  /** true when no directory files were evaluated */
  boolean noDirectoryFiles = true;

  /** the directories */
  List<String> directories = new LinkedList<>();

  /** the music file names */
  List<String> musicFiles = new LinkedList<>();

  /** the music file names without the extensions */
  List<String> musicFilesWithoutExtensions = new LinkedList<>();

  /** the extensions of the music file names (extensions include the dot) */
  List<String> musicFilesExtensions = new LinkedList<>();

  /** the file names of the cover files */
  List<String> covers = new LinkedList<>();

  /** the file names of the other files */
  List<String> otherFiles = new LinkedList<>();

  /**
   * Split a list of files into categories (directories, music files, covers,
   * and other files).
   * 
   * @param directory the directory of the files
   * @param directoryFiles the files in the directory
   * @param extension the file extension of music files. When null then the
   *          files are not filtered on extension.
   */
  public FileListSplit(File directory, String[] directoryFiles, String extension) {
    if (directory == null) {
      return;
    }

    this.directory = directory;

    if ((directoryFiles == null) || (directoryFiles.length == 0)) {
      return;
    }

    Arrays.sort(directoryFiles);

    for (String directoryFile : directoryFiles) {
      if (directoryFile == null) {
        continue;
      }

      noDirectoryFiles = false;

      if (new File(directory, directoryFile).isDirectory()) {
        this.directories.add(directoryFile);
        continue;
      }

      if (MusicTreeConstants.COVER.equals(directoryFile)) {
        this.covers.add(directoryFile);
        continue;
      }

      String[] directoryFileSplit = ExtensionUtils.split(directoryFile, true);
      String directoryFileExtension = directoryFileSplit[1];
      if ((extension != null) && extension.equals(directoryFileExtension)) {
        this.musicFiles.add(directoryFile);
        this.musicFilesWithoutExtensions.add(directoryFileSplit[0]);
        this.musicFilesExtensions.add(directoryFileExtension);
      } else {
        this.otherFiles.add(directoryFile);
      }
    }
  }
}
