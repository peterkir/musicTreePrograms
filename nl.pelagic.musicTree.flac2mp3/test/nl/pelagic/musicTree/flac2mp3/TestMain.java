package nl.pelagic.musicTree.flac2mp3;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import nl.pelagic.audio.conversion.flac2mp3.testhelpers.MyFlacToMp3;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;
import nl.pelagic.audio.musicTree.syncer.testhelpers.MySyncer;
import nl.pelagic.musicTree.flac2mp3.testhelpers.MyBundleContext;
import nl.pelagic.shell.script.listener.testhelpers.MyShellScriptListener;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "javadoc", "nls"
})
public class TestMain {

  File testdatadir = new File("testresources/testdata");

  Main main = null;
  MyShellScriptListener shellScriptListener = null;
  MyFlacToMp3 flacToMp3 = null;
  MySyncer syncer = null;

  @Before
  public void setUp() {
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
  }

  @Test
  public void testValidateConfiguration_Nulls() {
    boolean result = Main.validateConfiguration(null, null, null);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    result = Main.validateConfiguration(new File(testdatadir, "Music"), null, null);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));

    result = Main.validateConfiguration(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"), null);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testValidateConfiguration_FlacBaseDir_IsAFile() {
    boolean result =
        Main.validateConfiguration(new File(testdatadir, "Music/dummy1.flac"), new File(testdatadir, "from.flac"),
            "Music");
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testValidateConfiguration_Mp3BaseDir_IsAFile() {
    boolean result =
        Main.validateConfiguration(new File(testdatadir, "Music"), new File(testdatadir, "from.flac/dummy22.mp3"),
            "Music");
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testValidateConfiguration_Mp3BaseDir_SubdirOf_FlacBaseDir() {
    boolean result =
        Main.validateConfiguration(new File(testdatadir, "Music"), new File(testdatadir, "Music/dummydir1"), "Music");
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testValidateConfiguration_Mp3BaseDir_Is_FlacBaseDir() {
    boolean result =
        Main.validateConfiguration(new File(testdatadir, "Music"), new File(testdatadir, "Music"), "Music");
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testValidateConfiguration_Scanpath_NotExists() {
    boolean result =
        Main.validateConfiguration(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"),
            "scanpathdoesnotexist");
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testValidateConfiguration_Scanpath_NotBelow_FlacBaseDir() {
    boolean result =
        Main.validateConfiguration(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"), "../from.flac");
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testValidateConfiguration_Normal_Relative_Scanpath() {
    boolean result = Main.validateConfiguration(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"), "");
    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
  }

  @Test
  public void testValidateConfiguration_Normal_Absolute_Scanpath() {
    boolean result =
        Main.validateConfiguration(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"), new File(
            testdatadir, "Music").getAbsolutePath());
    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
  }

  @Test
  public void testFlacFileToMp3File_NotBelow() {
    File result =
        Main.flacFileToMp3File(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"), new File(
            testdatadir, "from.flac"));
    assertThat(result, nullValue());
  }

  @Test
  public void testFlacFileToMp3File_Normal_Flac() throws IOException {
    File result =
        Main.flacFileToMp3File(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"), new File(
            testdatadir, "Music/dummy1" + MusicTreeConstants.FLACEXTENSION));
    assertThat(result.getCanonicalFile(), equalTo(new File(testdatadir, "from.flac/Music/dummy1"
        + MusicTreeConstants.MP3EXTENSION).getCanonicalFile()));
  }

  @Test
  public void testFlacFileToMp3File_Normal_Txt() throws IOException {
    File result =
        Main.flacFileToMp3File(new File(testdatadir, "Music"), new File(testdatadir, "from.flac"), new File(
            testdatadir, "Music/dummy1.txt"));
    assertThat(result.getCanonicalFile(),
        equalTo(new File(testdatadir, "from.flac/Music/dummy1.txt").getCanonicalFile()));
  }

  @Test
  public void testSyncFileList_FileList_NotAFile() {
    File flacBaseDir = new File(testdatadir, "Music");
    File mp3BaseDir = new File(testdatadir, "from.flac");
    File fileList = new File("testresources");

    boolean result = main.syncFileList(null, flacBaseDir, mp3BaseDir, fileList, false);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(flacToMp3.countAll), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(flacToMp3.countNormal), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testSyncFileList_FileList_Empty() {
    File flacBaseDir = new File(testdatadir, "Music");
    File mp3BaseDir = new File(testdatadir, "from.flac");
    File fileList = new File("testresources/fileListEmpty.txt");

    boolean result = main.syncFileList(null, flacBaseDir, mp3BaseDir, fileList, false);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(flacToMp3.countAll), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(flacToMp3.countNormal), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testSyncFileList_FileList_NotBelowFlacBaseDir() {
    File flacBaseDir = new File(testdatadir, "Music");
    File mp3BaseDir = new File(testdatadir, "from.flac");
    File fileList = new File("testresources/fileListNotBelow.txt");

    boolean result = main.syncFileList(null, flacBaseDir, mp3BaseDir, fileList, false);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(flacToMp3.countAll), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(flacToMp3.countNormal), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testSyncFileList_FileList_NotBelowFlacBaseDir2() {
    File flacBaseDir = new File(testdatadir, "Music");
    File mp3BaseDir = new File(testdatadir, "from.flac");
    File fileList = new File("testresources/fileListNotBelow2.txt");

    boolean result = main.syncFileList(null, flacBaseDir, mp3BaseDir, fileList, false);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(flacToMp3.countAll), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(flacToMp3.countNormal), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testSyncFilteListt_FileList_Unreadable() {
    File flacBaseDir = new File(testdatadir, "Music");
    File mp3BaseDir = new File(testdatadir, "from.flac");
    File fileList = new File("testresources/fileListUnreadable.txt");

    boolean result = main.syncFileList(null, flacBaseDir, mp3BaseDir, fileList, false);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(flacToMp3.countAll), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(flacToMp3.countNormal), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testSyncFileList_FileList_ExceptionsInConversion() {
    flacToMp3.throwException = true;
    File flacBaseDir = new File(testdatadir, "Music");
    File mp3BaseDir = new File(testdatadir, "from.flac");
    File fileList = new File("testresources/fileListNormal.txt");

    boolean result = main.syncFileList(null, flacBaseDir, mp3BaseDir, fileList, true);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(flacToMp3.countAll), equalTo(Integer.valueOf(2)));
    assertThat(Integer.valueOf(flacToMp3.countNormal), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testSyncFileList_FileList_Normal() {
    File flacBaseDir = new File(testdatadir, "Music");
    File mp3BaseDir = new File(testdatadir, "from.flac");
    File fileList = new File("testresources/fileListNormal.txt");

    boolean result = main.syncFileList(null, flacBaseDir, mp3BaseDir, fileList, true);
    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(flacToMp3.countAll), equalTo(Integer.valueOf(2)));
    assertThat(Integer.valueOf(flacToMp3.countNormal), equalTo(Integer.valueOf(2)));
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
    boolean result = main.doMain();
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testDoMain_InvalidConfig() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args = {
        "-flac", "/some/dir/that/does/not/exist"
    };
    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain();
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
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
    boolean result = main.doMain();
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testDoMain_FileList() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args =
        {
            "-flac",
            new File(testdatadir, "Music").getPath(),
            "-mp3",
            new File(testdatadir, "from.flac").getPath(),
            "-f",
            "testresources/fileListNormal.txt"
        };

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain();
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testDoMain_Directory() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args = {
        "-flac", new File(testdatadir, "Music").getPath(), "-mp3", new File(testdatadir, "from.flac").getPath(), "."
    };

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain();
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testDoMain_Directory_NullArgs() {
    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain();
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test
  public void testDoMain_Directory_NoArgs() {
    Map<String, Object> parameters = new HashMap<>();
    String[] args = {};

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    boolean result = main.doMain();
    main.deactivate();

    assertThat(Boolean.valueOf(result), equalTo(Boolean.FALSE));
  }

  @Test(timeout = 1000)
  public void testRun_Normal() {
    syncer.returnValue = true;

    Map<String, Object> parameters = new HashMap<>();
    String[] args =
        {
            "-n",
            "-flac",
            new File(testdatadir, "Music").getPath(),
            "-mp3",
            new File(testdatadir, "from.flac").getPath(),
            "."
        };

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();

    main.activate(bc);
    main.run();
    main.deactivate();
  }

  @Test(timeout = 5000)
  public void testRun_NormalNoStop() throws InterruptedException {
    Map<String, Object> parameters = new HashMap<>();
    String[] args =
        {
            "-n",
            "-flac",
            new File(testdatadir, "Music").getPath(),
            "-mp3",
            new File(testdatadir, "from.flac").getPath(),
            "-f",
            "testresources/fileListNormal.txt"
        };

    parameters.put(Main.LAUNCHER_ARGUMENTS, args);
    main.setDone(new Object(), parameters);

    MyBundleContext bc = new MyBundleContext();
    bc.property = "true";

    main.activate(bc);
    Thread t = new Thread(main);
    t.start();

    while (flacToMp3.countAll == 0) {
      Thread.sleep(10);
    }
    Thread.sleep(10);

    t.interrupt();
    t.join();

    main.deactivate();
  }
}
