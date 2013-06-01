package nl.pelagic.audio.tag.checker.cli;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

@SuppressWarnings({
    "nls", "javadoc"
})
public class TestCommandLineOptions {

  private MyPrintStream out = null;
  private CommandLineOptions cli = null;
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
  public void test_Defaults() {
    List<File> cp = cli.getCheckPaths();
    assertThat(cp, notNullValue());
    assertThat(Integer.valueOf(cp.size()), equalTo(Integer.valueOf(0)));
    assertThat(Boolean.valueOf(cli.isDiagnostics()), equalTo(Boolean.FALSE));
    assertThat(cli.getRegex(), nullValue());
    assertThat(Boolean.valueOf(cli.isRegexInAllDirs()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cli.isRegexCaseSensitive()), equalTo(Boolean.FALSE));
    List<String> tcs = cli.getDisabledTagCheckers();
    assertThat(tcs, notNullValue());
    assertThat(Integer.valueOf(tcs.size()), equalTo(Integer.valueOf(0)));
    tcs = cli.getEnabledTagCheckers();
    assertThat(tcs, notNullValue());
    assertThat(Integer.valueOf(tcs.size()), equalTo(Integer.valueOf(0)));
    assertThat(Boolean.valueOf(cli.isHelp()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cli.isListCheckers()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cli.isNonRecursive()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cli.isVerbose()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cli.isExtraVerbose()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testSetHelp() {
    cli.setHelp(true);
    assertThat(Boolean.valueOf(cli.isHelp()), equalTo(Boolean.TRUE));
  }

  @Test
  public void testSetListCheckers() {
    cli.setListCheckers(true);
    assertThat(Boolean.valueOf(cli.isListCheckers()), equalTo(Boolean.TRUE));
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
