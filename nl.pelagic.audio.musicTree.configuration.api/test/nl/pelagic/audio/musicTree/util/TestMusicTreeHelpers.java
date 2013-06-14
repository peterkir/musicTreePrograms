package nl.pelagic.audio.musicTree.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "nls"
})
public class TestMusicTreeHelpers {

  File testdatadir = new File("testresources");

  @Test
  public void testFlacFileToMp3File_NotBelow() {
    File result =
        MusicTreeHelpers.flacFileToMp3File(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"),
            new File(testdatadir, "from.flac"));
    assertThat(result, nullValue());
  }

  @Test
  public void testFlacFileToMp3File_Normal_Flac() throws IOException {
    File result =
        MusicTreeHelpers.flacFileToMp3File(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"),
            new File(testdatadir, "Music/dummy1" + MusicTreeConstants.FLACEXTENSION));
    assertThat(result.getCanonicalFile(), equalTo(new File(testdatadir, "from.flac/Music/dummy1"
        + MusicTreeConstants.MP3EXTENSION).getCanonicalFile()));
  }

  @Test
  public void testFlacFileToMp3File_Normal_Txt() throws IOException {
    File result =
        MusicTreeHelpers.flacFileToMp3File(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"),
            new File(testdatadir, "Music/dummy1.txt"));
    assertThat(result.getCanonicalFile(),
        equalTo(new File(testdatadir, "from.flac/Music/dummy1.txt").getCanonicalFile()));
  }

}
