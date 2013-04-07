package nl.pelagic.audio.conversion.flac2mp3;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import nl.pelagic.audio.conversion.flac2mp3.testhelpers.TagHelper;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.KeyNotFoundException;
import org.jaudiotagger.tag.flac.FlacTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class TestTagInformation {

  TagInformation tagInformation = null;

  @Before
  public void setUp() {
    tagInformation = new TagInformation();
  }

  @After
  public void tearDown() {
    tagInformation = null;
  }

  @Test
  public void test_Defaults() {
    assertThat(tagInformation.getAlbum(), equalTo(TagInformation.DEFAULT_ALBUM));
    assertThat(tagInformation.getAlbumArtist(), equalTo(TagInformation.DEFAULT_ALBUMARTIST));
    assertThat(tagInformation.getArtist(), equalTo(TagInformation.DEFAULT_ARTIST));
    assertThat(tagInformation.getDate(), equalTo(TagInformation.DEFAULT_DATE));
    assertThat(tagInformation.getDiscNumber(), equalTo(TagInformation.DEFAULT_DISCNUMBER));
    assertThat(tagInformation.getGenre(), equalTo(TagInformation.DEFAULT_GENRE));
    assertThat(tagInformation.getTitle(), equalTo(TagInformation.DEFAULT_TITLE));
    assertThat(tagInformation.getTrackNumber(), equalTo(TagInformation.DEFAULT_TRACKNUMBER));
    assertThat(tagInformation.getTrackTotal(), equalTo(TagInformation.DEFAULT_TRACKTOTAL));
  }

  @Test
  public void test_FlacTag_Null() {
    tagInformation = new TagInformation(null);

    assertThat(tagInformation.getAlbum(), equalTo(TagInformation.DEFAULT_ALBUM));
    assertThat(tagInformation.getAlbumArtist(), equalTo(TagInformation.DEFAULT_ALBUMARTIST));
    assertThat(tagInformation.getArtist(), equalTo(TagInformation.DEFAULT_ARTIST));
    assertThat(tagInformation.getDate(), equalTo(TagInformation.DEFAULT_DATE));
    assertThat(tagInformation.getDiscNumber(), equalTo(TagInformation.DEFAULT_DISCNUMBER));
    assertThat(tagInformation.getGenre(), equalTo(TagInformation.DEFAULT_GENRE));
    assertThat(tagInformation.getTitle(), equalTo(TagInformation.DEFAULT_TITLE));
    assertThat(tagInformation.getTrackNumber(), equalTo(TagInformation.DEFAULT_TRACKNUMBER));
    assertThat(tagInformation.getTrackTotal(), equalTo(TagInformation.DEFAULT_TRACKTOTAL));
  }

  @Test
  public void test_FlacTag_Default() {
    FlacTag flacTag = new FlacTag();

    tagInformation = new TagInformation(flacTag);

    assertThat(tagInformation.getAlbum(), equalTo(TagInformation.DEFAULT_ALBUM));
    assertThat(tagInformation.getAlbumArtist(), equalTo(TagInformation.DEFAULT_ALBUMARTIST));
    assertThat(tagInformation.getArtist(), equalTo(TagInformation.DEFAULT_ARTIST));
    assertThat(tagInformation.getDate(), equalTo(TagInformation.DEFAULT_DATE));
    assertThat(tagInformation.getDiscNumber(), equalTo(TagInformation.DEFAULT_DISCNUMBER));
    assertThat(tagInformation.getGenre(), equalTo(TagInformation.DEFAULT_GENRE));
    assertThat(tagInformation.getTitle(), equalTo(TagInformation.DEFAULT_TITLE));
    assertThat(tagInformation.getTrackNumber(), equalTo(TagInformation.DEFAULT_TRACKNUMBER));
    assertThat(tagInformation.getTrackTotal(), equalTo(TagInformation.DEFAULT_TRACKTOTAL));
  }

  @Test
  public void test_FlacTag_NonDefault() throws KeyNotFoundException, FieldDataInvalidException {
    tagInformation = new TagInformation(TagHelper.getRandomFlacTag());

    assertThat(tagInformation.getAlbum(), equalTo(TagHelper.album));
    assertThat(tagInformation.getAlbumArtist(), equalTo(TagHelper.albumArtist));
    assertThat(tagInformation.getArtist(), equalTo(TagHelper.artist));
    assertThat(tagInformation.getDate(), equalTo(TagHelper.date));
    assertThat(tagInformation.getDiscNumber(), equalTo(TagHelper.discNumber + "/" + TagHelper.discTotal)); //$NON-NLS-1$
    assertThat(tagInformation.getGenre(), equalTo(TagHelper.genre));
    assertThat(tagInformation.getTitle(), equalTo(TagHelper.title));
    assertThat(tagInformation.getTrackNumber(), equalTo(TagHelper.tracknumber));
    assertThat(tagInformation.getTrackTotal(), equalTo(TagHelper.trackTotal));
  }
}
