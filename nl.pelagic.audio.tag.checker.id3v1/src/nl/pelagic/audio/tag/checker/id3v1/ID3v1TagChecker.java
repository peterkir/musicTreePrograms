package nl.pelagic.audio.tag.checker.id3v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.pelagic.audio.tag.checker.api.TagChecker;
import nl.pelagic.audio.tag.checker.id3v1.i18n.Messages;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;

import org.jaudiotagger.tag.id3.ID3v11Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;

import aQute.bnd.annotation.component.Component;

/**
 * This bundle performs 'ID3v1 truncated field' checks on a generic tag.
 * 
 * It checks the ALBUMTITLE, TRACKARTIST, TRACKTITLE and ALBUMARTIST fields to
 * see if they might be truncated (are exactly 30 characters long) because
 * they're part of an ID3v1 tag.
 */
@Component
public class ID3v1TagChecker implements TagChecker {
  /** the maximum ID3v1 field length */
  public static final int ID3V1_MAX_FIELD_LENGTH = 30;

  /** The position markers, always the same */
  private static final List<Integer> positionMarkers = new ArrayList<>();

  static {
    positionMarkers.add(Integer.valueOf(ID3V1_MAX_FIELD_LENGTH - 1));
  }

  @Override
  public void check(GenericTag genericTag) {
    if (genericTag == null) {
      return;
    }

    /* get the classes of the source tags */
    Set<Class<? extends Object>> sourceTagClasses = genericTag.getSourceTagClasses();
    assert (sourceTagClasses != null);

    /* exit when there are no source classes */
    if (sourceTagClasses.isEmpty()) {
      return;
    }

    /* only check mp3 files */
    if (!(sourceTagClasses.contains(ID3v24Tag.class) || sourceTagClasses.contains(ID3v23Tag.class)
        || sourceTagClasses.contains(ID3v22Tag.class) || sourceTagClasses.contains(ID3v11Tag.class) || sourceTagClasses
          .contains(ID3v1Tag.class))) {
      return;
    }

    /* get all fields */
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = genericTag.getFields();
    assert (fields != null);

    for (Entry<GenericTagFieldName, Map<String, Set<String>>> entry : fields.entrySet()) {
      GenericTagFieldName fieldName = entry.getKey();

      /* only evaluate the mentioned fields */
      if (!(GenericTagFieldName.ALBUMTITLE.equals(fieldName) || GenericTagFieldName.TRACKARTIST.equals(fieldName)
          || GenericTagFieldName.TRACKTITLE.equals(fieldName) || GenericTagFieldName.ALBUMARTIST.equals(fieldName))) {
        continue;
      }

      /* get all values for the current tag field name */
      Map<String, Set<String>> valueNameMap = entry.getValue();

      if ((valueNameMap == null) || valueNameMap.isEmpty()) {
        continue;
      }

      for (String value : valueNameMap.keySet()) {
        if (value.length() == ID3V1_MAX_FIELD_LENGTH) {
          genericTag.addReport(fieldName, Messages.getString("ID3v1TagChecker.0"), value, positionMarkers, null); //$NON-NLS-1$
        }
      }
    }
  }
}
