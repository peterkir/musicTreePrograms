package nl.pelagic.audio.musicTree.syncer.api;

import java.io.File;
import java.util.Set;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
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
   * @param flacDir the base directory of the flac tree
   * @param mp3Dir the base directory of the mp3 tree
   * @param extensionsList the extensions to accept as flac files in the flac
   *          tree (all must include the dot)
   * @param fileNamesList additional file names to accept in the flac tree
   *          (optional)
   * @param simulate true to simulate synchronisation/mirroring
   * @return true on success
   */
  boolean syncFlac2Mp3(Flac2Mp3Configuration flac2Mp3Configuration, File flacDir, File mp3Dir,
      Set<String> extensionsList, Set<String> fileNamesList, boolean simulate);
}
