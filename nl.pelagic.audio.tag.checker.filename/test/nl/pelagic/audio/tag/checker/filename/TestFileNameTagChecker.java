package nl.pelagic.audio.tag.checker.filename;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.pelagic.audio.tag.checker.filename.i18n.Messages;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.ProblemReport;
import nl.pelagic.audio.tag.checker.types.TypeUtilsForTests;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings({
    "nls", "static-method", "javadoc"
})
public class TestFileNameTagChecker {

  private static FileNameTagChecker checker;

  @BeforeClass
  public static void setUpBeforeClass() {
    checker = new FileNameTagChecker();
  }

  @AfterClass
  public static void tearDownAfterClass() {
    checker = null;
  }

  @Test
  public void testCheckArtistDirectory_None() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, null, "Track Title", null);
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkArtistDirectory(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.0")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckArtistDirectory_WrongTrackArtist() {
    String ta = "Track Artist";
    String wta = "Wrong Track Artist";
    GenericTag tag = TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, ta, "Track Title", null);
    tag.setBackingFile(new File("/some/path/root/" + wta + "/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkArtistDirectory(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.1")));
    assertThat(pr.getExpectedValue(), equalTo(ta));
    assertThat(pr.getActualValue(), equalTo(wta));
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckArtistDirectory_WrongAlbumArtist() {
    String aa = "Album Artist";
    String waa = "Wrong Album Artist";
    GenericTag tag = TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, null, "Track Title", aa);
    tag.setBackingFile(new File("/some/path/root/" + waa + "/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkArtistDirectory(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.1")));
    assertThat(pr.getExpectedValue(), equalTo(aa));
    assertThat(pr.getActualValue(), equalTo(waa));
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckArtistDirectory_WrongAlbumArtistWithTrackArtist() {
    String aa = "Album Artist";
    String waa = "Wrong Album Artist";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, "Track Artist", "Track Title", aa);
    tag.setBackingFile(new File("/some/path/root/" + waa + "/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkArtistDirectory(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.1")));
    assertThat(pr.getExpectedValue(), equalTo(aa));
    assertThat(pr.getActualValue(), equalTo(waa));
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckArtistDirectory_OkAlbumArtistWithTrackArtist() {
    String aa = "Album Artist";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, "Track Artist", "Track Title", aa);
    tag.setBackingFile(new File("/some/path/root/" + aa + "/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkArtistDirectory(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testCheckAlbumDirectory_None() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", null, "1", "2013", null, "Track Artist", "Track Title", "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkAlbumDirectory(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.2")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckAlbumDirectory_WrongAlbumTitle() {
    String at = "Album Title";
    String wat = "Wrong Album Title";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", at, "1", "2013", null, "Track Artist", "Track Title", "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Track Artist/" + wat + "/01 - Track Title.flac"));

    FileNameTagChecker.checkAlbumDirectory(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.3")));
    assertThat(pr.getExpectedValue(), equalTo(at));
    assertThat(pr.getActualValue(), equalTo(wat));
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckAlbumDirectory_OkAlbumArtistWithTrackArtist() {
    String at = "Album Title";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", at, "1", "2013", null, "Track Artist", "Track Title", "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/" + at + "/01 - Track Title.flac"));

    FileNameTagChecker.checkAlbumDirectory(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testDetermineNumberOfAlbumDigits() {
    int[] zeroes = {
        Integer.MIN_VALUE, -1, 0, 1
    };
    for (int zero : zeroes) {
      assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(zero)), equalTo(Integer.valueOf(0)));
    }

    int[] twos = {
        2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 98, 99
    };
    for (int two : twos) {
      assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(two)), equalTo(Integer.valueOf(2)));
    }

    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(100)), equalTo(Integer.valueOf(3)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(101)), equalTo(Integer.valueOf(3)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(998)), equalTo(Integer.valueOf(3)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(999)), equalTo(Integer.valueOf(3)));

    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(1000)), equalTo(Integer.valueOf(4)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(1001)), equalTo(Integer.valueOf(4)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(9998)), equalTo(Integer.valueOf(4)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(9999)), equalTo(Integer.valueOf(4)));

    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(10000)), equalTo(Integer.valueOf(5)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(10001)), equalTo(Integer.valueOf(5)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(99998)), equalTo(Integer.valueOf(5)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(99999)), equalTo(Integer.valueOf(5)));

    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(100000)), equalTo(Integer.valueOf(6)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(100001)), equalTo(Integer.valueOf(6)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(999998)), equalTo(Integer.valueOf(6)));
    assertThat(Integer.valueOf(FileNameTagChecker.determineNumberOfAlbumDigits(999999)), equalTo(Integer.valueOf(6)));
  }

  @Test
  public void testCheckFilename_None() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", null, null, null, "2013", null, "Track Artist", null, "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkFilename(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.4")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckFilename_Only_AlbumDiscNumber() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", null, null, "2013", null, "Track Artist", null, "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkFilename(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.4")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckFilename_Only_TrackNumber() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", null, null, "1", "2013", null, "Track Artist", null, "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkFilename(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.4")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckFilename_Only_TrackTitle() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", null, null, null, "2013", null, "Track Artist", "Track Title", "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/01 - Track Title.flac"));

    FileNameTagChecker.checkFilename(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.4")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckFilename_WrongAlbumDiscNumber1() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("81", "a/1", "Album Title", "23", "2013", null, "Track Artist", "Track Title",
            "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/154123 - Track Title.flac"));

    FileNameTagChecker.checkFilename(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.4")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckFilename_WrongAlbumDiscNumber2() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("81", "1/b", "Album Title", "23", "2013", null, "Track Artist", "Track Title",
            "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/154123 - Track Title.flac"));

    FileNameTagChecker.checkFilename(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.4")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckFilename_WrongTrackNumber() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("81", "21/1541", "Album Title", "a", "2013", null, "Track Artist", "Track Title",
            "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/154123 - Track Title.flac"));

    FileNameTagChecker.checkFilename(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.4")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckFilename_WrongFilename() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("81", "21/1541", "Album Title", "23", "2013", null, "Track Artist", "Track Title",
            "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/154123 - Track Title.flac"));

    FileNameTagChecker.checkFilename(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.5")));
    assertThat(pr.getExpectedValue(), equalTo("002123 - Track Title.flac"));
    assertThat(pr.getActualValue(), equalTo("154123 - Track Title.flac"));
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheckFilename_Normal() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("81", "21/1541", "Album Title", "23", "2013", null, "Track Artist", "Track Title",
            "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/002123 - Track Title.flac"));

    FileNameTagChecker.checkFilename(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testCheck_Null() {
    checker.check(null);
  }

  @Test
  public void testCheck_NoFields() {
    GenericTag tag = new GenericTag();
    tag.setBackingFile(new File("/some/path/root/Track Artist/Album Title/01 - Track Title.flac"));

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.6")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheck_EmptyMap() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, null, "Track Title", null);
    tag.setBackingFile(new File("/some/path/root/Track Artist/Album Title/01 - Track Title.flac"));

    /* set album title field to null */
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = tag.getFields();
    fields.put(GenericTagFieldName.ALBUMARTIST, new HashMap<String, Set<String>>());

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.0")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheck_NoBackingFile() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, "Track Artist", "Track Title", null);

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.FILE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("FileNameTagChecker.7")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheck_Normal() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, "Track Artist", "Track Title", null);
    tag.setBackingFile(new File("/some/path/root/Track Artist/Album Title/01 - Track Title.flac"));

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));

    tag =
        TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, null, "Track Title", "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/01 - Track Title.flac"));

    checker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));

    tag =
        TypeUtilsForTests.setupTag("11", "1/1", "Album Title", "1", "2013", null, "Track Artist", "Track Title",
            "Album Artist");
    tag.setBackingFile(new File("/some/path/root/Album Artist/Album Title/01 - Track Title.flac"));

    checker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }
}
