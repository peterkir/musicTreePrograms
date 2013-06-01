package nl.pelagic.audio.tag.checker.api;

import java.util.Map;
import java.util.Set;

import nl.pelagic.audio.tag.checker.types.GenericTag;

import org.jaudiotagger.tag.Tag;

import aQute.bnd.annotation.ConsumerType;

/**
 * Interface to convert a non-generic tag into a generic tag
 */
@ConsumerType
public interface TagConverter {
  /**
   * @return A set of tag classes (from the jaudiotagger library) that the
   *         converter can convert into a generic tag. Not allowed to return
   *         null;
   */
  Set<Class<? extends Object>> getSupportedTagClasses();

  /**
   * @return A set of unknown tag field names that were encountered during
   *         lifetime of the converter.
   */
  Map<Class<? extends Object>, Set<String>> getUnknownTagFieldNames();

  /**
   * <p>
   * Convert a non-generic tag into a generic tag.
   * </p>
   * <p>
   * The generic tag will always have its backing file set. The source classes
   * and artwork flag are set when the backing file actually had a tag.
   * </p>
   * <p>
   * This method will only be called for tags that are instances of the classes
   * that are returned by the {@link #getSupportedTagClasses} method
   * </p>
   * 
   * @param genericTag The generic tag (non-null) to convert the non-generic tag
   *          into.
   * @param tag The non-generic tag (non-null).
   * @return true on a successful conversion
   */
  boolean convert(GenericTag genericTag, Tag tag);
}
