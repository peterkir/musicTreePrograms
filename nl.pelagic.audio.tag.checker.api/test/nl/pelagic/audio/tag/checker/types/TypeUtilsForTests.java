package nl.pelagic.audio.tag.checker.types;

import org.junit.Ignore;

@Ignore
@SuppressWarnings({
    "nls", "javadoc"
})
public class TypeUtilsForTests {
  public static GenericTagFieldName[] primaries = GenericTagFieldName.getPrimaries(true);
  public static final String namePostfix = " name";
  public static final String valuePostfix = " value";

  public static GenericTag setupTag(String albumTotalTracks, String discNumber, String title, String trackNumber,
      String year, String albumGenre, String trackArtist, String trackTitle, String albumArtist) {
    GenericTag tag = new GenericTag();
    for (GenericTagFieldName primary : primaries) {
      switch (primary) {
        case ALBUMTOTALTRACKS:
          tag.addField(null, primary, primary + namePostfix, albumTotalTracks);
          break;
        case ALBUMDISCNUMBER:
          tag.addField(null, primary, primary + namePostfix, discNumber);
          break;
        case ALBUMTITLE:
          tag.addField(null, primary, primary + namePostfix, title);
          break;
        case TRACKNUMBER:
          tag.addField(null, primary, primary + namePostfix, trackNumber);
          break;
        case ALBUMYEAR:
          tag.addField(null, primary, primary + namePostfix, year);
          break;
        case ALBUMGENRE:
          tag.addField(null, primary, primary + namePostfix, albumGenre);
          break;
        case TRACKARTIST:
          tag.addField(null, primary, primary + namePostfix, trackArtist);
          break;
        case TRACKTITLE:
          tag.addField(null, primary, primary + namePostfix, trackTitle);
          break;
        case ALBUMARTIST:
          tag.addField(null, primary, primary + namePostfix, albumArtist);
          break;
        default:
          assert (false);
          break;
      }
    }
    tag.addField(null, GenericTagFieldName.OTHER, "OTHER name", "OTHER value");

    return tag;
  }
}
