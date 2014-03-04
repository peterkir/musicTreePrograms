package nl.pelagic.audio.tag.checker.converter.flac;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nl.pelagic.audio.tag.checker.api.TagConverter;
import nl.pelagic.audio.tag.checker.common.RegularExpressions;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.NameValuePair;

import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.reference.GenreTypes;

import aQute.bnd.annotation.component.Component;

/** This bundle converts a (non-generic) FlacTag into a (generic) GenericTag */
@Component
public class FlacTagConverter implements TagConverter {
  /**
   * A set of tag classes (from the jaudiotagger library) that the converter can
   * convert into a generic tag
   */
  private Set<Class<? extends Object>> supportedTagClasses = new HashSet<>();

  /**
   * A set of unknown tag field names that were encountered during lifetime of
   * the converter
   */
  private final Map<Class<? extends Object>, Set<String>> unknownTagFieldNames = new HashMap<>();

  /**
   * Fast access set into unknownTagFieldNames (we only convert a single tag
   * class)
   */
  private TreeSet<String> fastUnknownTagFieldNames = new TreeSet<>();

  /**
   * Constructor
   */
  public FlacTagConverter() {
    supportedTagClasses.add(FlacTag.class);
    unknownTagFieldNames.put(FlacTag.class, fastUnknownTagFieldNames);
  }

  @Override
  public Set<Class<? extends Object>> getSupportedTagClasses() {
    return supportedTagClasses;
  }

  @Override
  public Map<Class<? extends Object>, Set<String>> getUnknownTagFieldNames() {
    return unknownTagFieldNames;
  }

  @Override
  public boolean convert(GenericTag genericTag, Tag tag) {
    assert (tag instanceof FlacTag);

    Locale locale = Locale.getDefault();
    NameValuePair previousTagNameValuePair = new NameValuePair();

    /* iterate over all fields */
    Iterator<TagField> tagFieldIterator = ((FlacTag) tag).getFields();
    while (tagFieldIterator.hasNext()) {
      TagField tagField = tagFieldIterator.next();

      /* get name and value */
      String name = tagField.getId().trim().toUpperCase(locale);
      String value = tagField.toString();

      /* determine which (generic) field to set */
      GenericTagFieldName genericTagFieldName = GenericTagFieldName.OTHER;
      switch (name) {

      /* primary fields */

        case "DISCNUMBER": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.ALBUMDISCNUMBER;
          break;

        case "GENRE": //$NON-NLS-1$
          if (RegularExpressions.patternSimpleNumber.matcher(value).matches()) {
            value = GenreTypes.getInstanceOf().getValueForId(Integer.parseInt(value));
          }
          genericTagFieldName = GenericTagFieldName.ALBUMGENRE;
          break;

        case "ALBUMARTIST": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.ALBUMARTIST;
          break;

        case "ALBUM": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.ALBUMTITLE;
          break;

        case "TRACKTOTAL": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.ALBUMTOTALTRACKS;
          break;

        case "DATE": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.ALBUMYEAR;
          break;

        case "ARTIST": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.TRACKARTIST;
          break;

        case "TRACKNUMBER": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.TRACKNUMBER;
          break;

        case "TITLE": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.TRACKTITLE;
          break;

        /* other fields */

        default:
          fastUnknownTagFieldNames.add(name);
          break;
      }

      previousTagNameValuePair = genericTag.addField(previousTagNameValuePair, genericTagFieldName, name, value);
    }

    return true;
  }
}