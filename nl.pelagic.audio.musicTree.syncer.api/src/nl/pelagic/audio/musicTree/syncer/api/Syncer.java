package nl.pelagic.audio.musicTree.syncer.api;

import java.io.File;
import java.util.Set;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConfiguration;
import aQute.bnd.annotation.ProviderType;

/**
 * API for the music tree synchroniser, which mirrors a tree of flac files into
 * a tree of mp3 files
 */
@ProviderType
public interface Syncer {
  /**
   * Synchronise/Mirror a tree of flac files into a tree of mp3 files: remove
   * superfluous directories and files in the mp3 tree, copy cover images from
   * the flac tree into the mp3 tree, and convert flac files into mp3 files
   * 
   * @param flac2Mp3Configuration the configuration for the flac-to-mp3
   *          conversion
   * @param musicTreeConfiguration the music tree configuration. If it's invalid
   *          then false is returned immediately.
   * @param directoryToSync the directory to sync. If null then it is assumed to
   *          be the same as the flac base directory of the
   *          musicTreeConfiguration. If it is not a directory or not below the
   *          flac base directory of the musicTreeConfiguration then false is
   *          returned.
   * @param extensionsList the extensions to accept as flac files in the flac
   *          tree (all must include the dot)
   * @param fileNamesList additional file names to accept in the flac tree
   *          (optional)
   * @param simulate true to simulate synchronisation/mirroring
   * @return true on success
   */
  boolean syncFlac2Mp3(Flac2Mp3Configuration flac2Mp3Configuration, MusicTreeConfiguration musicTreeConfiguration,
      File directoryToSync, Set<String> extensionsList, Set<String> fileNamesList, boolean simulate);
}
