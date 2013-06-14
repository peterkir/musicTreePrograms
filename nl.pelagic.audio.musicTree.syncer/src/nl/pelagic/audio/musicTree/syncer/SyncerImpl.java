package nl.pelagic.audio.musicTree.syncer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
import nl.pelagic.audio.conversion.flac2mp3.api.FlacToMp3;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConfiguration;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;
import nl.pelagic.audio.musicTree.syncer.api.Syncer;
import nl.pelagic.audio.musicTree.syncer.i18n.Messages;
import nl.pelagic.shell.script.listener.api.ShellScriptListener;
import nl.pelagic.util.file.FileUtils;
import nl.pelagic.util.string.StringUtils;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * The music tree synchroniser: mirrors a tree of flac files into a tree of mp3
 * files
 */
@Component
public class SyncerImpl implements Syncer {
  /** the logger */
  private Logger logger = Logger.getLogger(this.getClass().getName());

  /** the shell script listener (optional) */
  private AtomicReference<ShellScriptListener> shellScriptListener = new AtomicReference<>();

  /**
   * @param shellScriptListener the shellScriptListener to set
   */
  @Reference(type = '?')
  void setShellScriptListener(ShellScriptListener shellScriptListener) {
    this.shellScriptListener.set(shellScriptListener);
  }

  /**
   * @param shellScriptListener the shellScriptListener to unset
   */
  void unsetShellScriptListener(ShellScriptListener shellScriptListener) {
    this.shellScriptListener.compareAndSet(shellScriptListener, null);
  }

  /** the flac to mp3 converter */
  private FlacToMp3 flacToMp3 = null;

  /**
   * @param flacToMp3 the flacToMp3 to set
   */
  @Reference
  void setFlacToMp3(FlacToMp3 flacToMp3) {
    this.flacToMp3 = flacToMp3;
  }

  /**
   * Remove directories/files from the mp3 directory that are not in the
   * corresponding flac directory.
   * 
   * @param mp3Dir the mp3 directory
   * @param mp3Entries the directories/files that are in the mp3 directory. When
   *          null or empty then nothing is done.
   * @param mp3EntriesFullName the full names of the directories/files in
   *          mp3entries. When null then mp3Entries is used instead, otherwise
   *          the lists must be the same size and the entries must be in the
   *          same order in both lists.
   * @param flacEntries the directories/files that are in the corresponding flac
   *          directory. When null then all mp3Entries are removed.
   * @param simulate true to simulate removal
   * @throws IllegalArgumentException when mp3EntriesFullName is not null and
   *           mp3Entries and mp3EntriesFullName don't have the same size
   */
  void removeFromMp3Dir(File mp3Dir, List<String> mp3Entries, List<String> mp3EntriesFullName,
      List<String> flacEntries, boolean simulate) {
    assert (mp3Dir != null);

    if (!mp3Dir.exists()) {
      return;
    }

    if (mp3Entries == null) {
      return;
    }

    if ((mp3EntriesFullName != null) && (mp3Entries.size() != mp3EntriesFullName.size())) {
      throw new IllegalArgumentException(String.format(Messages.getString("SyncerImpl.7"), //$NON-NLS-1$
          Integer.valueOf(mp3Entries.size()), Integer.valueOf(mp3EntriesFullName.size())));
    }

    if (mp3Entries.size() == 0) {
      return;
    }

    /* get the shell script listener */
    ShellScriptListener listener = shellScriptListener.get();

    for (int index = 0; index < mp3Entries.size(); index++) {
      String mp3Entry = mp3Entries.get(index);
      if ((flacEntries == null) || !flacEntries.contains(mp3Entry)) {
        String mp3EntryFullName = (mp3EntriesFullName == null) ? mp3Entry : mp3EntriesFullName.get(index);
        File mp3EntryFullNameFile = new File(mp3Dir, mp3EntryFullName);
        if (!mp3EntryFullNameFile.exists()) {
          continue;
        }
        if (listener != null) {
          listener.addMessage(String.format(Messages.getString("SyncerImpl.0"), mp3EntryFullNameFile.getPath())); //$NON-NLS-1$
          listener.addCommand(String.format("rm -fr \"%s\"", //$NON-NLS-1$
              StringUtils.escQuote(mp3EntryFullNameFile.getPath())));
        }
        if (!(simulate || FileUtils.delete(mp3EntryFullNameFile))) {
          /* can't be covered in a test */
          logger.log(Level.SEVERE, String.format(Messages.getString("SyncerImpl.1"), mp3EntryFullNameFile.getPath())); //$NON-NLS-1$
        }
      }
    }
  }

  /**
   * <p>
   * Copy covers from the flac directory to the mp3 directory.
   * </p>
   * <p>
   * It copies a cover when the cover doesn't exist in the mp3 directory or when
   * the cover in the flac directory is newer than the cover in the mp3
   * directory. Automatically creates the target directory when needed.
   * </p>
   * <p>
   * After copying the timestamp of the cover in the mp3 directory is set to the
   * timestamp of the cover in the flac directory.
   * </p>
   * 
   * @param flacDir the flac directory
   * @param covers the covers in the flac directory
   * @param mp3Dir the mp3 directory
   * @param simulate true to simulate copying
   */
  void copyCovers(File flacDir, List<String> covers, File mp3Dir, boolean simulate) {
    assert (flacDir != null);
    assert (mp3Dir != null);
    assert (!mp3Dir.exists() || mp3Dir.isDirectory());

    boolean reported = false;

    if ((covers == null) || covers.isEmpty() || !flacDir.isDirectory()) {
      return;
    }

    /* get the shell script listener */
    ShellScriptListener listener = shellScriptListener.get();

    for (String cover : covers) {
      File flacCoverFile = new File(flacDir, cover);
      File mp3CoverFile = new File(mp3Dir, cover);

      if (!mp3CoverFile.exists() || (flacCoverFile.lastModified() > mp3CoverFile.lastModified())) {
        if (listener != null) {
          if (!reported) {
            listener.addMessage(String.format(Messages.getString("SyncerImpl.2"), mp3Dir.getPath())); //$NON-NLS-1$
            reported = true;
          }
          if (!mp3Dir.exists()) {
            listener.addCommand(String.format("mkdir -p \"%s\"", StringUtils.escQuote(mp3Dir.getPath()))); //$NON-NLS-1$
          }
          listener.addCommand(String.format("cp    -t \"%s\" \\%n         \"%s\"", //$NON-NLS-1$
              StringUtils.escQuote(mp3Dir.getPath()), StringUtils.escQuote(flacCoverFile.getPath())));
          listener.addCommand(String.format("touch --reference \"%s\" \\%n                  \"%s\"", //$NON-NLS-1$
              StringUtils.escQuote(flacCoverFile.getPath()), StringUtils.escQuote(mp3CoverFile.getPath())));
        }

        if (!simulate) {
          try {
            FileUtils.copy(flacCoverFile, mp3CoverFile);
            if (!mp3CoverFile.setLastModified(flacCoverFile.lastModified())) {
              /* can't be covered by a test */
              logger.log(Level.INFO, String.format(Messages.getString("SyncerImpl.3"), //$NON-NLS-1$
                  mp3Dir.getPath()));
            }
          }
          catch (IOException e) {
            /* can't be covered by a test */
            logger.log(Level.SEVERE,
                String.format(Messages.getString("SyncerImpl.4"), flacCoverFile.getPath(), mp3CoverFile.getPath()), e); //$NON-NLS-1$
          }
        }
      }
    }
  }

  /**
   * <p>
   * Convert flac files in the flac directory to mp3 files in the mp3 directory
   * when needed.
   * </p>
   * <p>
   * A file needs conversion when the mp3 file doesn't exist, when the flac file
   * was modified more recently than the mp3 file, or when the mp3 directory
   * (mp3Entries) is null or doesn't contain the (converted) flac file (taken
   * from flacEntries).
   * </p>
   * <p>
   * Automatically creates the target directory when needed.
   * </p>
   * 
   * @param flac2Mp3Configuration the configuration for the flac-to-mp3
   *          conversion. May be null, in which case the default configuration
   *          is used.
   * @param flacDir the flac directory. When null or not an existing directory,
   *          then false is returned.
   * @param flacEntries the flac file names in the flac directory, without
   *          extensions. When null or empty, then true is returned.
   * @param flacEntriesFullName the flac file names in the flac directory, with
   *          extensions. Must be the same size and in the same order as
   *          flacEntries. If not, then false is returned.
   * @param mp3Dir the mp3 directory. When null, then false is returned.
   * @param mp3Entries the mp3 file names in the mp3 directory, without
   *          extensions. May be null, which means 'no entries'.
   * @param simulate true to simulate conversion
   * @return true when successful
   */
  boolean convertFlacFiles(Flac2Mp3Configuration flac2Mp3Configuration, File flacDir, List<String> flacEntries,
      List<String> flacEntriesFullName, File mp3Dir, List<String> mp3Entries, boolean simulate) {
    if ((flacDir == null) || !flacDir.isDirectory()) {
      return false;
    }

    if ((flacEntries == null) || flacEntries.isEmpty()) {
      return true;
    }

    if ((flacEntriesFullName == null) || (flacEntries.size() != flacEntriesFullName.size())) {
      return false;
    }

    if (mp3Dir == null) {
      return false;
    }

    for (int index = 0; index < flacEntries.size(); index++) {
      String flacEntry = flacEntries.get(index);

      File flacEntryFullNameFile = new File(flacDir, flacEntriesFullName.get(index));
      if (!flacEntryFullNameFile.exists() || flacEntryFullNameFile.isDirectory()) {
        continue;
      }

      File mp3EntryFullNameFile = new File(mp3Dir, flacEntry + MusicTreeConstants.MP3EXTENSION);

      boolean doConversion =
          !mp3EntryFullNameFile.exists()
              || (flacEntryFullNameFile.lastModified() > mp3EntryFullNameFile.lastModified()) || (mp3Entries == null)
              || !mp3Entries.contains(flacEntry);

      if (!doConversion) {
        continue;
      }

      try {
        if (!flacToMp3.convert(flac2Mp3Configuration, flacEntryFullNameFile, mp3EntryFullNameFile, simulate)) {
          return false;
        }
      }
      catch (FileNotFoundException e) {
        logger.log(Level.SEVERE, Messages.getString("SyncerImpl.5"), e); //$NON-NLS-1$
        return false;
      }
    }

    return true;
  }

  /**
   * Synchronise/Mirror a tree of flac files into a tree of mp3 files: remove
   * superfluous directories and files in the mp3 tree, copy cover images from
   * the flac tree into the mp3 tree, and convert flac files into mp3 files
   * 
   * @param flac2Mp3Configuration the configuration for the flac-to-mp3
   *          conversion. May be null in which case the default configuration is
   *          used.
   * @param musicTreeConfiguration the music tree configuration
   * @param simulate true to simulate synchronisation/mirroring
   * @param filter the filter to use (created from the extensions to accept as
   *          flac files in the flac tree and from the additional file names to
   *          accept in the flac tree
   * @return true on success
   */
  boolean syncFlac2Mp3(Flac2Mp3Configuration flac2Mp3Configuration, MusicTreeConfiguration musicTreeConfiguration,
      boolean simulate, FlacTreeFilenameFilter filter) {
    assert (musicTreeConfiguration != null);
    assert (filter != null);

    File flacDir = musicTreeConfiguration.getFlacBaseDir();
    File mp3Dir = musicTreeConfiguration.getMp3BaseDir();

    /* get the shell script listener */
    ShellScriptListener listener = shellScriptListener.get();

    if (listener != null) {
      listener.addMessage(String.format(Messages.getString("SyncerImpl.6"), flacDir.getPath())); //$NON-NLS-1$
    }

    /*
     * Get file lists for flacDir and mp3Dir; split out by directories, covers,
     * converted files, and other files.
     */

    /* get all (filtered) files in the flac tree directory */
    FileListSplit flacDirListSplit = new FileListSplit(flacDir, flacDir.list(filter), MusicTreeConstants.FLACEXTENSION);

    /* Exit early when there are no files in the flac directory */
    if (flacDirListSplit.noDirectoryFiles) {
      return true;
    }

    /* get all (unfiltered) files in the mp3 tree directory */
    FileListSplit mp3DirListSplit = new FileListSplit(mp3Dir, mp3Dir.list(), MusicTreeConstants.MP3EXTENSION);

    /*
     * Remove directories in the mp3 directory that are not present in the flac
     * directory
     */
    removeFromMp3Dir(mp3DirListSplit.directory, mp3DirListSplit.directories, null, flacDirListSplit.directories,
        simulate);

    /*
     * Remove covers in the mp3 directory that are not present in the flac
     * directory
     */
    removeFromMp3Dir(mp3DirListSplit.directory, mp3DirListSplit.covers, null, flacDirListSplit.covers, simulate);

    /*
     * Remove other files in the mp3 directory that are not present in the flac
     * directory
     */
    removeFromMp3Dir(mp3DirListSplit.directory, mp3DirListSplit.otherFiles, null, null, simulate);

    /*
     * Remove mp3s in the mp3 directory that are not present as flac files in
     * the flac directory
     */
    removeFromMp3Dir(mp3DirListSplit.directory, mp3DirListSplit.musicFilesWithoutExtensions,
        mp3DirListSplit.musicFiles, flacDirListSplit.musicFilesWithoutExtensions, simulate);

    /*
     * Recurse into directories
     */

    for (String directory : flacDirListSplit.directories) {
      MusicTreeConfiguration subMusicTreeConfiguration =
          new MusicTreeConfiguration(new File(flacDirListSplit.directory, directory), new File(mp3Dir, directory));
      assert (subMusicTreeConfiguration.validate(false) == null);
      if (!syncFlac2Mp3(flac2Mp3Configuration, subMusicTreeConfiguration, simulate, filter)) {
        return false;
      }
    }

    /* Early exit when we have no flac files */
    if (flacDirListSplit.musicFiles.isEmpty()) {
      return true;
    }

    /* Copy covers (if needed and only when we have flac files) */
    copyCovers(flacDirListSplit.directory, flacDirListSplit.covers, mp3DirListSplit.directory, simulate);

    /* convert flac files */
    return convertFlacFiles(flac2Mp3Configuration, flacDir, flacDirListSplit.musicFilesWithoutExtensions,
        flacDirListSplit.musicFiles, mp3Dir, mp3DirListSplit.musicFilesWithoutExtensions, simulate);
  }

  @Override
  public boolean syncFlac2Mp3(Flac2Mp3Configuration flac2Mp3Configuration,
      MusicTreeConfiguration musicTreeConfiguration, Set<String> extensionsList, Set<String> fileNamesList,
      boolean simulate) {
    if (musicTreeConfiguration.validate(false) != null) {
      return false;
    }

    return syncFlac2Mp3(flac2Mp3Configuration, musicTreeConfiguration, simulate, new FlacTreeFilenameFilter(
        extensionsList, fileNamesList, true));
  }
}
