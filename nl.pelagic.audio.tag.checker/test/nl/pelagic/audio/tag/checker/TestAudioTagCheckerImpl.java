package nl.pelagic.audio.tag.checker;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.pelagic.audio.tag.checker.types.AudioTagCheckerConfiguration;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.ProblemReport;
import nl.pelagic.util.file.FilenameFilterWithRegex;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "nls", "javadoc"
})
public class TestAudioTagCheckerImpl {

  private MyTagConverter mytc = null;
  private AudioTagCheckerImpl atc = null;
  private MyTagChecker mytch = null;
  private MyAudioTagCheckerCallback callback = null;
  private AudioTagCheckerConfiguration config = null;
  private FilenameFilter filenameFilter = null;

  private String testresourcesFiletest = "testresources/filetest";
  private String testresourcesDirtest = "testresources/dirtest";

  @Before
  public void setUp() throws IOException {
    atc = new AudioTagCheckerImpl();
    mytc = new MyTagConverter();
    mytch = new MyTagChecker();

    atc.addTagConverter(mytc);
    atc.addTagChecker(mytch);

    callback = new MyAudioTagCheckerCallback();
    config = new AudioTagCheckerConfiguration();
    filenameFilter = new FilenameFilterWithRegex(null, config.getRegexPattern());

    config.setRecursiveScan(true);
  }

  @After
  public void tearDown() {
    filenameFilter = null;
    config = null;
    callback = null;

    atc.removeTagChecker(mytch);
    atc.removeTagConverter(mytc);

    mytch = null;
    mytc = null;
    atc = null;
  }

  @Test
  public void testProcess_NotRun() {
    atc.shutdownHook();
    atc.process(config, new File(testresourcesDirtest), filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_NonExistingFile() {
    File ed = new File(testresourcesDirtest, "dummynonexistingfile");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  /*
   * Files
   */

  @Test
  public void testProcess_UnsupportedExtension1() {
    File ed = new File(testresourcesFiletest, "laser");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(callback.unsupportedExtensions.contains(ed)), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_UnsupportedExtension2() {
    File ed = new File(testresourcesFiletest, "laser.mp333");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(callback.unsupportedExtensions.contains(ed)), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_Unreadable() {
    File ed = new File(testresourcesFiletest, "laser_zero_size.flac");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(callback.notReadables.contains(ed)), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_NotConvertedMp3() {
    File ed = new File(testresourcesFiletest, "laser.mp3");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(ed)), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_NotConvertedFlac() {
    File ed = new File(testresourcesFiletest, "laser.flac");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(ed)), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_CheckerDisabled() {
    Set<String> tcs = new HashSet<>();
    tcs.add(mytch.getClass().getSimpleName());
    config.setDisabledTagCheckers(tcs);
    mytc.retval = true;
    File ed = new File(testresourcesFiletest, "laser.flac");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(1)));
    assertThat(callback.checksPasseds.get(0).getBackingFile(), equalTo(ed));
  }

  @Test
  public void testProcess_CheckerNotEnabled() {
    Set<String> tcs = new HashSet<>();
    tcs.add("somedummycheckerclass");
    config.setEnabledTagCheckers(tcs);
    mytc.retval = true;
    File ed = new File(testresourcesFiletest, "laser.flac");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(1)));
    assertThat(callback.checksPasseds.get(0).getBackingFile(), equalTo(ed));
  }

  @Test
  public void testProcess_CheckFailed() {
    mytc.retval = true;
    mytch.key = GenericTagFieldName.FILE;
    mytch.value = new LinkedList<>();
    List<Integer> positionMarkers = new LinkedList<>();
    ProblemReport pr = new ProblemReport("message", "expectedValue", "actualValue", positionMarkers);
    mytch.value.add(pr);

    File ed = new File(testresourcesFiletest, "laser.flac");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(1)));
    GenericTag gt = callback.checksFaileds.get(0);
    assertThat(gt.getBackingFile(), equalTo(ed));
    Map<GenericTagFieldName, List<ProblemReport>> prs = gt.getReports();
    assertThat(prs, notNullValue());
    assertThat(Integer.valueOf(prs.size()), equalTo(Integer.valueOf(1)));
    List<ProblemReport> pr1 = prs.get(GenericTagFieldName.FILE);
    assertThat(pr1, notNullValue());
    assertThat(Integer.valueOf(pr1.size()), equalTo(Integer.valueOf(1)));
    ProblemReport pr11 = pr1.get(0);
    assertThat(pr11, notNullValue());
    assertThat(pr11.getActualValue(), equalTo(pr.getActualValue()));
    assertThat(pr11.getExpectedValue(), equalTo(pr.getExpectedValue()));
    assertThat(pr11.getMessage(), equalTo(pr.getMessage()));
    assertThat(pr11.getPositionMarkers(), equalTo(pr.getPositionMarkers()));

    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_CheckPassed() {
    mytc.retval = true;

    File ed = new File(testresourcesFiletest, "laser.flac");

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(1)));
    assertThat(callback.checksPasseds.get(0).getBackingFile(), equalTo(ed));
  }

  /*
   * Dirs
   */

  @Test
  public void testProcess_NotRecursive1() {
    config.setRecursiveScan(false);
    atc.process(config, new File(testresourcesDirtest), filenameFilter, false, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_NotRecursive2() {
    config.setRecursiveScan(true);
    atc.process(config, new File(testresourcesDirtest), filenameFilter, false, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_NotRecursive3() {
    config.setRecursiveScan(false);
    atc.process(config, new File(testresourcesDirtest), filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest, "laser.flac"))),
        equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_Recursive() {
    config.setRecursiveScan(true);
    atc.process(config, new File(testresourcesDirtest), filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(3)));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest, "laser.flac"))),
        equalTo(Boolean.TRUE));
    assertThat(
        Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest, "subdir/laser.flac"))),
        equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest,
        "subdir/subsubdir/laser.flac"))), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_Recursive_Aborted() {
    callback.atc = atc;
    callback.shutdownAfterNumberOfCalls = 2;
    config.setRecursiveScan(true);
    atc.process(config, new File(testresourcesDirtest), filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(2)));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest, "laser.flac"))),
        equalTo(Boolean.TRUE));
    assertThat(
        Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest, "subdir/laser.flac"))),
        equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testProcess_EmptyDirectory() {
    File ed = new File(testresourcesDirtest, "dummyemptydirectory");
    ed.mkdirs();
    ed.deleteOnExit();

    atc.process(config, ed, filenameFilter, true, callback);

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testCheck_Nulls() throws IOException {
    boolean r = atc.check(null, null);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    r = atc.check(config, null);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testCheck_ScanPath_Problems() throws IOException {
    boolean r = atc.check(config, callback);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    config.setCheckPath(new File(testresourcesDirtest, "somepaththatdoesntexist"));
    r = atc.check(config, callback);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testCheck_Recursive1() throws IOException {
    config.setRecursiveScan(true);
    config.setCheckPath(new File(testresourcesDirtest));

    boolean r = atc.check(config, callback);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(3)));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest, "laser.flac"))),
        equalTo(Boolean.TRUE));
    assertThat(
        Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest, "subdir/laser.flac"))),
        equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest,
        "subdir/subsubdir/laser.flac"))), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testCheck_Recursive2() throws IOException {
    config.setRecursiveScan(true);
    config.setRegexInAllDirs(true);
    config.setCheckPath(new File(testresourcesDirtest));

    boolean r = atc.check(config, callback);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    assertThat(Integer.valueOf(callback.unsupportedExtensions.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.notReadables.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.noTags.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.tagNotConverteds.size()), equalTo(Integer.valueOf(3)));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest, "laser.flac"))),
        equalTo(Boolean.TRUE));
    assertThat(
        Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest, "subdir/laser.flac"))),
        equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(callback.tagNotConverteds.contains(new File(testresourcesDirtest,
        "subdir/subsubdir/laser.flac"))), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(callback.checksFaileds.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(callback.checksPasseds.size()), equalTo(Integer.valueOf(0)));
  }

}
