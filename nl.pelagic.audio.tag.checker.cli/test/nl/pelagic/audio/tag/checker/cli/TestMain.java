package nl.pelagic.audio.tag.checker.cli;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nl.pelagic.audio.tag.checker.api.TagChecker;
import nl.pelagic.audio.tag.checker.cli.i18n.Messages;
import nl.pelagic.audio.tag.checker.types.AudioTagCheckerConfiguration;

import org.jaudiotagger.tag.flac.FlacTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.osgi.framework.BundleContext;

@SuppressWarnings({
    "nls", "javadoc", "static-method"
})
public class TestMain {

  private MyPrintStream out = null;
  private MyPrintStream err = null;
  private MyTagChecker mytc = null;
  private MyTagConverter mytcc = null;
  private MyAudioTagChecker myatc = null;
  private Main main = null;
  private String outfile = "testresources/out";
  private String errfile = "testresources/err";

  @Before
  public void setUp() throws FileNotFoundException {
    out = new MyPrintStream(outfile);
    err = new MyPrintStream(errfile);
    main = new Main();
    mytc = new MyTagChecker();
    mytcc = new MyTagConverter();
    myatc = new MyAudioTagChecker();
    myatc.main = main;
    main.addTagChecker(mytc);
    main.addTagConverter(mytcc);
    main.setAudioTagChecker(myatc);
  }

  @After
  public void tearDown() {
    main.removeTagConverter(mytcc);
    main.removeTagChecker(mytc);
    myatc = null;
    mytcc = null;
    mytc = null;
    main = null;
    new File(outfile).delete();
    new File(errfile).delete();
  }

  @Test
  public void testIsInTagCheckers() {
    TagChecker mytc = new MyTagChecker();
    main.addTagChecker(mytc);

    assertThat(Boolean.valueOf(main.isInTagCheckers("dummy")), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(main.isInTagCheckers(mytc.getClass().getSimpleName())), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(main.isInTagCheckers(mytc.getClass().getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testValidateConfiguration_InvalidPath() throws CmdLineException {
    File f = new File("/some/path/that/does/not/exist/really!");
    String[] args = {
      f.getPath()
    };
    CommandLineOptions cli = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(cli);
    parser.parseArgument(args);

    main.validateConfiguration(cli, out);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(2)));
    String s = out.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Main.0"), f.getPath())));
    s = out.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(Messages.getString("Main.1")));
  }

  @Test
  public void testValidateConfiguration_InvalidRegex() throws CmdLineException {
    String regex = "^[";
    String[] args = {
        "--regex", regex, new File("testresources").getPath()
    };
    CommandLineOptions cli = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(cli);
    parser.parseArgument(args);

    main.validateConfiguration(cli, out);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    String s = out.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Main.2"), regex)));
  }

  @Test
  public void testValidateConfiguration_InvalidTagChecker() throws CmdLineException {
    String tc = "thistagcheckerisveryunknown!";
    String[] args = {
        "--disable", tc, new File("testresources").getPath()
    };
    CommandLineOptions cli = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(cli);
    parser.parseArgument(args);

    main.validateConfiguration(cli, out);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    String s = out.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Main.3"), tc)));
  }

  @Test
  public void testValidateConfiguration_Valid1() throws CmdLineException {
    String[] args = {
        "--regex", "^[a].*$", "--enable", mytc.getClass().getSimpleName(), new File("testresources").getPath()
    };
    CommandLineOptions cli = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(cli);
    parser.parseArgument(args);

    main.validateConfiguration(cli, out);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testValidateConfiguration_Valid2() throws CmdLineException {
    String[] args = {
        "--regex", "", "--enable", mytc.getClass().getSimpleName(), new File("testresources").getPath()
    };
    CommandLineOptions cli = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(cli);
    parser.parseArgument(args);

    main.validateConfiguration(cli, out);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testListTagCheckers() {
    main.listTagCheckers(out);

    assertThat(Integer.valueOf(out.strings.size()), not(equalTo(Integer.valueOf(0))));
  }

  @Test
  public void testConstructExtensionRegex() {
    AudioTagCheckerConfiguration config = new AudioTagCheckerConfiguration();
    config.setRegexInAllDirs(false);
    String regex = null;
    String s = Main.constructExtensionRegex(regex, config);

    assertThat(s, notNullValue());
    assertThat(s, equalTo("^.*\\.(aif|flac|m4a|m4b|m4p|mp3|mp4|ogg|ra|rm|wav|wma)$"));
    assertThat(Boolean.valueOf(config.isRegexInAllDirs()), equalTo(Boolean.TRUE));

    config.setRegexInAllDirs(false);
    regex = "";
    s = Main.constructExtensionRegex(regex, config);

    assertThat(s, notNullValue());
    assertThat(s, equalTo("^.*\\.(aif|flac|m4a|m4b|m4p|mp3|mp4|ogg|ra|rm|wav|wma)$"));
    assertThat(Boolean.valueOf(config.isRegexInAllDirs()), equalTo(Boolean.TRUE));

    config.setRegexInAllDirs(false);
    regex = "dummy";
    s = Main.constructExtensionRegex(regex, config);

    assertThat(s, notNullValue());
    assertThat(s, equalTo(regex));
    assertThat(Boolean.valueOf(config.isRegexInAllDirs()), equalTo(Boolean.FALSE));

  }

  @Test
  public void testPrintSettings_NoDiagnostics() {
    CommandLineOptions options = new CommandLineOptions();
    AudioTagCheckerConfiguration config = new AudioTagCheckerConfiguration();

    main.printSettings(out, options, config);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testPrintSettings_Diagnostics() {
    CommandLineOptions options = new CommandLineOptions();
    options.diagnostics = true;
    AudioTagCheckerConfiguration config = new AudioTagCheckerConfiguration();

    main.printSettings(out, options, config);

    assertThat(Integer.valueOf(out.strings.size()), not(equalTo(Integer.valueOf(0))));
  }

  @Test
  public void testPrintDiagnostics_NoDiagnostics() {
    CommandLineOptions options = new CommandLineOptions();
    Date startDate = new Date();

    main.printDiagnostics(out, options, startDate, startDate);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testPrintDiagnostics_Diagnostics_No_Converter() {
    main.removeTagConverter(mytcc);
    CommandLineOptions options = new CommandLineOptions();
    options.diagnostics = true;
    Date startDate = new Date();
    Date endDate = new Date();
    endDate.setTime(startDate.getTime() + 10000);

    main.printDiagnostics(out, options, startDate, endDate);

    assertThat(Integer.valueOf(out.strings.size()), not(equalTo(Integer.valueOf(0))));
  }

  @Test
  public void testPrintDiagnostics_Diagnostics_Empty_Converter() {
    mytcc.supportedTagClasses = null;
    mytcc.unknownTagFieldNames = null;
    CommandLineOptions options = new CommandLineOptions();
    options.diagnostics = true;
    Date startDate = new Date();
    Date endDate = new Date();
    endDate.setTime(startDate.getTime() + 10000);

    main.printDiagnostics(out, options, startDate, endDate);

    assertThat(Integer.valueOf(out.strings.size()), not(equalTo(Integer.valueOf(0))));
  }

  @Test
  public void testPrintDiagnostics_Diagnostics_Empty_Converter_Sets() {
    mytcc.supportedTagClasses.clear();
    mytcc.unknownTagFieldNames.clear();
    CommandLineOptions options = new CommandLineOptions();
    options.diagnostics = true;
    Date startDate = new Date();
    Date endDate = new Date();
    endDate.setTime(startDate.getTime() + 10000);

    main.printDiagnostics(out, options, startDate, endDate);

    assertThat(Integer.valueOf(out.strings.size()), not(equalTo(Integer.valueOf(0))));
  }

  @Test
  public void testPrintDiagnostics_Diagnostics_Converter_With_Empty_UnknownTagFieldNames() {
    mytcc.unknownTagFieldNames.get(FlacTag.class).clear();
    CommandLineOptions options = new CommandLineOptions();
    options.diagnostics = true;
    Date startDate = new Date();
    Date endDate = new Date();
    endDate.setTime(startDate.getTime() + 10000);

    main.printDiagnostics(out, options, startDate, endDate);

    assertThat(Integer.valueOf(out.strings.size()), not(equalTo(Integer.valueOf(0))));
  }

  @Test
  public void testPrintDiagnostics_Diagnostics_Normal_Converter() {
    CommandLineOptions options = new CommandLineOptions();
    options.diagnostics = true;
    Date startDate = new Date();
    Date endDate = new Date();
    endDate.setTime(startDate.getTime() + 10000);

    main.printDiagnostics(out, options, startDate, endDate);

    assertThat(Integer.valueOf(out.strings.size()), not(equalTo(Integer.valueOf(0))));
  }

  @SuppressWarnings("resource")
  @Test
  public void testDoMain_NoArgs() throws FileNotFoundException {
    boolean r = main.doMain(out, err);

    /* construct expected output */
    CommandLineOptions commandLineOptions = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(commandLineOptions);
    String tmpfile = "testresources/tmp";
    MyPrintStream tmp = new MyPrintStream(tmpfile);
    try {
      parser.parseArgument(new String[0]);
    }
    catch (CmdLineException e) {
      tmp.println(e.getMessage());
    }
    CommandLineOptions.usage(tmp, Main.PROGRAM_NAME, parser);
    tmp.close();
    new File(tmpfile).delete();

    tmp.strings.add(0, Messages.getString("Main.1"));

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(tmp.strings.size())));
    assertThat(err.strings, equalTo(tmp.strings));
  }

  @SuppressWarnings("resource")
  @Test
  public void testDoMain_InvalidOption() throws FileNotFoundException {
    String[] args = {
      "-invalidoption_really!"
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    boolean r = main.doMain(out, err);

    /* construct expected output */
    CommandLineOptions commandLineOptions = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(commandLineOptions);
    String tmpfile = "testresources/tmp";
    MyPrintStream tmp = new MyPrintStream(tmpfile);
    try {
      parser.parseArgument(args);
    }
    catch (CmdLineException e) {
      tmp.printf(Messages.getString("Main.32"), e.getLocalizedMessage());
    }
    CommandLineOptions.usage(tmp, Main.PROGRAM_NAME, parser);
    tmp.close();
    new File(tmpfile).delete();

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(tmp.strings.size())));
    assertThat(err.strings, equalTo(tmp.strings));
  }

  @SuppressWarnings("resource")
  @Test
  public void testDoMain_Help() throws FileNotFoundException {
    String[] args = {
      "-h"
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    boolean r = main.doMain(out, err);

    /* construct expected output */
    CommandLineOptions commandLineOptions = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(commandLineOptions);
    String tmpfile = "testresources/tmp";
    MyPrintStream tmp = new MyPrintStream(tmpfile);
    try {
      parser.parseArgument(args);
    }
    catch (CmdLineException e) {
      tmp.println(e.getMessage());
    }
    CommandLineOptions.usage(tmp, Main.PROGRAM_NAME, parser);
    tmp.close();
    new File(tmpfile).delete();

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(tmp.strings.size())));
    assertThat(err.strings, equalTo(tmp.strings));
  }

  @SuppressWarnings("resource")
  @Test
  public void testDoMain_ListCheckers() throws FileNotFoundException {
    String[] args = {
      "-l"
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    boolean r = main.doMain(out, err);

    /* construct expected output */
    String tmpfile = "testresources/tmp";
    MyPrintStream tmp = new MyPrintStream(tmpfile);
    main.listTagCheckers(tmp);
    tmp.close();
    new File(tmpfile).delete();

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(tmp.strings.size())));
    assertThat(err.strings, equalTo(tmp.strings));
  }

  @SuppressWarnings("resource")
  @Test
  public void testDoMain_ListCheckersAndHelp() throws FileNotFoundException {
    String[] args = {
        "-l", "-h"
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    boolean r = main.doMain(out, err);

    /* construct expected output */
    CommandLineOptions commandLineOptions = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(commandLineOptions);
    String tmpfile = "testresources/tmp";
    MyPrintStream tmp = new MyPrintStream(tmpfile);
    main.listTagCheckers(tmp);
    try {
      parser.parseArgument(args);
    }
    catch (CmdLineException e) {
      tmp.println(e.getMessage());
    }
    CommandLineOptions.usage(tmp, Main.PROGRAM_NAME, parser);
    tmp.close();
    new File(tmpfile).delete();

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(tmp.strings.size())));
    assertThat(err.strings, equalTo(tmp.strings));
  }

  @SuppressWarnings("resource")
  @Test
  public void testDoMain_InvalidChecker() throws FileNotFoundException {
    String tc = "notavalidtagcheckername_really!";
    String[] args = {
        "--disable", tc, new File("testresources").getPath()
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    boolean r = main.doMain(out, err);

    /* construct expected output */
    /* construct expected output */
    CommandLineOptions commandLineOptions = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(commandLineOptions);
    String tmpfile = "testresources/tmp";
    MyPrintStream tmp = new MyPrintStream(tmpfile);
    main.listTagCheckers(tmp);
    try {
      parser.parseArgument(args);
    }
    catch (CmdLineException e) {
      tmp.println(e.getMessage());
    }
    CommandLineOptions.usage(tmp, Main.PROGRAM_NAME, parser);
    tmp.close();
    new File(tmpfile).delete();

    tmp.strings.add(0, String.format(Messages.getString("Main.3"), tc));

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(tmp.strings.size())));
    assertThat(err.strings, equalTo(tmp.strings));
  }

  @Test
  public void testDoMain_Normal1() {
    String[] args = {
      new File("testresources").getPath()
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    boolean r = main.doMain(out, err);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testDoMain_Normal2() {
    String tc = mytc.getClass().getSimpleName();
    String[] args = {
        "--disable", tc, "--enable", tc, "-n", "-c", "-v", new File("testresources").getPath()
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    boolean r = main.doMain(out, err);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(1)));
    String s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(""));
  }

  @Test
  public void testDoMain_CheckFail() {
    String cp = new File("testresources").getPath();
    String[] args = {
        cp, cp, cp
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);
    boolean[] ces = {
        false, false, false
    };
    myatc.checkExceptions = ces;
    boolean[] crs = {
        true, false, false
    };
    myatc.checkResults = crs;
    boolean[] stops = {
        false, false, false
    };
    myatc.callShutdownHook = stops;

    boolean r = main.doMain(out, err);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(2)));
    String s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Main.8"), cp)));
    s = err.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Main.8"), cp)));
  }

  @Test
  public void testDoMain_CheckStop() {
    String cp = new File("testresources").getPath();
    String[] args = {
        cp, cp, cp
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);
    boolean[] ces = {
        false, false, false
    };
    myatc.checkExceptions = ces;
    boolean[] crs = {
        true, false, false
    };
    myatc.checkResults = crs;
    boolean[] stops = {
        true, false, false
    };
    myatc.callShutdownHook = stops;

    boolean r = main.doMain(out, err);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testDoMain_CheckException() {
    String cp = new File("testresources").getPath();
    String[] args = {
        cp, cp, cp
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);
    boolean[] ces = {
        true, true, true
    };
    myatc.checkExceptions = ces;

    boolean r = main.doMain(out, err);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(3)));
    String s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Main.9"), cp, "test")));
    s = err.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Main.9"), cp, "test")));
    s = err.strings.get(2);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Main.9"), cp, "test")));
  }

  @Test(timeout = 1000)
  public void testRun_PropNotSet() {
    String[] args = {
      new File("testresources").getPath()
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);
    BundleContext bundleContext = new MyBundleContext();
    main.activate(bundleContext);

    main.run();

    main.deactivate();
  }

  @Test(timeout = 1000)
  public void testRun_NoStayAlive() {
    String[] args = {
      new File("testresources").getPath()
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);
    MyBundleContext bundleContext = new MyBundleContext();
    bundleContext.property = "false";
    main.activate(bundleContext);

    main.run();

    main.deactivate();
  }

  class MyStopper extends Thread {
    Thread st = null;

    @Override
    public void run() {
      try {
        Thread.sleep(500);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
      st.interrupt();
    }
  }

  @Test(timeout = 1000)
  public void testRun_StayAlive() {
    MyStopper stopper = new MyStopper();
    stopper.st = Thread.currentThread();

    String[] args = {
      new File("testresources").getPath()
    };
    Map<String, Object> parameters = new HashMap<>();
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);
    MyBundleContext bundleContext = new MyBundleContext();
    bundleContext.property = "true";
    main.activate(bundleContext);

    stopper.start();
    main.run();

    main.deactivate();
  }
}
