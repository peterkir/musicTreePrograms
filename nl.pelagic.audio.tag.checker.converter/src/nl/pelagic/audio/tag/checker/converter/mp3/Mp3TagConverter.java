package nl.pelagic.audio.tag.checker.converter.mp3;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;

import nl.pelagic.audio.tag.checker.api.TagConverter;
import nl.pelagic.audio.tag.checker.common.RegularExpressions;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.NameValuePair;

import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.reference.GenreTypes;

import aQute.bnd.annotation.component.Component;

/**
 * This bundle converts a (non-generic) MP3 Tag into a (generic) GenericTag.
 * 
 * It will only process ID3v2x tags since the jaudiotagger library doesn't
 * support the needed operations on ID3v1x tags.
 */
@Component
public class Mp3TagConverter implements TagConverter {
  /** The logger */
  private final Logger logger = Logger.getLogger(this.getClass().getName());

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
   * Constructor
   */
  public Mp3TagConverter() {
    supportedTagClasses.add(ID3v22Tag.class);
    supportedTagClasses.add(ID3v23Tag.class);
    supportedTagClasses.add(ID3v24Tag.class);

    unknownTagFieldNames.put(ID3v22Tag.class, new TreeSet<String>());
    unknownTagFieldNames.put(ID3v23Tag.class, new TreeSet<String>());
    unknownTagFieldNames.put(ID3v24Tag.class, new TreeSet<String>());
  }

  @Override
  public Set<Class<? extends Object>> getSupportedTagClasses() {
    return supportedTagClasses;
  }

  @Override
  public Map<Class<? extends Object>, Set<String>> getUnknownTagFieldNames() {
    return unknownTagFieldNames;
  }

  /**
   * Concatenate the values from an MP3 name/value tag text. Example: from
   * name/value
   * 
   * <pre>
   * USLT/Language="English";Lyrics="Give&nbsp;me"
   * </pre>
   * 
   * it will return
   * 
   * <pre>
   * &quot;English - Give me&quot;
   * </pre>
   * 
   * @param name the name
   * @param value the value
   * @return the concatenated values
   */
  String extractValues(String name, String value) {
    if ((value == null) || value.isEmpty()) {
      return value;
    }

    StringBuilder sb = new StringBuilder();
    Matcher matcher = RegularExpressions.patternMp3TextValue.matcher(value);
    if (matcher.find()) {
      do {
        if (sb.length() > 0) {
          sb.append(" - "); //$NON-NLS-1$
        }
        sb.append(matcher.group(2));
      }
      while (matcher.find());

      return sb.toString();
    }

    logger.log(Level.WARNING, "Unknown MP3 tag value qualifier encountered: name/value = " + name + "/" + value); //$NON-NLS-1$//$NON-NLS-2$
    return value;
  }

  @Override
  public boolean convert(GenericTag genericTag, Tag tag) {
    assert ((tag instanceof ID3v22Tag) || (tag instanceof ID3v23Tag) || (tag instanceof ID3v24Tag));

    NameValuePair previousTagNameValuePair = new NameValuePair();

    /* iterate over all fields */
    Iterator<TagField> tagFieldIterator = null;
    Set<String> unknowns = null;
    if (tag instanceof ID3v24Tag) {
      tagFieldIterator = ((ID3v24Tag) tag).getFields();
      unknowns = unknownTagFieldNames.get(ID3v24Tag.class);
    } else if (tag instanceof ID3v23Tag) {
      tagFieldIterator = ((ID3v23Tag) tag).getFields();
      unknowns = unknownTagFieldNames.get(ID3v23Tag.class);
    } else /* if (tag instanceof ID3v22Tag) */{
      tagFieldIterator = ((ID3v22Tag) tag).getFields();
      unknowns = unknownTagFieldNames.get(ID3v22Tag.class);
    }
    assert (tagFieldIterator != null);
    assert (unknowns != null);

    while (tagFieldIterator.hasNext()) {
      TagField tagField = tagFieldIterator.next();

      /* get name and value */
      String name = tagField.getId().trim().toUpperCase();
      String value = tagField.toString();
      value = extractValues(name, value);

      /* determine which (generic) field to set */
      GenericTagFieldName genericTagFieldName = GenericTagFieldName.OTHER;
      switch (name) {

      /* primary fields */

        case "TPOS": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.ALBUMDISCNUMBER;
          break;

        case "TCON": //$NON-NLS-1$
          if (RegularExpressions.patternSimpleNumber.matcher(value).matches()) {
            value = GenreTypes.getInstanceOf().getValueForId(Integer.parseInt(value));
          }
          genericTagFieldName = GenericTagFieldName.ALBUMGENRE;
          break;

        case "TOPE" /* ID3v23 */: //$NON-NLS-1$
        case "TPE2" /* ID3v24 */: //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.ALBUMARTIST;
          break;

        case "TALB": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.ALBUMTITLE;
          break;

        case "TRCK": //$NON-NLS-1$
          if (RegularExpressions.patternMp3TrackNumber.matcher(value).matches()) {
            String[] s = value.split("/"); //$NON-NLS-1$

            previousTagNameValuePair =
                genericTag.addField(previousTagNameValuePair, GenericTagFieldName.TRACKNUMBER, name, s[0]);
            if (s.length > 1) {
              previousTagNameValuePair =
                  genericTag.addField(previousTagNameValuePair, GenericTagFieldName.ALBUMTOTALTRACKS, name, s[1]);
            }
            previousTagNameValuePair.setName(name);
            previousTagNameValuePair.setValue(value);
            continue;
          }
          break;

        case "TYER" /* ID3v23 */: //$NON-NLS-1$
        case "TDRC" /* ID3v24 */: //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.ALBUMYEAR;
          break;

        case "TPE1": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.TRACKARTIST;
          break;

        case "TIT2": //$NON-NLS-1$
          genericTagFieldName = GenericTagFieldName.TRACKTITLE;
          break;

        /* other fields */

        default:
          unknowns.add(name);
          break;
      }

      previousTagNameValuePair = genericTag.addField(previousTagNameValuePair, genericTagFieldName, name, value);
    }

    return true;
  }
}