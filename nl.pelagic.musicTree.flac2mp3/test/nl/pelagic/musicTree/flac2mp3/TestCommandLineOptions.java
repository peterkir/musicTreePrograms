package nl.pelagic.musicTree.flac2mp3;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineParser;

@SuppressWarnings({
    "nls", "javadoc"
})
public class TestCommandLineOptions {

  CommandLineOptions cli = null;

  @Before
  public void setUp() {
    cli = new CommandLineOptions();
  }

  @After
  public void tearDown() {
    cli = null;
  }

  @Test
  public void testCommandLineOptions_Defaults() throws IOException {
    assertThat(cli.getFlacBaseDir(), equalTo(new File(CommandLineOptions.DEFAULT_FLAC_BASE_DIR).getCanonicalFile()));
    assertThat(cli.getMp3BaseDir(), equalTo(new File(CommandLineOptions.DEFAULT_MP3_BASE_DIR).getCanonicalFile()));
    assertThat(cli.getFlacSubDir(), equalTo(CommandLineOptions.flacSubDirDefault));
    assertThat(cli.getFileList(), nullValue());
    assertThat(Boolean.valueOf(cli.isHelp()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cli.isQuiet()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cli.isSimulate()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cli.isVerbose()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testSetFlacBaseDir() throws IOException {
    File dirFile = new File("");
    String dir = dirFile.getPath();
    cli.setFlacBaseDir(dir);
    assertThat(cli.getFlacBaseDir(), equalTo(dirFile.getCanonicalFile()));
  }

  @Test
  public void testSetMp3BaseDir() throws IOException {
    File dirFile = new File("");
    String dir = dirFile.getPath();
    cli.setMp3BaseDir(dir);
    assertThat(cli.getMp3BaseDir(), equalTo(dirFile.getCanonicalFile()));
  }

  @Test
  public void testSetFileList() {
    String list = "some file list.lst";
    File dirFile = new File(list);
    cli.setFileList(list);
    assertThat(cli.getFileList(), equalTo(dirFile));
  }

  @Test
  public void testSetHelp() {
    boolean value = true;
    cli.setHelp(value);
    assertThat(Boolean.valueOf(cli.isHelp()), equalTo(Boolean.valueOf(value)));
    value = false;
    cli.setHelp(value);
    assertThat(Boolean.valueOf(cli.isHelp()), equalTo(Boolean.valueOf(value)));
  }

  @Test
  public void testUsage() {
    CmdLineParser parser = new CmdLineParser(cli);
    CommandLineOptions.usage("programName", parser);
  }
}
