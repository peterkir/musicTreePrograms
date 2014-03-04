package nl.pelagic.audio.tag.checker.basic;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;

import nl.pelagic.audio.tag.checker.api.TagChecker;
import nl.pelagic.audio.tag.checker.basic.i18n.Messages;
import nl.pelagic.audio.tag.checker.common.RegularExpressions;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import aQute.bnd.annotation.component.Component;

/**
 * This bundle performs basic checks on a generic tag, it ensures that:
 * <ul>
 * <li>A tag is present</li>
 * <li>The tag has no artwork</li>
 * <li>All primary fields (see {@link GenericTagFieldName}) are present</li>
 * </ul>
 * 
 * Then only primary and pseudo-primary fields are considered when ensuring
 * that:
 * <ul>
 * <li>Fields are not empty</li>
 * <li>Fields have no leading and/or trailing whitespace</li>
 * <li>Fields don't have 2 or more consecutive whitespace characters</li>
 * <li>Disc number fields are formatted as number/number</li>
 * <li>The current disc number not greater than the total number of discs</li>
 * <li>Disc number fields are formatted as number/number</li>
 * <li>Track number is a number</li>
 * <li>Total number of track on the album is a number</li>
 * <li>Album year is a 4-digit year between 1500 and this year</li>
 * </ul>
 */
@Component
public class BasicTagChecker implements TagChecker {

  /** The primary generic tag field names */
  private GenericTagFieldName[] primaries = GenericTagFieldName.getPrimaries(false);

  @Override
  public void check(GenericTag genericTag) {
    if (genericTag == null) {
      return;
    }

    /* no artwork */
    if (genericTag.hasArtwork()) {
      genericTag.addReport(GenericTagFieldName.ARTWORK, Messages.getString("BasicTagChecker.0"), null, null, null); //$NON-NLS-1$
    }

    /* get all fields */
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = genericTag.getFields();
    assert (fields != null);

    /* check for presence of primary fields */
    List<GenericTagFieldName> missingPrimaries = new LinkedList<>();
    for (GenericTagFieldName primary : primaries) {
      if (!fields.containsKey(primary)) {
        missingPrimaries.add(primary);
      }
    }
    if (missingPrimaries.size() > 0) {
      Collections.sort(missingPrimaries);
      genericTag.addReport(GenericTagFieldName.FILE,
          Messages.getString("BasicTagChecker.1") + " " + missingPrimaries, null, //$NON-NLS-1$//$NON-NLS-2$
          null, null);
    }
    missingPrimaries.clear();

    for (Entry<GenericTagFieldName, Map<String, Set<String>>> entry : fields.entrySet()) {
      GenericTagFieldName fieldName = entry.getKey();

      /* skip non-primary fields */
      if (!fieldName.isPrimary(true)) {
        continue;
      }

      /* only primary and pseudo-primary fields from here */

      Map<String, Set<String>> valueNameMap = entry.getValue();

      /* no empty fields */
      if ((valueNameMap == null) || valueNameMap.isEmpty()) {
        genericTag.addReport(fieldName, Messages.getString("BasicTagChecker.2"), null, null, null); //$NON-NLS-1$
        continue;
      }

      for (String value : valueNameMap.keySet()) {
        /* no empty fields */
        if (value.isEmpty()) {
          genericTag.addReport(fieldName, Messages.getString("BasicTagChecker.3"), null, null, null); //$NON-NLS-1$
        }

        /*
         * make sure all whitespace characters are simple spaces, needed for the
         * position markers
         */
        String simpleValue = value.replaceAll("\\s", " "); //$NON-NLS-1$ //$NON-NLS-2$

        /*
         * trim the value, for efficient compares and avoidance of duplicate
         * reports
         */
        String trimmedValue = simpleValue.trim();

        /* no leading/trailing whitespace */
        if (!simpleValue.equals(trimmedValue)) {
          List<Integer> positionMarkers = new ArrayList<>();

          Matcher matcher = RegularExpressions.patternLeadingWhitespace.matcher(simpleValue);
          if (matcher.matches()) {
            int i = matcher.start(1);
            while (i < matcher.end(1)) {
              positionMarkers.add(Integer.valueOf(i));
              i++;
            }
          }

          matcher = RegularExpressions.patternTrailingWhitespace.matcher(simpleValue);
          if (matcher.matches()) {
            int i = matcher.start(1);
            while (i < matcher.end(1)) {
              positionMarkers.add(Integer.valueOf(i));
              i++;
            }
          }

          genericTag.addReport(fieldName,
              Messages.getString("BasicTagChecker.4"), simpleValue, positionMarkers, trimmedValue); //$NON-NLS-1$
        }

        /*
         * No multiple consecutive whitespace characters. Use trimmed value to
         * prevent duplicate reports when the value has (multiple) leading
         * and/or trailing whitespace
         */
        {
          Matcher matcher = RegularExpressions.patternMultipleWhitespace.matcher(trimmedValue);
          if (matcher.find()) {
            List<Integer> positionMarkers = new ArrayList<>();
            do {
              int i = matcher.start(1);
              int end = matcher.end(1);
              while (i < end) {
                positionMarkers.add(Integer.valueOf(i));
                i++;
              }
            }
            while (matcher.find());

            genericTag
                .addReport(
                    fieldName,
                    Messages.getString("BasicTagChecker.5"), trimmedValue, positionMarkers, trimmedValue.replaceAll("\\s+", " ")); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
          }
        }

        /* correct x/y disc number */
        if (fieldName.equals(GenericTagFieldName.ALBUMDISCNUMBER)) {
          if (RegularExpressions.patternDiscNumber.matcher(trimmedValue).matches()) {
            String[] split = trimmedValue.split("/"); //$NON-NLS-1$
            int currentDisc = Integer.parseInt(split[0]);
            int totalDiscs = Integer.parseInt(split[1]);
            if (currentDisc > totalDiscs) {
              List<Integer> positionMarkers = new ArrayList<>();
              int pos = trimmedValue.indexOf('/');
              for (int i = 0; i < pos; i++) {
                positionMarkers.add(Integer.valueOf(i));
              }
              genericTag.addReport(fieldName, Messages.getString("BasicTagChecker.14"), trimmedValue, positionMarkers, //$NON-NLS-1$
                  String.format(Messages.getString("BasicTagChecker.15"), Integer.valueOf(totalDiscs), //$NON-NLS-1$
                      Integer.valueOf(totalDiscs)));
            }
          } else {
            genericTag.addReport(fieldName, Messages.getString("BasicTagChecker.6"), trimmedValue, null, //$NON-NLS-1$
                Messages.getString("BasicTagChecker.7")); //$NON-NLS-1$
          }
        }

        /* correct track number and correct total tracks number */
        if ((fieldName.equals(GenericTagFieldName.TRACKNUMBER) || fieldName
            .equals(GenericTagFieldName.ALBUMTOTALTRACKS))
            && !RegularExpressions.patternSimpleNumber.matcher(trimmedValue).matches()) {
          genericTag.addReport(fieldName,
              Messages.getString("BasicTagChecker.8"), trimmedValue, null, Messages.getString("BasicTagChecker.9")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        if (fieldName.equals(GenericTagFieldName.ALBUMYEAR)) {
          /* correct 4 digit year */
          if (!RegularExpressions.patternYear.matcher(trimmedValue).matches()) {
            genericTag.addReport(fieldName,
                Messages.getString("BasicTagChecker.10"), trimmedValue, null, Messages.getString("BasicTagChecker.11")); //$NON-NLS-1$ //$NON-NLS-2$
          } else {
            /* correct year in range [1500, now] */
            int yearNow = Calendar.getInstance().get(Calendar.YEAR);
            int year = Integer.parseInt(trimmedValue);
            int yearLow = 1500;
            if ((year < yearLow) || (year > yearNow)) {
              genericTag.addReport(fieldName, Messages.getString("BasicTagChecker.12"), trimmedValue, null, //$NON-NLS-1$
                  Messages.getString("BasicTagChecker.13") + " [" + yearLow + ", " //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
                      + yearNow + "]"); //$NON-NLS-1$
            }
          }
        }
      }
    }
  }
}
