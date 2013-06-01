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
      if (primary == GenericTagFieldName.ALBUMTOTALTRACKS) {
        tag.addField(null, primary, primary + namePostfix, albumTotalTracks);
      } else if (primary == GenericTagFieldName.ALBUMDISCNUMBER) {
        tag.addField(null, primary, primary + namePostfix, discNumber);
      } else if (primary == GenericTagFieldName.ALBUMTITLE) {
        tag.addField(null, primary, primary + namePostfix, title);
      } else if (primary == GenericTagFieldName.TRACKNUMBER) {
        tag.addField(null, primary, primary + namePostfix, trackNumber);
      } else if (primary == GenericTagFieldName.ALBUMYEAR) {
        tag.addField(null, primary, primary + namePostfix, year);
      } else if (primary == GenericTagFieldName.ALBUMGENRE) {
        tag.addField(null, primary, primary + namePostfix, albumGenre);
      } else if (primary == GenericTagFieldName.TRACKARTIST) {
        tag.addField(null, primary, primary + namePostfix, trackArtist);
      } else if (primary == GenericTagFieldName.TRACKTITLE) {
        tag.addField(null, primary, primary + namePostfix, trackTitle);
      } else if (primary == GenericTagFieldName.ALBUMARTIST) {
        tag.addField(null, primary, primary + namePostfix, albumArtist);
      } else {
        assert (false);
      }
    }
    tag.addField(null, GenericTagFieldName.OTHER, "OTHER name", "OTHER value");

    return tag;
  }
}
