package nl.pelagic.audio.musicTree.syncer;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "nls", "static-method"
})
public class TestFlacTreeFilenameFilter {
  private static final File testDir = new File("testresources/testdata");
  private static final File testDirFileListSplit = new File(testDir, "FileListSplit");

  class RunTestDirAcceptResult {
    public Set<String> accepted = new TreeSet<>();
    public Set<String> rejected = new TreeSet<>();
  }

  private RunTestDirAcceptResult runTestDirAccept(FlacTreeFilenameFilter filter) {
    RunTestDirAcceptResult runTestDirAcceptResult = new RunTestDirAcceptResult();

    String[] files = testDirFileListSplit.list();
    if ((files == null) || (files.length == 0)) {
      return runTestDirAcceptResult;
    }

    for (String file : files) {
      boolean r = filter.accept(testDirFileListSplit, file);
      if (!r) {
        runTestDirAcceptResult.rejected.add(file);
      } else {
        runTestDirAcceptResult.accepted.add(file);
      }
    }

    return runTestDirAcceptResult;
  }

  @Test(expected = ExceptionInInitializerError.class)
  public void testFlacTreeFilenameFilter_Extension_Empty() {
    Set<String> extensions = new TreeSet<>();
    extensions.add("");

    @SuppressWarnings("unused")
    FlacTreeFilenameFilter filter = new FlacTreeFilenameFilter(extensions, null, true);
  }

  @Test(expected = ExceptionInInitializerError.class)
  public void testFlacTreeFilenameFilter_Extension_NoDot() {
    Set<String> extensions = new TreeSet<>();
    extensions.add("flac");

    @SuppressWarnings("unused")
    FlacTreeFilenameFilter filter = new FlacTreeFilenameFilter(extensions, null, true);
  }

  @Test(expected = ExceptionInInitializerError.class)
  public void testFlacTreeFilenameFilter_Filename_Empty() {
    Set<String> filenames = new TreeSet<>();
    filenames.add("");

    @SuppressWarnings("unused")
    FlacTreeFilenameFilter filter = new FlacTreeFilenameFilter(null, filenames, true);
  }

  @Test
  public void testAccept_Nulls() {
    boolean r;
    FlacTreeFilenameFilter filter = new FlacTreeFilenameFilter(null, null, false);

    r = filter.accept(null, null);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    r = filter.accept(testDirFileListSplit, null);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    r = filter.accept(null, "dummy1.txt");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testAccept_Empty_Lists() {
    boolean r;
    Set<String> extensions = new TreeSet<>();
    Set<String> filenames = new TreeSet<>();
    FlacTreeFilenameFilter filter = new FlacTreeFilenameFilter(extensions, filenames, false);

    r = filter.accept(null, null);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    r = filter.accept(testDirFileListSplit, null);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    r = filter.accept(null, "dummy1.txt");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testAccept_Directories() {
    boolean r;
    FlacTreeFilenameFilter filter = new FlacTreeFilenameFilter(null, null, false);

    r = filter.accept(testDirFileListSplit, "dummydir1");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    filter = new FlacTreeFilenameFilter(null, null, true);

    r = filter.accept(testDirFileListSplit, "dummydir1");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAccept_Extensions() {
    boolean r;
    Set<String> extensions = new TreeSet<>();
    extensions.add(".FLAC");
    extensions.add(".mp3");
    FlacTreeFilenameFilter filter = new FlacTreeFilenameFilter(extensions, null, true);

    r = filter.accept(testDirFileListSplit, "dummy1.flac");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    r = filter.accept(testDirFileListSplit, "dummy1.mp3");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    r = filter.accept(testDirFileListSplit, "dummy1.txt");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    r = filter.accept(testDirFileListSplit, "dummy1");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testAccept_FileNames() {
    boolean r;
    Set<String> filenames = new TreeSet<>();
    filenames.add("cover.jpg");
    filenames.add("dummy1.txt");
    FlacTreeFilenameFilter filter = new FlacTreeFilenameFilter(null, filenames, true);

    r = filter.accept(testDirFileListSplit, "cover.jpg");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    r = filter.accept(testDirFileListSplit, "dummy1.flac");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    r = filter.accept(testDirFileListSplit, "dummy1.txt");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    r = filter.accept(testDirFileListSplit, "dummy1");
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testAccept_Normal() {
    Set<String> extensions = new TreeSet<>();
    extensions.add(MusicTreeConstants.FLACEXTENSION);
    Set<String> filenames = new TreeSet<>();
    filenames.add(MusicTreeConstants.COVER);
    FlacTreeFilenameFilter filter = new FlacTreeFilenameFilter(extensions, filenames, true);

    RunTestDirAcceptResult result = runTestDirAccept(filter);

    Set<String> accepted = new TreeSet<>();
    accepted.add("dummydir1");
    accepted.add("dummydir2");
    accepted.add("cover.jpg");
    accepted.add("dummy1.flac");
    accepted.add("dummy2.flac");
    Set<String> rejected = new TreeSet<>();
    rejected.add("dummy1.mp3");
    rejected.add("dummy1.other");
    rejected.add("dummy1.txt");
    rejected.add("dummy2.mp3");
    rejected.add("dummy2.other");
    rejected.add("dummy2.txt");

    assertThat(result.accepted, equalTo(accepted));
    assertThat(result.rejected, equalTo(rejected));
  }
}
