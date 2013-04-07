package nl.pelagic.audio.conversion.flac2mp3.api;

import java.io.File;
import java.io.FileNotFoundException;

import aQute.bnd.annotation.ProviderType;

/**
 * Interface for flac to mp3 conversion.
 */
@ProviderType
public interface FlacToMp3 {
  /**
   * <p>
   * Convert a flac file into an mp3 file.
   * </p>
   * <p>
   * Creates all directories up to the mp3 file.
   * </p>
   * 
   * @param configuration the configuration for the conversion process. If null
   *          then the default configuration is used.
   * @param flac the flac (source) file
   * @param mp3 the mp3 (destination) file
   * @param simulate true to simulate conversion
   * @return true when conversion was successful, false otherwise
   * @throws FileNotFoundException when:
   *           <ul>
   *           <li>the flac file is null, doesn't exist or is not a file</li>
   *           <li>the mp3 file is null or exists but is not a file</li>
   *           </ul>
   */
  boolean convert(Flac2Mp3Configuration configuration, File flac, File mp3, boolean simulate)
      throws FileNotFoundException;
}
