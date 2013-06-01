package nl.pelagic.audio.tag.checker.types;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method"
})
public class TestGenericTagFieldName {
  @Test
  public void testIsPrimary() {
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMARTIST.isPrimary(false)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMDISCNUMBER.isPrimary(false)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMGENRE.isPrimary(false)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTITLE.isPrimary(false)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTOTALTRACKS.isPrimary(false)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMYEAR.isPrimary(false)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ARTWORK.isPrimary(false)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.FILE.isPrimary(false)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.OTHER.isPrimary(false)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKARTIST.isPrimary(false)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKNUMBER.isPrimary(false)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKTITLE.isPrimary(false)), equalTo(Boolean.TRUE));

    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMARTIST.isPrimary(true)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMDISCNUMBER.isPrimary(true)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMGENRE.isPrimary(true)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTITLE.isPrimary(true)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTOTALTRACKS.isPrimary(true)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMYEAR.isPrimary(true)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ARTWORK.isPrimary(true)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.FILE.isPrimary(true)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.OTHER.isPrimary(true)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKARTIST.isPrimary(true)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKNUMBER.isPrimary(true)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKTITLE.isPrimary(true)), equalTo(Boolean.TRUE));
  }

  @Test
  public void testIsPseudoPrimary() {
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMARTIST.isPseudoPrimary()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMDISCNUMBER.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMGENRE.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTITLE.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTOTALTRACKS.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMYEAR.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ARTWORK.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.FILE.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.OTHER.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKARTIST.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKNUMBER.isPseudoPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKTITLE.isPseudoPrimary()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testIsNonPrimary() {
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMARTIST.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMDISCNUMBER.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMGENRE.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTITLE.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTOTALTRACKS.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMYEAR.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ARTWORK.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.FILE.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.OTHER.isNonPrimary()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKARTIST.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKNUMBER.isNonPrimary()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKTITLE.isNonPrimary()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testIsReportingOnly() {
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMARTIST.isReportingOnly()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMDISCNUMBER.isReportingOnly()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMGENRE.isReportingOnly()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTITLE.isReportingOnly()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMTOTALTRACKS.isReportingOnly()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ALBUMYEAR.isReportingOnly()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.ARTWORK.isReportingOnly()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.FILE.isReportingOnly()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(GenericTagFieldName.OTHER.isReportingOnly()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKARTIST.isReportingOnly()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKNUMBER.isReportingOnly()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(GenericTagFieldName.TRACKTITLE.isReportingOnly()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testGetPrimaries() {
    GenericTagFieldName[] withoutPseudo =
        {
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.ALBUMTOTALTRACKS,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKNUMBER,
            GenericTagFieldName.TRACKTITLE
        };

    GenericTagFieldName[] primaries = GenericTagFieldName.getPrimaries(false);
    assertThat(primaries, equalTo(withoutPseudo));

    GenericTagFieldName[] withPseudo =
        {
            GenericTagFieldName.ALBUMARTIST,
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.ALBUMTOTALTRACKS,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKNUMBER,
            GenericTagFieldName.TRACKTITLE
        };

    primaries = GenericTagFieldName.getPrimaries(true);
    assertThat(primaries, equalTo(withPseudo));
  }

}
