package nl.pelagic.jaudiotagger.util;

import java.util.ArrayList;
import java.util.List;

import org.jaudiotagger.tag.TagField;

/**
 * Utilities for tags
 */
public class TagUtils {
  /**
   * <p>
   * Concatenate a number of field values into a string.
   * </p>
   * 
   * @param fieldValues a list with the field values
   * @param defaultValue the default value for the field
   * @return the value of the field, or the default value when fieldValues is
   *         null or empty
   * @see #concatenateTagValues(List, String)
   */
  public static String concatenateTagFields(List<TagField> fieldValues, String defaultValue) {
    if ((fieldValues == null) || (fieldValues.size() == 0)) {
      return defaultValue;
    }

    List<String> values = new ArrayList<>(fieldValues.size());
    for (TagField tagField : fieldValues) {
      values.add(tagField.toString());
    }

    return concatenateTagValues(values, defaultValue);
  }

  /**
   * <p>
   * Concatenate a number of field values into a string.
   * </p>
   * <p>
   * A tag can contain multiple values for every field name, so we just
   * concatenate these values and put a "&nbsp;-&nbsp;" in between. Only
   * non-zero length values are concatenated.
   * </p>
   * <p>
   * This functionality basically exists because there is a bug in the EasyTag
   * application that splits fields values like 'aa&nbsp;-&nbsp;bb' into 2 tag
   * values in the file. This functionality allows the application to merge
   * those back together.
   * </p>
   * 
   * @param fieldValues a list with the field values
   * @param defaultValue the default value for the field
   * @return the value of the field, or the default value when fieldValues is
   *         null or empty
   */
  public static String concatenateTagValues(List<String> fieldValues, String defaultValue) {
    if ((fieldValues == null) || (fieldValues.size() == 0)) {
      return defaultValue;
    }

    StringBuilder result = new StringBuilder();
    for (String fieldValue : fieldValues) {
      if ((result.length() > 0) && (fieldValue.length() > 0)) {
        result.append(" - "); //$NON-NLS-1$
      }
      result.append(fieldValue);
    }

    return result.toString();
  }
}
