package nl.pelagic.musicTree.flac2mp3.cli;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

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
    assertThat(cli.getFlacSubDir(), equalTo(CommandLineOptions.flacSubDirDefault));
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
