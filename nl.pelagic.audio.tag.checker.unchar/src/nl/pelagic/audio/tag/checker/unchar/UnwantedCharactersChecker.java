package nl.pelagic.audio.tag.checker.unchar;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nl.pelagic.audio.tag.checker.api.TagChecker;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.unchar.i18n.Messages;
import aQute.bnd.annotation.component.Component;

/**
 * This bundle performs 'unwanted character' checks on a generic tag.
 * 
 * Only regular ASCII characters are allowed; character codes [32, 126], hex
 * [20, 7e]
 * 
 * Note: only primary and pseudo-primary fields are considered when checking
 * fields.
 */
@Component
public class UnwantedCharactersChecker implements TagChecker {
  /**
   * Only regular ASCII characters are allowed; character codes [32, 126], hex
   * [20, 7e]
   */
  private static final byte[] allowedCharacters = {
      0x20, '-', 0x7e
  };

  /** The regular expression for unwanted characters, for incremental matching */
  private static final String REGEX_UNCHAR;

  /** The compiled regex expression REGEX_UNCHAR */
  public static final Pattern patternUnchar;

  /**
   * Compile the regular expression
   */
  static {
    try {
      REGEX_UNCHAR = ".*?([^" + new String(allowedCharacters, "UTF-8") + "]+)"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
      patternUnchar = Pattern.compile(REGEX_UNCHAR);
    }
    catch (Throwable e) {
      /* can't be covered by a test */
      throw new ExceptionInInitializerError(e);
    }
  }

  @Override
  public void check(GenericTag genericTag) {
    if (genericTag == null) {
      return;
    }

    /* get all fields */
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = genericTag.getFields();
    assert (fields != null);

    for (Entry<GenericTagFieldName, Map<String, Set<String>>> entry : fields.entrySet()) {
      GenericTagFieldName fieldName = entry.getKey();

      /* skip non-primary fields */
      if (!fieldName.isPrimary(true)) {
        continue;
      }

      /* only primary and pseudo-primary fields from here */

      Map<String, Set<String>> valueNameMap = entry.getValue();

      if ((valueNameMap == null) || valueNameMap.isEmpty()) {
        continue;
      }

      for (String value : valueNameMap.keySet()) {
        /* no unwanted characters */

        Matcher matcher = patternUnchar.matcher(value);
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

          genericTag.addReport(fieldName,
              Messages.getString("UnwantedCharactersChecker.0"), value, positionMarkers, null); //$NON-NLS-1$
        }
      }
    }
  }
}
