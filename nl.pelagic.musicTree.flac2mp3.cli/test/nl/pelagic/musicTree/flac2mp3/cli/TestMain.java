package nl.pelagic.musicTree.flac2mp3.cli;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
import nl.pelagic.audio.conversion.flac2mp3.testhelpers.MyFlacToMp3;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConfiguration;
import nl.pelagic.audio.musicTree.syncer.testhelpers.MySyncer;
import nl.pelagic.musicTree.flac2mp3.cli.i18n.Messages;
import nl.pelagic.shell.script.listener.testhelpers.MyShellScriptListener;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kohsuke.args4j.CmdLineParser;

@SuppressWarnings({
    "javadoc", "nls"
})
public class TestMain {

  File testdatadir = new File("testresources/testdata");

  Main main = null;
  MyShellScriptListener shellScriptListener = null;
  MyFlacToMp3 flacToMp3 = null;
  MySyncer syncer = null;
  private MyPrintStream out = null;
  static private String outfile = "testresources/out";

  static private List<String> usageStrings = new LinkedList<>();

  @BeforeClass
  static public void init() throws FileNotFoundException {
    CommandLineOptions commandLineOptions = new CommandLineOptions();
    CmdLineParser parser = new CmdLineParser(commandLineOptions);
    try (MyPrintStream out = new MyPrintStream(outfile)) {
      CommandLineOptions.usage(out, Main.PROGRAM_NAME, parser);
      usageStrings.addAll(out.strings);
    }
    finally {
      new File(outfile).delete();
    }
  }

  @AfterClass
  static public void destroy() {
    usageStrings.clear();
  }

  @Before
  public void setUp() throws FileNotFoundException {
    out = new MyPrintStream(outfile);
    main = new Main();
    shellScriptListener = new MyShellScriptListener();
    flacToMp3 = new MyFlacToMp3();
    syncer = new MySyncer();
    main.setShellScriptListener(shellScriptListener);
    main.setFlacToMp3(flacToMp3);
    main.setSyncer(syncer);
  }

  @After
  public void tearDown() {
    main.setSyncer(null);
    main.setFlacToMp3(null);
    main.setShellScriptListener(null);
    syncer = null;
    flacToMp3 = null;
    shellScriptListener = null;
    main = null;
    new File(outfile).delete();
  }

  @Test
  public void testReadFileList_DoesNotExist() {
    File fileList = new File("testresources/fileListReallyDoesNotExist.txt");
    List<String> entriesToConvert = new LinkedList<>();

    boolean result = Main.readFileList(out, fileList, entriesToConvert);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(entriesToConvert.size()), equalTo(Integer.valueOf(0)));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    assertThat(out.strings.get(0), equalTo(String.format(Messages.getString("Main.8"), fileList.getPath())));
  }

  @Test
  public void testReadFileList_Empty() {
    File fileList = new File("testresources/fileListEmpty.txt");
    List<String> entriesToConvert = new LinkedList<>();

    boolean result = Main.readFileList(out, fileList, entriesToConvert);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));

    assertThat(Integer.valueOf(entriesToConvert.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testReadFileList_Normal() {
    File fileList = new File("testresources/fileListNormal.txt");
    List<String> entriesToConvert = new LinkedList<>();

    boolean result = Main.readFileList(out, fileList, entriesToConvert);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));

    assertThat(Integer.valueOf(entriesToConvert.size()), equalTo(Integer.valueOf(2)));

    assertThat(entriesToConvert.get(0), equalTo("testresources/testdata/Music/dummy1.flac"));
    assertThat(entriesToConvert.get(1), equalTo("testresources/testdata/Music/dummydir1/dummy1.flac"));
  }

  @Test
  public void testValidateEntryToConvert_DoesNotExist() {
    File fbd = new File(testdatadir, "Music");
    String entry = "scanpathdoesnotexist";

    boolean result =
        Main.validateEntryToConvert(out, new MusicTreeConfiguration(fbd, new File(testdatadir, "from.flac")), new File(
            entry));
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    assertThat(out.strings.get(0), equalTo(String.format(Messages.getString("Main.5"), entry)));
  }

  @Test
  public void testValidateEntryToConvert_NotBelowFlacBaseDir() {
    File fbd = new File(testdatadir, "Music");
    String entry = "testresources/testdata/from.flac/dummy22.mp3";

    boolean result =
        Main.validateEntryToConvert(out, new MusicTreeConfiguration(fbd, new File(testdatadir, "from.flac")), new File(
            entry));
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    assertThat(out.strings.get(0), equalTo(String.format(Messages.getString("Main.6"), entry, fbd.getPath())));
  }

  @Test
  public void testValidateEntryToConvert_Normal() {
    File fbd = new File(testdatadir, "Music");
    String entry = "testresources/testdata/Music/dummy1.flac";

    boolean result =
        Main.validateEntryToConvert(out, new MusicTreeConfiguration(fbd, new File(testdatadir, "from.flac")), new File(
            entry));
    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testConvertFile_NotBelow() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "from.flac");
    Flac2Mp3Configuration flac2Mp3Configuration = new Flac2Mp3Configuration();
    MusicTreeConfiguration musicTreeConfiguration = new MusicTreeConfiguration(fbd, mbd);
    File fileToConvert = new File(mbd, "dummy22.mp3");

    boolean result = main.convertFile(out, flac2Mp3Configuration, musicTreeConfiguration, true, fileToConvert);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    assertThat(out.strings.get(0), equalTo(String.format(Messages.getString("Main.12"), fileToConvert.getPath(), //$NON-NLS-1$
        musicTreeConfiguration.getMp3BaseDir().getPath())));
  }

  @Test
  public void testConvertFile_Exception() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "from.flac");
    Flac2Mp3Configuration flac2Mp3Configuration = new Flac2Mp3Configuration();
    MusicTreeConfiguration musicTreeConfiguration = new MusicTreeConfiguration(fbd, mbd);
    File fileToConvert = new File(fbd, "dummy1.flac");

    flacToMp3.throwException = true;

    boolean result = main.convertFile(out, flac2Mp3Configuration, musicTreeConfiguration, true, fileToConvert);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    assertThat(out.strings.get(0),
        equalTo(String.format(Messages.getString("Main.2"), fileToConvert.getPath(), MyFlacToMp3.msg)));
  }

  @Test
  public void testConvertFile_Fail() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "from.flac");
    Flac2Mp3Configuration flac2Mp3Configuration = new Flac2Mp3Configuration();
    MusicTreeConfiguration musicTreeConfiguration = new MusicTreeConfiguration(fbd, mbd);
    File fileToConvert = new File(fbd, "dummy1.flac");

    flacToMp3.retval = false;

    boolean result = main.convertFile(out, flac2Mp3Configuration, musicTreeConfiguration, true, fileToConvert);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    assertThat(out.strings.get(0), equalTo(String.format(Messages.getString("Main.1"), fileToConvert.getPath())));
  }

  @Test
  public void testConvertFile_Ok() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "from.flac");
    Flac2Mp3Configuration flac2Mp3Configuration = new Flac2Mp3Configuration();
    MusicTreeConfiguration musicTreeConfiguration = new MusicTreeConfiguration(fbd, mbd);
    File fileToConvert = new File(fbd, "dummy1.flac");

    flacToMp3.retval = true;

    boolean result = main.convertFile(out, flac2Mp3Configuration, musicTreeConfiguration, true, fileToConvert);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testConvertFile_No_Conversion_Needed() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "from.flac");
    Flac2Mp3Configuration flac2Mp3Configuration = new Flac2Mp3Configuration();
    MusicTreeConfiguration musicTreeConfiguration = new MusicTreeConfiguration(fbd, mbd);
    File fileToConvert = new File(fbd, "dummy2.flac");
    File fileToConvertTo = new File(mbd, "Music/dummy2.mp3");
    boolean modified = fileToConvertTo.setLastModified(fileToConvert.lastModified());
    assertThat(Boolean.valueOf(modified), equalTo(Boolean.TRUE));

    flacToMp3.retval = true;

    boolean result = main.convertFile(out, flac2Mp3Configuration, musicTreeConfiguration, true, fileToConvert);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testConvertFile_Conversion_Needed() {
    File fbd = new File(testdatadir, "Music");
    File mbd = new File(testdatadir, "from.flac");
    Flac2Mp3Configuration flac2Mp3Configuration = new Flac2Mp3Configuration();
    MusicTreeConfiguration musicTreeConfiguration = new MusicTreeConfiguration(fbd, mbd);
    File fileToConvert = new File(fbd, "dummy2.flac");
    File fileToConvertTo = new File(mbd, "Music/dummy2.mp3");
    boolean modified = fileToConvertTo.setLastModified(fileToConvert.lastModified() - 10000);
    assertThat(Boolean.valueOf(modified), equalTo(Boolean.TRUE));

    flacToMp3.retval = true;

    boolean result = main.convertFile(out, flac2Mp3Configuration, musicTreeConfiguration, true, fileToConvert);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testStayAlive_Not() {
    MyBundleContext bundleContext = new MyBundleContext();
    bundleContext.property = "false";
    main.activate(bundleContext);
    try {
      main.stayAlive(out);
    }
    finally {
      main.deactivate();
    }

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
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
  public void testStayAlive_Yes() {
    MyStopper stopper = new MyStopper();
    stopper.st = Thread.currentThread();

    MyBundleContext bundleContext = new MyBundleContext();
    bundleContext.property = "true";
    main.activate(bundleContext);
    try {
      stopper.start();
      main.stayAlive(out);
    }
    finally {
      main.deactivate();
    }

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    assertThat(out.strings.get(0), equalTo(String.format(Messages.getString("Main.7"))));
  }

  @Test
  public void testDoMain_InvalidArgument() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args = {
      "--dummyargument"
    };
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain(out);
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1 + usageStrings.size())));
    List<String> expected = new LinkedList<>();
    expected.add(String.format(Messages.getString("Main.4"), "\"" + args[0] + "\" is not a valid option"));
    expected.addAll(usageStrings);
    assertThat(out.strings, equalTo(expected));
  }

  @Test
  public void testDoMain_Help() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args = {
      "-h"
    };
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain(out);
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(usageStrings.size())));
    assertThat(out.strings, equalTo(usageStrings));
  }

  @Test
  public void testDoMain_Invalid_MusicTreeConfiguration() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args = {
        "-f", "/some/flac/base/dir/that/does/not/exist", "-m", "/some/mp3/base/dir/dir/that/does/not/exist", "-s"
    };
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain(out);
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(2)));
    /*
     * can't test the contents of the strings: can't access the strings of
     * MusicTreeConfiguration
     */
  }

  @Test
  public void testDoMain_NullArgs_Error() {
    MyBundleContext bc = new MyBundleContext();

    syncer.returnValue = false;
    flacToMp3.retval = false;

    main.activate(bc);
    boolean result = main.doMain(out);
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(2)));
    /*
     * can't test the contents of the strings: can't access the strings of
     * MusicTreeConfiguration
     */
  }

  @Test
  public void testDoMain_FileList_Empty_Sync_Error() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args =
        {
            "-f",
            new File(testdatadir, "Music").getPath(),
            "-m",
            new File(testdatadir, "from.flac").getPath(),
            "-l",
            "testresources/fileListEmpty.txt",
            "-s"
        };

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain(out);
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testDoMain_File_Validation_Error() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args =
        {
            "-f",
            new File(testdatadir, "Music").getAbsolutePath(),
            "-m",
            new File(testdatadir, "from.flac").getAbsolutePath(),
            "-s",
            new File(testdatadir, "from.flac/dummy22.mp3").getAbsolutePath()
        };

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain(out);
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    assertThat(out.strings.get(0), equalTo(String.format(Messages.getString("Main.6"), args[5], args[1])));
  }

  @Test
  public void testDoMain_File_Conversion_Error() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args =
        {
            "-f",
            new File(testdatadir, "Music").getAbsolutePath(),
            "-m",
            new File(testdatadir, "from.flac").getAbsolutePath(),
            "-s",
            new File(testdatadir, "Music/dummy1.flac").getAbsolutePath()
        };

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    flacToMp3.retval = false;

    main.activate(bc);
    boolean result = main.doMain(out);
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(1)));
    assertThat(out.strings.get(0), equalTo(String.format(Messages.getString("Main.1"), args[5])));
  }

  @Test
  public void testDoMain_DirectoryAndFile_Ok() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args =
        {
            "-f",
            new File(testdatadir, "Music").getAbsolutePath(),
            "-m",
            new File(testdatadir, "from.flac").getAbsolutePath(),
            "-s",
            new File(testdatadir, "Music/dummydir1").getAbsolutePath(),
            new File(testdatadir, "Music/dummy1.flac").getAbsolutePath()
        };

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    syncer.returnValue = true;
    flacToMp3.retval = true;

    main.activate(bc);
    boolean result = main.doMain(out);
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testDoMain_File_Ok() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args =
        {
            "-f",
            new File(testdatadir, "Music").getAbsolutePath(),
            "-m",
            new File(testdatadir, "from.flac").getAbsolutePath(),
            "-s",
            new File(testdatadir, "Music/dummy1.flac").getAbsolutePath()
        };

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    syncer.returnValue = true;
    flacToMp3.retval = true;

    main.activate(bc);
    main.run();
    main.deactivate();
  }
}
