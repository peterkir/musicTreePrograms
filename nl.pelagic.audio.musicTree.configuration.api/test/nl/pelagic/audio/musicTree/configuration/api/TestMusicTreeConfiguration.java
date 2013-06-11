package nl.pelagic.audio.musicTree.configuration.api;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.List;

import nl.pelagic.audio.musicTree.configuration.api.i18n.Messages;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "javadoc", "nls"
})
public class TestMusicTreeConfiguration {

  private MusicTreeConfiguration musicTreeConfiguration = null;

  private File testdatadir = new File("testresources");

  @Before
  public void setUp() {
    musicTreeConfiguration = new MusicTreeConfiguration();
  }

  @After
  public void tearDown() {
    musicTreeConfiguration = null;
  }

  @Test
  public void testMusicTreeConfiguration_Defaults() {
    assertThat(musicTreeConfiguration.getFlacBaseDir().getPath(),
        equalTo(MusicTreeConfiguration.DEFAULT_FLAC_BASE_DIRECTORY));
    assertThat(musicTreeConfiguration.getMp3BaseDir().getPath(),
        equalTo(MusicTreeConfiguration.DEFAULT_MP3_BASE_DIRECTORY));
  }

  @Test
  public void testMusicTreeConfiguration_InitConstructor() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "from.flac");
    musicTreeConfiguration = new MusicTreeConfiguration(fbd, mbd);

    assertThat(musicTreeConfiguration.getFlacBaseDir(), equalTo(fbd));
    assertThat(musicTreeConfiguration.getMp3BaseDir(), equalTo(mbd));
  }

  @Test
  public void testSetFlacBaseDir() {
    File bd = new File("some flac base directory");
    musicTreeConfiguration.setFlacBaseDir(bd);
    assertThat(musicTreeConfiguration.getFlacBaseDir(), equalTo(bd));
  }

  @Test
  public void testSetMp3BaseDir() {
    File bd = new File("some mp3 base directory");
    musicTreeConfiguration.setMp3BaseDir(bd);
    assertThat(musicTreeConfiguration.getMp3BaseDir(), equalTo(bd));
  }

  @Test
  public void testValidate_BaseDirs_AreNull() {
    File fbd = null;
    File mbd = null;
    musicTreeConfiguration.setFlacBaseDir(fbd);
    musicTreeConfiguration.setMp3BaseDir(mbd);

    List<String> result = musicTreeConfiguration.validate(true);

    assertThat(result, notNullValue());
    assertThat(Integer.valueOf(result.size()), equalTo(Integer.valueOf(2)));
    String s = result.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("MusicTreeConfiguration.0"), "null")));
    s = result.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("MusicTreeConfiguration.3"), "null")));
  }

  @Test
  public void testValidate_BaseDirs_AreFiles() {
    File fbd = new File(testdatadir, "Music/flac/file.flac");
    File mbd = new File(testdatadir, "from.flac/.gitignore");
    musicTreeConfiguration.setFlacBaseDir(fbd);
    musicTreeConfiguration.setMp3BaseDir(mbd);

    List<String> result = musicTreeConfiguration.validate(true);

    assertThat(result, notNullValue());
    assertThat(Integer.valueOf(result.size()), equalTo(Integer.valueOf(2)));
    String s = result.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("MusicTreeConfiguration.0"), fbd.getPath())));
    s = result.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("MusicTreeConfiguration.1"), mbd.getPath())));
  }

  @Test
  public void testValidate_BaseDirs_AreFiles_DontCheckMp3BaseDir() {
    File fbd = new File(testdatadir, "Music/flac/file.flac");
    File mbd = new File(testdatadir, "from.flac/.gitignore");
    musicTreeConfiguration.setFlacBaseDir(fbd);
    musicTreeConfiguration.setMp3BaseDir(mbd);

    List<String> result = musicTreeConfiguration.validate(false);

    assertThat(result, notNullValue());
    assertThat(Integer.valueOf(result.size()), equalTo(Integer.valueOf(1)));
    String s = result.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("MusicTreeConfiguration.0"), fbd.getPath())));
  }

  @Test
  public void testValidate_Mp3BaseDir_SubdirOf_FlacBaseDir() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "Music/flac");
    musicTreeConfiguration.setFlacBaseDir(fbd);
    musicTreeConfiguration.setMp3BaseDir(mbd);

    List<String> result = musicTreeConfiguration.validate(true);

    assertThat(result, notNullValue());
    assertThat(Integer.valueOf(result.size()), equalTo(Integer.valueOf(1)));
    String s = result.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("MusicTreeConfiguration.2"), //$NON-NLS-1$
        mbd.getPath(), fbd.getPath())));
  }

  @Test
  public void testValidate_Mp3BaseDir_Is_FlacBaseDir() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "Music");
    musicTreeConfiguration.setFlacBaseDir(fbd);
    musicTreeConfiguration.setMp3BaseDir(mbd);

    List<String> result = musicTreeConfiguration.validate(true);

    assertThat(result, notNullValue());
    assertThat(Integer.valueOf(result.size()), equalTo(Integer.valueOf(1)));
    String s = result.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("MusicTreeConfiguration.2"), //$NON-NLS-1$
        mbd.getPath(), fbd.getPath())));
  }

  @Test
  public void testValidate_Ok() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "from.flac");
    musicTreeConfiguration.setFlacBaseDir(fbd);
    musicTreeConfiguration.setMp3BaseDir(mbd);

    List<String> result = musicTreeConfiguration.validate(true);

    assertThat(result, nullValue());
  }
}
