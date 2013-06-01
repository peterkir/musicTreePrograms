package nl.pelagic.audio.tag.checker.api;

import java.io.IOException;

import nl.pelagic.audio.tag.checker.types.AudioTagCheckerConfiguration;
import aQute.bnd.annotation.ProviderType;

/**
 * Interface for a bundle that checks audio file tags.
 */
@ProviderType
public interface AudioTagChecker {
  /**
   * Check all audio file tags in a tree/directory/file . The callback is called
   * for every audio file found.
   * 
   * @param configuration the configuration for the check
   * @param callback the callback to use
   * @return true when successful, false when any of the parameters is null or
   *         when the scanPath in the configuration is null or doesn't exist.
   * @throws IOException when a file could not be resolved as a canonical file
   */
  boolean check(AudioTagCheckerConfiguration configuration, AudioTagCheckerCallback callback) throws IOException;
}
