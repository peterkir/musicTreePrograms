package nl.pelagic.audio.tag.checker.api;

import java.io.File;

import nl.pelagic.audio.tag.checker.types.GenericTag;

import org.jaudiotagger.tag.Tag;

import aQute.bnd.annotation.ConsumerType;

/**
 * Interface for users of the AudioTagChecker interface.
 * 
 * A service that implements the AudioTagChecker interface will report detailed
 * results through this interface to the user of its interface.
 */
@ConsumerType
public interface AudioTagCheckerCallback {
  /**
   * Called when a file has an unsupported extension.
   * 
   * @param file the file
   */
  void unsupportedExtension(File file);

  /**
   * Called when the file was not readable.
   * 
   * @param file the unreadable file
   * @param e the exception that occurred
   */
  void notReadable(File file, Exception e);

  /**
   * Called when the file had no tag.
   * 
   * @param file the file without a tag
   */
  void noTag(File file);

  /**
   * Called when the file had a tag that could not be converted into a generic
   * tag.
   * 
   * @param file the file the tag
   * @param tag the tag that could not be converted into a generic tag
   */
  void tagNotConverted(File file, Tag tag);

  /**
   * Called when the file had a tag didn't pass all checks.
   * 
   * @param tag the tag
   */
  void checksFailed(GenericTag tag);

  /**
   * Called when the file had a tag that passes all checks.
   * 
   * @param tag the tag
   */
  void checksPassed(GenericTag tag);
}
