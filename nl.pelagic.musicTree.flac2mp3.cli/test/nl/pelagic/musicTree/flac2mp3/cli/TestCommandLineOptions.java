package nl.pelagic.musicTree.flac2mp3.cli;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

@SuppressWarnings({
    "nls", "javadoc"
})
public class TestCommandLineOptions {

  private CommandLineOptions cli = null;
  private MyPrintStream out = null;
  private String outfile = "testresources/out";

  @Before
  public void setUp() throws FileNotFoundException {
    out = new MyPrintStream(outfile);
    cli = new CommandLineOptions();
  }

  @After
  public void tearDown() {
    cli = null;
    new File(outfile).delete();
  }

  @Test
  public void testCommandLineOptions_Defaults() throws IOException {
    assertThat(cli.getFlacBaseDir(), equalTo(new File(CommandLineOptions.DEFAULT_FLAC_BASE_DIR).getCanonicalFile()));
    assertThat(cli.getMp3BaseDir(), equalTo(new File(CommandLineOptions.DEFAULT_MP3_BASE_DIR).getCanonicalFile()));
    assertThat(cli.getFlacExecutable(), equalTo(new File(Flac2Mp3Configuration.DEFAULT_FLAC_EXECUTABLE)));
    assertThat(cli.getLameExecutable(), equalTo(new File(Flac2Mp3Configuration.DEFAULT_LAME_EXECUTABLE)));
    assertThat(cli.getFlacOptions(), equalTo(Flac2Mp3Configuration.DEFAULT_FLAC_OPTIONS));
    assertThat(cli.getLameOptions(), equalTo(Flac2Mp3Configuration.DEFAULT_LAME_OPTIONS));
    List<String> entries = cli.getEntriesToConvert();
    assertThat(entries, notNullValue());
    assertThat(Integer.valueOf(entries.size()), equalTo(Integer.valueOf(0)));
    assertThat(cli.getFileList(), nullValue());
    assertThat(Boolean.valueOf(cli.isHelp()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cli.isQuiet()), equalTo(Boolean.valueOf(CommandLineOptions.quietDefault)));
    assertThat(Boolean.valueOf(cli.isSimulate()), equalTo(Boolean.valueOf(CommandLineOptions.simulateDefault)));
    assertThat(Boolean.valueOf(cli.isVerbose()), equalTo(Boolean.valueOf(CommandLineOptions.verboseDefault)));
    assertThat(Boolean.valueOf(cli.isExtraVerbose()), equalTo(Boolean.valueOf(CommandLineOptions.extraVerboseDefault)));
  }

  @Test
  public void testSetFlacBaseDir() throws IOException {
    File dirFile = new File("");
    cli.setFlacBaseDir(dirFile);
    assertThat(cli.getFlacBaseDir(), equalTo(dirFile.getCanonicalFile()));
  }

  @Test
  public void testSetMp3BaseDir() throws IOException {
    File dirFile = new File("");
    cli.setMp3BaseDir(dirFile);
    assertThat(cli.getMp3BaseDir(), equalTo(dirFile.getCanonicalFile()));
  }

  @Test
  public void testSetFlacExecutable() {
    File dirFile = new File("/some/path/to/flac");
    cli.setFlacExecutable(dirFile);
    assertThat(cli.getFlacExecutable(), equalTo(dirFile));
  }

  @Test
  public void testSetLameExecutable() {
    File dirFile = new File("/some/path/to/lame");
    cli.setLameExecutable(dirFile);
    assertThat(cli.getLameExecutable(), equalTo(dirFile));
  }

  @Test
  public void testSetFlacOptions() {
    String options = "--dummy --bs";
    cli.setFlacOptions(options);
    assertThat(cli.getFlacOptions(), equalTo(options));
  }

  @Test
  public void testSetLameOptions() {
    String options = "--dummy --bs";
    cli.setLameOptions(options);
    assertThat(cli.getLameOptions(), equalTo(options));
  }

  @Test
  public void testSetFileList() throws IOException {
    String list = "some file list.lst";
    File dirFile = new File(list);
    cli.setFileList(dirFile);
    assertThat(cli.getFileList(), equalTo(dirFile.getCanonicalFile()));
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
  public void testUsage() throws CmdLineException {
    String[] args = {
      "/some/path"
    };
    CmdLineParser parser = new CmdLineParser(cli);
    parser.parseArgument(args);
    CommandLineOptions.usage(out, "programName", parser);

    assertThat(out.strings, notNullValue());
    assertThat(Integer.valueOf(out.strings.size()), not(equalTo(Integer.valueOf(0))));
  }
}
