package nl.pelagic.audio.tag.checker.filename;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.pelagic.audio.tag.checker.api.TagChecker;
import nl.pelagic.audio.tag.checker.common.RegularExpressions;
import nl.pelagic.audio.tag.checker.filename.i18n.Messages;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.jaudiotagger.util.TagUtils;
import nl.pelagic.util.file.ExtensionUtils;
import aQute.bnd.annotation.component.Component;

/**
 * This bundle performs a filename check on the backing file of a generic tag:
 * <ul>
 * <li>
 * In case there is only 1 'disc' in the 'album', then the filename must be
 * formatted as:<br/>
 * <code>&nbsp;&nbsp;trackNumber - trackTitle.extension</code><br/>
 * <code>&nbsp;&nbsp;</code>(trackNumber has 2 digits, with leading zero)<br/>
 * For example: <code>05 - Forty-Six & 2.flac</code><br/>
 * </li>
 * <li>
 * In case there are multiple 'discs' in the 'album', then the filename must be
 * formatted as:<br/>
 * <code>&nbsp;&nbsp;discNumberTrackNumber - trackTitle.extension</code><br/>
 * <code>&nbsp;&nbsp;</code>(discNumber has 2 or 3 digits, with leading zero)<br/>
 * <code>&nbsp;&nbsp;</code>(trackNumber has 2 digits, with leading zero)<br/>
 * For example: <code>0103 - One Look.flac</code></li>
 * <li>
 * The directory in which the file is located must be equal to the album title.</li>
 * <li>
 * In case the albumArtist tag field is set, then the parent directory of the
 * directory in which the file is located must be equal to the album artist,
 * otherwise it must be equal to the track artist</li>
 * </ul>
 * 
 * Summary:<br/>
 * A music file is expected on the path<br/>
 * <code>&nbsp;&nbsp;.../artistName/albumTitle/numbers - trackTitle.extension</code>
 * <br/>
 * or<br/>
 * <code>&nbsp;&nbsp;.../albumArtist/albumTitle/numbers - trackTitle.extension</code>
 */
@Component
public class FileNameTagChecker implements TagChecker {
  /**
   * Replace some special characters ('/' --> '-') that are not used in paths
   * 
   * @param path the path
   * @return the path with special characters replaced
   */
  static String pathEscape(String path) {
    String result = path.replaceAll("/", "-"); //$NON-NLS-1$ //$NON-NLS-2$
    return result;
  }

  /**
   * Concatenate all values of the value/name map
   * 
   * @param valueNameMap the value/name map
   * @return the concatenated values
   * @see TagUtils#concatenateTagValues(List, String)
   */
  static String concatenateValues(Map<String, Set<String>> valueNameMap) {
    if ((valueNameMap == null) || valueNameMap.isEmpty()) {
      return null;
    }

    ArrayList<String> l = new ArrayList<>(valueNameMap.keySet());
    return TagUtils.concatenateTagValues(l, null);
  }

  /**
   * Check artist directory name
   * 
   * @param genericTag the generic tag
   */
  static void checkArtistDirectory(GenericTag genericTag) {
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = genericTag.getFields();

    String albumArtist = concatenateValues(fields.get(GenericTagFieldName.ALBUMARTIST)); /* optional */
    String trackArtist = concatenateValues(fields.get(GenericTagFieldName.TRACKARTIST));
    if ((albumArtist == null) && (trackArtist == null)) {
      genericTag.addReport(GenericTagFieldName.FILE, Messages.getString("FileNameTagChecker.0"), null, null, null); //$NON-NLS-1$
    } else {
      /* albumArtist has priority over trackArtist */
      String expectedArtistDir = (albumArtist != null) ? pathEscape(albumArtist) : pathEscape(trackArtist);
      String artistDir = genericTag.getBackingFile().getParentFile().getParentFile().getName();

      if (!artistDir.equals(expectedArtistDir)) {
        genericTag.addReport(GenericTagFieldName.FILE,
            Messages.getString("FileNameTagChecker.1"), artistDir, null, expectedArtistDir); //$NON-NLS-1$
      }
    }
  }

  /**
   * Check album directory name
   * 
   * @param genericTag the generic tag
   */
  static void checkAlbumDirectory(GenericTag genericTag) {
    String albumTitle = concatenateValues(genericTag.getFields().get(GenericTagFieldName.ALBUMTITLE));
    if (albumTitle == null) {
      genericTag.addReport(GenericTagFieldName.FILE, Messages.getString("FileNameTagChecker.2"), //$NON-NLS-1$
          null, null, null);
    } else {
      String expectedAlbumDir = pathEscape(albumTitle);
      String albumDir = genericTag.getBackingFile().getParentFile().getName();

      if (!albumDir.equals(expectedAlbumDir)) {
        genericTag.addReport(GenericTagFieldName.FILE,
            Messages.getString("FileNameTagChecker.3"), albumDir, null, expectedAlbumDir); //$NON-NLS-1$
      }
    }
  }

  /**
   * Determine the number of digits to use for the album number in a filename.
   * 
   * <pre>
   * albumTotalDiscnumber - albumTotalDiscnumber = #albumDigits
   *       (low)                  (high)
   *       -inf           -           0               0
   *          1           -           1               0
   *          2           -           9               2
   *         10           -          99               2
   *        100           -         999               3
   *       1000           -        9999               4
   *      10000           -       99999               5
   *     ......           -      ......               .
   * </pre>
   * 
   * @param albumTotalDiscnumber the total number of discs of the album
   * @return the number of digits to use for the disc number in the filename
   */
  static int determineNumberOfAlbumDigits(int albumTotalDiscnumber) {
    if (albumTotalDiscnumber <= 1) {
      /* this assumes 1 album when albumTotalDiscnumber < 0 */
      return 0;
    }

    if (albumTotalDiscnumber < 100) {
      return 2;
    }

    if (albumTotalDiscnumber < 1000) {
      return 3;
    }

    if (albumTotalDiscnumber < 10000) {
      return 4;
    }

    return ((int) Math.log10(albumTotalDiscnumber) + 1);
  }

  /**
   * Check track file name: includes track number, track title, disc number
   * (optional), total disc number (optional)
   * 
   * @param genericTag the generic tag
   */
  static void checkFilename(GenericTag genericTag) {
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = genericTag.getFields();

    String fieldValue = concatenateValues(fields.get(GenericTagFieldName.ALBUMDISCNUMBER));
    String[] spl = {};
    if (fieldValue != null) {
      spl = fieldValue.split("/", 2); //$NON-NLS-1$
    }

    int albumDiscnumber = -1;
    int albumTotalDiscnumber = -1;

    if ((spl.length > 0) && RegularExpressions.patternSimpleNumber.matcher(spl[0]).matches()) {
      albumDiscnumber = Integer.parseInt(spl[0]);
    }
    if ((spl.length > 1) && RegularExpressions.patternSimpleNumber.matcher(spl[1]).matches()) {
      albumTotalDiscnumber = Integer.parseInt(spl[1]);
    }

    fieldValue = concatenateValues(fields.get(GenericTagFieldName.TRACKNUMBER));
    int trackNumber = -1;
    if ((fieldValue != null) && RegularExpressions.patternSimpleNumber.matcher(fieldValue).matches()) {
      trackNumber = Integer.parseInt(fieldValue);
    }

    String trackTitle = concatenateValues(fields.get(GenericTagFieldName.TRACKTITLE));

    if ((albumTotalDiscnumber == -1) || (albumDiscnumber == -1) || (trackTitle == null) || (trackNumber == -1)) {
      genericTag.addReport(GenericTagFieldName.FILE, Messages.getString("FileNameTagChecker.4"), null, //$NON-NLS-1$
          null, null);
      return;
    }

    String backingFileName = genericTag.getBackingFile().getName();
    String[] splitBackingFileName = ExtensionUtils.split(backingFileName, true);

    int albumDigitsCount = determineNumberOfAlbumDigits(albumTotalDiscnumber);
    String expectedBackingFileNameNoExtension;
    if (albumDigitsCount == 0) {
      expectedBackingFileNameNoExtension = String.format("%02d - %s", Integer.valueOf(trackNumber), trackTitle); //$NON-NLS-1$
    } else {
      expectedBackingFileNameNoExtension =
          String
              .format(
                  "%0" + albumDigitsCount + "d%02d - %s", Integer.valueOf(albumDiscnumber), Integer.valueOf(trackNumber), trackTitle); //$NON-NLS-1$ //$NON-NLS-2$
    }
    expectedBackingFileNameNoExtension = pathEscape(expectedBackingFileNameNoExtension);

    if (!splitBackingFileName[0].equals(expectedBackingFileNameNoExtension)) {
      genericTag.addReport(GenericTagFieldName.FILE, Messages.getString("FileNameTagChecker.5"), //$NON-NLS-1$
          backingFileName, null, expectedBackingFileNameNoExtension + splitBackingFileName[1]);
    }
  }

  @Override
  public void check(GenericTag genericTag) {
    if (genericTag == null) {
      return;
    }

    /* get the backing file */
    File backingFile = genericTag.getBackingFile();
    if (backingFile == null) {
      genericTag.addReport(GenericTagFieldName.FILE, Messages.getString("FileNameTagChecker.7"), null, null, null); //$NON-NLS-1$
      return;
    }

    /* exit early when there are no fields */
    if (genericTag.getFields().size() == 0) {
      genericTag.addReport(GenericTagFieldName.FILE, Messages.getString("FileNameTagChecker.6"), null, null, null); //$NON-NLS-1$
      return;
    }

    checkArtistDirectory(genericTag);
    checkAlbumDirectory(genericTag);
    checkFilename(genericTag);
  }
}
