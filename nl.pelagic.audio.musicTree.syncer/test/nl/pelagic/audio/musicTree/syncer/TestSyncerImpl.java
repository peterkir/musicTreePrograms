package nl.pelagic.audio.musicTree.syncer;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import nl.pelagic.audio.conversion.flac2mp3.testhelpers.MyFlacToMp3;
import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;
import nl.pelagic.shell.script.listener.testhelpers.MyShellScriptListener;
import nl.pelagic.util.file.ExtensionUtils;
import nl.pelagic.util.file.FileUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings({
    "javadoc", "nls"
})
public class TestSyncerImpl {
  private static Logger logger = Logger.getLogger(SyncerImpl.class.getName());
  private static Logger jlogger = Logger.getLogger("org.jaudiotagger");

  private MyFlacToMp3 myFlacToMp3 = null;
  private MyShellScriptListener myShellScriptListener = null;
  private SyncerImpl syncerImpl = null;

  private static final File testDir = new File("testresources/testdata");
  private static final File testDirFileListSplit = new File(testDir, "FileListSplit");
  private static final File tmpTestBaseDir = new File("testresources/tmpTestBaseDir");

  @BeforeClass
  public static void setUpBeforeClass() {
    logger.setLevel(Level.OFF);
    jlogger.setLevel(Level.OFF);
  }

  private File tmpDir = new File(tmpTestBaseDir, "tmpDir");
  private File tmpDir2 = new File(tmpTestBaseDir, "tmpDir2");

  @Before
  public void setUp() {
    myFlacToMp3 = new MyFlacToMp3();
    myShellScriptListener = new MyShellScriptListener();
    syncerImpl = new SyncerImpl();
    syncerImpl.setFlacToMp3(myFlacToMp3);
    syncerImpl.setShellScriptListener(myShellScriptListener);

    try {
      tmpDir.mkdir();
      tmpDir2.mkdir();
      FileUtils.copy(testDirFileListSplit, tmpDir);
    }
    catch (IOException e) {
      throw new ExceptionInInitializerError(e);
    }
  }

  @After
  public void tearDown() {
    FileUtils.delete(tmpDir2);
    FileUtils.delete(tmpDir);

    syncerImpl.unsetShellScriptListener(myShellScriptListener);
    syncerImpl.setFlacToMp3(myFlacToMp3);
    syncerImpl = null;
    myShellScriptListener = null;
    myFlacToMp3 = null;
  }

  @Test
  public void testRemoveFromMp3Dir_Mp3Dir_DoesNotExist() {
    syncerImpl.removeFromMp3Dir(new File("somedummydirectorythatdoesnotexist"), null, null, null, false);
  }

  @Test
  public void testRemoveFromMp3Dir_Mp3Entries_Null() {

    syncerImpl.removeFromMp3Dir(tmpDir, null, null, null, false);

    String[] src = testDirFileListSplit.list();
    Arrays.sort(src);
    String[] dst = tmpDir.list();
    Arrays.sort(dst);
    assertThat(dst, equalTo(src));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testRemoveFromMp3Dir_Mp3Entries_Mismatch() {
    List<String> mp3Entries = Arrays.asList(tmpDir.list());
    List<String> mp3EntriesFullName = new LinkedList<>();

    syncerImpl.removeFromMp3Dir(tmpDir, mp3Entries, mp3EntriesFullName, null, false);
  }

  @Test
  public void testRemoveFromMp3Dir_Mp3Entries_Empty() {
    List<String> mp3Entries = new LinkedList<>();
    List<String> mp3EntriesFullName = new LinkedList<>();

    syncerImpl.removeFromMp3Dir(tmpDir, mp3Entries, mp3EntriesFullName, null, false);

    String[] src = testDirFileListSplit.list();
    Arrays.sort(src);
    String[] dst = tmpDir.list();
    Arrays.sort(dst);
    assertThat(dst, equalTo(src));
  }

  @Test
  public void testRemoveFromMp3Dir_All() {
    List<String> mp3Entries = Arrays.asList(tmpDir.list());

    syncerImpl.removeFromMp3Dir(tmpDir, mp3Entries, null, null, false);

    String[] res = tmpDir.list();
    assertThat(Integer.valueOf(res.length), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testRemoveFromMp3Dir_Normal() {
    List<String> flacEntries = new LinkedList<>();
    List<String> mp3Entries = new LinkedList<>();
    List<String> mp3EntriesFullName = new LinkedList<>();
    mp3EntriesFullName.addAll(Arrays.asList(tmpDir.list()));
    mp3EntriesFullName.add("zzz.doesnotexist");

    flacEntries.add("dummy1");

    for (String mp3Entry : mp3EntriesFullName) {
      mp3Entries.add(ExtensionUtils.split(mp3Entry, true)[0]);
    }

    syncerImpl.removeFromMp3Dir(tmpDir, mp3Entries, mp3EntriesFullName, flacEntries, false);

    mp3EntriesFullName.remove("dummydir1");
    mp3EntriesFullName.remove("dummydir2");
    mp3EntriesFullName.remove("cover.jpg");
    mp3EntriesFullName.remove("dummy2.flac");
    mp3EntriesFullName.remove("dummy2.mp3");
    mp3EntriesFullName.remove("dummy2.other");
    mp3EntriesFullName.remove("dummy2.txt");
    mp3EntriesFullName.remove("zzz.doesnotexist");
    Collections.sort(mp3EntriesFullName);

    List<String> actual = Arrays.asList(tmpDir.list());
    Collections.sort(actual);

    assertThat(actual, equalTo(mp3EntriesFullName));
  }

  @Test
  public void testRemoveFromMp3Dir_Normal_Simulated() {
    List<String> flacEntries = new LinkedList<>();
    List<String> mp3Entries = new LinkedList<>();
    List<String> mp3EntriesFullName = new LinkedList<>();
    mp3EntriesFullName.addAll(Arrays.asList(tmpDir.list()));

    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    for (String mp3Entry : mp3EntriesFullName) {
      mp3Entries.add(ExtensionUtils.split(mp3Entry, true)[0]);
    }

    syncerImpl.removeFromMp3Dir(tmpDir, mp3Entries, mp3EntriesFullName, flacEntries, true);

    Collections.sort(mp3EntriesFullName);

    List<String> actual = Arrays.asList(tmpDir.list());
    Collections.sort(actual);

    assertThat(actual, equalTo(mp3EntriesFullName));
  }

  @Test
  public void testRemoveFromMp3Dir_Normal_Simulated_NoListener() {
    syncerImpl.unsetShellScriptListener(myShellScriptListener);

    List<String> flacEntries = new LinkedList<>();
    List<String> mp3Entries = new LinkedList<>();
    List<String> mp3EntriesFullName = new LinkedList<>();
    mp3EntriesFullName.addAll(Arrays.asList(tmpDir.list()));

    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    for (String mp3Entry : mp3EntriesFullName) {
      mp3Entries.add(ExtensionUtils.split(mp3Entry, true)[0]);
    }

    syncerImpl.removeFromMp3Dir(tmpDir, mp3Entries, mp3EntriesFullName, flacEntries, true);

    Collections.sort(mp3EntriesFullName);

    List<String> actual = Arrays.asList(tmpDir.list());
    Collections.sort(actual);

    assertThat(actual, equalTo(mp3EntriesFullName));
  }

  @Test
  public void testCopyCovers_Covers_Null() {
    File dstcover = new File(tmpDir, "cover.jpg");
    FileUtils.delete(dstcover);
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));

    syncerImpl.copyCovers(testDirFileListSplit, null, tmpDir, false);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testCopyCovers_Covers_Empty() {
    List<String> covers = new LinkedList<>();

    File dstcover = new File(tmpDir, "cover.jpg");
    FileUtils.delete(dstcover);
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));

    syncerImpl.copyCovers(testDirFileListSplit, covers, tmpDir, false);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testCopyCovers_FlacDir_DoesNotExist() {
    List<String> covers = new LinkedList<>();
    covers.add("cover.jpg");

    File dstcover = new File(tmpDir, "cover.jpg");
    FileUtils.delete(dstcover);
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));

    syncerImpl.copyCovers(new File(testDirFileListSplit, "doesnotexist"), covers, tmpDir, false);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testCopyCovers_Normal() {
    List<String> covers = new LinkedList<>();
    covers.add("cover.jpg");

    File srccover = new File(testDirFileListSplit, "cover.jpg");
    long srccoverts = srccover.lastModified();
    File dstcover = new File(tmpDir, "cover.jpg");
    FileUtils.delete(dstcover);
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));

    syncerImpl.copyCovers(testDirFileListSplit, covers, tmpDir, false);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.TRUE));
    long dstcoverts = dstcover.lastModified();
    assertThat(Long.valueOf(dstcoverts), equalTo(Long.valueOf(srccoverts)));
  }

  @Test
  public void testCopyCovers_Normal_NoListener() {
    List<String> covers = new LinkedList<>();
    covers.add("cover.jpg");

    File srccover = new File(testDirFileListSplit, "cover.jpg");
    long srccoverts = srccover.lastModified();
    File dstcover = new File(tmpDir, "cover.jpg");
    FileUtils.delete(dstcover);
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));

    syncerImpl.unsetShellScriptListener(myShellScriptListener);
    syncerImpl.copyCovers(testDirFileListSplit, covers, tmpDir, false);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.TRUE));
    long dstcoverts = dstcover.lastModified();
    assertThat(Long.valueOf(dstcoverts), equalTo(Long.valueOf(srccoverts)));
  }

  @Test
  public void testCopyCovers_Normal_Simulated() {
    List<String> covers = new LinkedList<>();
    covers.add("cover.jpg");

    File dstcover = new File(tmpDir, "cover.jpg");
    FileUtils.delete(dstcover);
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));

    syncerImpl.copyCovers(testDirFileListSplit, covers, tmpDir, true);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testCopyCovers_Normal_DstDirNotExists() {
    List<String> covers = new LinkedList<>();
    covers.add("cover.jpg");

    File srccover = new File(testDirFileListSplit, "cover.jpg");
    long srccoverts = srccover.lastModified();
    File tmpDir1 = new File(tmpDir, "extrasubdir");
    File dstcover = new File(tmpDir1, "cover.jpg");
    FileUtils.delete(dstcover);
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));

    syncerImpl.copyCovers(testDirFileListSplit, covers, tmpDir1, false);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.TRUE));
    long dstcoverts = dstcover.lastModified();
    assertThat(Long.valueOf(dstcoverts), equalTo(Long.valueOf(srccoverts)));
  }

  @Test
  public void testCopyCovers_Normal_DstDirNotExists_NoListener() {
    List<String> covers = new LinkedList<>();
    covers.add("cover.jpg");

    File srccover = new File(testDirFileListSplit, "cover.jpg");
    long srccoverts = srccover.lastModified();
    File tmpDir1 = new File(tmpDir, "extrasubdir");
    File dstcover = new File(tmpDir1, "cover.jpg");
    FileUtils.delete(dstcover);
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));

    syncerImpl.unsetShellScriptListener(myShellScriptListener);
    syncerImpl.copyCovers(testDirFileListSplit, covers, tmpDir1, false);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.TRUE));
    long dstcoverts = dstcover.lastModified();
    assertThat(Long.valueOf(dstcoverts), equalTo(Long.valueOf(srccoverts)));
  }

  @Test
  public void testCopyCovers_Normal_DstDirNotExists_Simulated() {
    List<String> covers = new LinkedList<>();
    covers.add("cover.jpg");

    File tmpDir1 = new File(tmpDir, "extrasubdir");
    File dstcover = new File(tmpDir1, "cover.jpg");
    FileUtils.delete(dstcover);
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));

    syncerImpl.copyCovers(testDirFileListSplit, covers, tmpDir1, true);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testCopyCovers_Src_Newer() {
    List<String> covers = new LinkedList<>();
    covers.add("cover.jpg");

    File srccover = new File(testDirFileListSplit, "cover.jpg");
    long srccoverts = srccover.lastModified();
    File dstcover = new File(tmpDir, "cover.jpg");
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.TRUE));
    dstcover.setLastModified(srccoverts - 5000);

    syncerImpl.copyCovers(testDirFileListSplit, covers, tmpDir, false);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.TRUE));
    long dstcoverts = dstcover.lastModified();
    assertThat(Long.valueOf(dstcoverts), equalTo(Long.valueOf(srccoverts)));
  }

  @Test
  public void testCopyCovers_Src_Older() {
    List<String> covers = new LinkedList<>();
    covers.add("cover.jpg");

    File srccover = new File(testDirFileListSplit, "cover.jpg");
    long srccoverts = srccover.lastModified();
    File dstcover = new File(tmpDir, "cover.jpg");
    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.TRUE));
    dstcover.setLastModified(srccoverts + 5000);

    syncerImpl.copyCovers(testDirFileListSplit, covers, tmpDir, false);

    assertThat(Boolean.valueOf(dstcover.exists()), equalTo(Boolean.TRUE));
    long dstcoverts = dstcover.lastModified();
    assertThat(Long.valueOf(dstcoverts), equalTo(Long.valueOf(srccoverts + 5000)));
  }

  @Test
  public void testConvertFlacFiles_Nulls_And_Others() {
    File flacDir = null;
    List<String> flacEntries = null;
    List<String> flacEntriesFullName = null;
    File mp3Dir = null;
    List<String> mp3Entries = null;
    boolean simulate = true;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    /* tests flacDir is not a directory */
    flacDir = new File(testDir, "dummy1.txt");
    r = syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    /* tests flacEntries == null */
    flacDir = testDir;
    r = syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    /* tests flacEntries is empty */
    flacEntries = new LinkedList<>();
    r = syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    String[] flacDirFilenames = flacDir.list();

    /* tests flacEntriesFullName == null */
    for (String flacDirFilename : flacDirFilenames) {
      flacEntries.add(ExtensionUtils.split(flacDirFilename, true)[0]);
    }
    r = syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    /* tests flacEntriesFullName is empty */
    flacEntriesFullName = new LinkedList<>();
    r = syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    /* tests mp3Dir == null */
    for (String flacDirFilename : flacDirFilenames) {
      flacEntriesFullName.add(flacDirFilename);
    }
    r = syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testConvertFlacFiles_Normal_FlacFilesDontExist() {
    File flacDir = testDir;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpTestBaseDir;
    List<String> mp3Entries = null;

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testConvertFlacFiles_Normal_Mp3Entries_Null() {
    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = null;

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.TRUE));
  }

  @Test
  public void testConvertFlacFiles_Normal() {
    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = new LinkedList<>();

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.TRUE));
  }

  @Test
  public void testConvertFlacFiles_FlacEntries_NotExistAndDirectory() {
    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummydir1");
    flacEntries.add("dummy222");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummydir1");
    flacEntriesFullName.add("dummy222.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = new LinkedList<>();

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testConvertFlacFiles_Normal_NoConversionNeeded() throws FileAlreadyExistsException,
      FileNotFoundException, IOException {
    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = new LinkedList<>();
    mp3Entries.add("dummy1");
    mp3Entries.add("dummy2");

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    FileUtils.copy(new File(flacDir, "dummy1.flac"), mp3dummy1);
    FileUtils.copy(new File(flacDir, "dummy2.flac"), mp3dummy2);
    long mp3dummy1_ts = mp3dummy1.lastModified();
    long mp3dummy2_ts = mp3dummy2.lastModified();

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.TRUE));
    assertThat(Long.valueOf(mp3dummy1.lastModified()), equalTo(Long.valueOf(mp3dummy1_ts)));
    assertThat(Long.valueOf(mp3dummy2.lastModified()), equalTo(Long.valueOf(mp3dummy2_ts)));
  }

  @Test
  public void testConvertFlacFiles_Normal_ConversionNeededBecauseOfTimestamps() throws FileAlreadyExistsException,
      FileNotFoundException, IOException {
    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = new LinkedList<>();
    mp3Entries.add("dummy1");
    mp3Entries.add("dummy2");

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    FileUtils.copy(new File(flacDir, "dummy1.flac"), mp3dummy1);
    FileUtils.copy(new File(flacDir, "dummy2.flac"), mp3dummy2);
    mp3dummy1.setLastModified(new File(flacDir, "dummy1.flac").lastModified() - 5000);
    mp3dummy2.setLastModified(new File(flacDir, "dummy2.flac").lastModified() - 5000);
    long mp3dummy1_ts = mp3dummy1.lastModified();
    long mp3dummy2_ts = mp3dummy2.lastModified();

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.TRUE));
    assertThat(Long.valueOf(mp3dummy1.lastModified()), not(Long.valueOf(mp3dummy1_ts)));
    assertThat(Long.valueOf(mp3dummy2.lastModified()), not(Long.valueOf(mp3dummy2_ts)));
  }

  @Test
  public void testConvertFlacFiles_Normal_NoConversionNeeded_Dummy2NotInMp3Dir() throws FileAlreadyExistsException,
      FileNotFoundException, IOException {
    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = new LinkedList<>();
    mp3Entries.add("dummy1");

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    FileUtils.copy(new File(flacDir, "dummy1.flac"), mp3dummy1);
    long mp3dummy1_ts = mp3dummy1.lastModified();

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.TRUE));
    assertThat(Long.valueOf(mp3dummy1.lastModified()), equalTo(Long.valueOf(mp3dummy1_ts)));
  }

  @Test
  public void testConvertFlacFiles_Normal_NoConversionNeeded_Dummy2NotInMp3Entries() throws FileAlreadyExistsException,
      FileNotFoundException, IOException {
    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = new LinkedList<>();
    mp3Entries.add("dummy1");

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    FileUtils.copy(new File(flacDir, "dummy1.flac"), mp3dummy1);
    FileUtils.copy(new File(flacDir, "dummy2.flac"), mp3dummy2);
    long mp3dummy1_ts = mp3dummy1.lastModified();

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.TRUE));
    assertThat(Long.valueOf(mp3dummy1.lastModified()), equalTo(Long.valueOf(mp3dummy1_ts)));
  }

  @Test
  public void testConvertFlacFiles_Normal_NoConversionNeeded_Mp3Entries_Null() throws FileAlreadyExistsException,
      FileNotFoundException, IOException {
    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = null;

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    FileUtils.copy(new File(flacDir, "dummy1.flac"), mp3dummy1);
    FileUtils.copy(new File(flacDir, "dummy2.flac"), mp3dummy2);
    long mp3dummy1_ts = mp3dummy1.lastModified();

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.TRUE));
    assertThat(Long.valueOf(mp3dummy1.lastModified()), equalTo(Long.valueOf(mp3dummy1_ts)));
  }

  @Test
  public void testConvertFlacFiles_ConversionFail() {
    myFlacToMp3.retval = false;

    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = new LinkedList<>();

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testConvertFlacFiles_ConversionException() {
    myFlacToMp3.throwException = true;

    File flacDir = testDirFileListSplit;

    List<String> flacEntries = new LinkedList<>();
    flacEntries.add("dummy1");
    flacEntries.add("dummy2");

    List<String> flacEntriesFullName = new LinkedList<>();
    flacEntriesFullName.add("dummy1.flac");
    flacEntriesFullName.add("dummy2.flac");

    File mp3Dir = tmpDir2;
    List<String> mp3Entries = new LinkedList<>();

    boolean simulate = false;

    /* tests flacDir == null */
    boolean r =
        syncerImpl.convertFlacFiles(null, flacDir, flacEntries, flacEntriesFullName, mp3Dir, mp3Entries, simulate);

    File mp3dummy1 = new File(mp3Dir, "dummy1.mp3");
    File mp3dummy2 = new File(mp3Dir, "dummy2.mp3");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testSyncFlac2Mp3_Dirs_NullOrNotADirectory() {
    File flacDir = null;
    File mp3Dir = tmpDir2;
    Set<String> extensionsList = new HashSet<>();
    extensionsList.add(MusicTreeConstants.FLACEXTENSION);
    Set<String> fileNamesList = new HashSet<>();
    fileNamesList.add(MusicTreeConstants.COVER);

    boolean r = syncerImpl.syncFlac2Mp3(null, flacDir, mp3Dir, extensionsList, fileNamesList, false);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    flacDir = new File(testDirFileListSplit, "dummy1.txt");
    r = syncerImpl.syncFlac2Mp3(null, flacDir, mp3Dir, extensionsList, fileNamesList, false);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    flacDir = testDir;
    mp3Dir = null;
    r = syncerImpl.syncFlac2Mp3(null, flacDir, mp3Dir, extensionsList, fileNamesList, false);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testSyncFlac2Mp3_ConversionFailure() {
    myFlacToMp3.retval = false;

    File flacDir = testDir;
    File mp3Dir = tmpDir2;
    Set<String> extensionsList = new HashSet<>();
    extensionsList.add(MusicTreeConstants.FLACEXTENSION);
    Set<String> fileNamesList = new HashSet<>();
    fileNamesList.add(MusicTreeConstants.COVER);

    boolean r = syncerImpl.syncFlac2Mp3(null, flacDir, mp3Dir, extensionsList, fileNamesList, false);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    File subdir = new File(mp3Dir, "FileListSplit");
    assertThat(Boolean.valueOf(subdir.isDirectory()), equalTo(Boolean.TRUE));

    File mp3cover = new File(subdir, "cover.jpg");
    File mp3dummy1 = new File(subdir, "dummy1.mp3");
    File mp3dummy2 = new File(subdir, "dummy2.mp3");
    assertThat(Boolean.valueOf(mp3cover.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.FALSE));

  }

  @Test
  public void testSyncFlac2Mp3_Normal() {
    File flacDir = testDir;
    File mp3Dir = tmpDir2;
    Set<String> extensionsList = new HashSet<>();
    extensionsList.add(MusicTreeConstants.FLACEXTENSION);
    Set<String> fileNamesList = new HashSet<>();
    fileNamesList.add(MusicTreeConstants.COVER);

    boolean r = syncerImpl.syncFlac2Mp3(null, flacDir, mp3Dir, extensionsList, fileNamesList, false);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    File subdir = new File(mp3Dir, "FileListSplit");
    assertThat(Boolean.valueOf(subdir.isDirectory()), equalTo(Boolean.TRUE));

    File mp3cover = new File(subdir, "cover.jpg");
    File mp3dummy1 = new File(subdir, "dummy1.mp3");
    File mp3dummy2 = new File(subdir, "dummy2.mp3");
    assertThat(Boolean.valueOf(mp3cover.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.TRUE));
  }

  @Test
  public void testSyncFlac2Mp3_Normal_NoListener() {
    syncerImpl.unsetShellScriptListener(myShellScriptListener);

    File flacDir = testDir;
    File mp3Dir = tmpDir2;
    Set<String> extensionsList = new HashSet<>();
    extensionsList.add(MusicTreeConstants.FLACEXTENSION);
    Set<String> fileNamesList = new HashSet<>();
    fileNamesList.add(MusicTreeConstants.COVER);

    boolean r = syncerImpl.syncFlac2Mp3(null, flacDir, mp3Dir, extensionsList, fileNamesList, false);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    File subdir = new File(mp3Dir, "FileListSplit");
    assertThat(Boolean.valueOf(subdir.isDirectory()), equalTo(Boolean.TRUE));

    File mp3cover = new File(subdir, "cover.jpg");
    File mp3dummy1 = new File(subdir, "dummy1.mp3");
    File mp3dummy2 = new File(subdir, "dummy2.mp3");
    assertThat(Boolean.valueOf(mp3cover.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy1.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(mp3dummy2.exists()), equalTo(Boolean.TRUE));
  }
}
