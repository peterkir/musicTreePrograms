package nl.pelagic.audio.tag.checker.basic;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.pelagic.audio.tag.checker.basic.i18n.Messages;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.ProblemReport;
import nl.pelagic.audio.tag.checker.types.TypeUtilsForTests;

import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method", "nls"
})
public class TestBasicTagChecker {

  private static BasicTagChecker basicTagChecker;

  @BeforeClass
  public static void setUpBeforeClass() {
    basicTagChecker = new BasicTagChecker();
  }

  @AfterClass
  public static void tearDownAfterClass() {
    basicTagChecker = null;
  }

  @Test
  public void testCheck_Null() {
    basicTagChecker.check(null);
  }

  @Test
  public void testCheck_MissingPrimaries() {
    GenericTag tag = new GenericTag();
    List<Artwork> artwork = new ArrayList<>();
    artwork.add(new StandardArtwork());
    tag.setArtwork(artwork);

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(2)));

    List<ProblemReport> artworkReport = reports.get(GenericTagFieldName.ARTWORK);
    assertThat(artworkReport, notNullValue());

    List<ProblemReport> fileReport = reports.get(GenericTagFieldName.FILE);
    assertThat(fileReport, notNullValue());

    /* artwork report */

    assertThat(Integer.valueOf(artworkReport.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = artworkReport.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.0")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());

    /* file report */

    assertThat(Integer.valueOf(fileReport.size()), equalTo(Integer.valueOf(1)));

    List<GenericTagFieldName> primariesList = new LinkedList<>();
    for (GenericTagFieldName primary : GenericTagFieldName.getPrimaries(false)) {
      primariesList.add(primary);
    }
    Collections.sort(primariesList);

    pr = fileReport.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.1") + " " + primariesList));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheck_NullMap() {
    GenericTag tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "", "1", "2013", "genre", "track artist", "track title", "album artist");

    /* set album title field to null */
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = tag.getFields();
    fields.put(GenericTagFieldName.ALBUMTITLE, null);

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.2")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheck_EmptyMap() {
    GenericTag tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "", "1", "2013", "genre", "track artist", "track title", "album artist");

    /* set album title field to null */
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = tag.getFields();
    fields.put(GenericTagFieldName.ALBUMTITLE, new HashMap<String, Set<String>>());

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.2")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheck_EmptyField() {
    GenericTag tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "", "1", "2013", "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.2")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheck_LeadingWhitespaceSingle() {
    String s = " leading whitespace";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(0));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(s.trim()));
    assertThat(pr.getActualValue(), equalTo(s));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testCheck_LeadingWhitespaceMultiple() {
    String s = "     leading whitespace";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(0));
    markers.add(Integer.valueOf(1));
    markers.add(Integer.valueOf(2));
    markers.add(Integer.valueOf(3));
    markers.add(Integer.valueOf(4));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(s.trim()));
    assertThat(pr.getActualValue(), equalTo(s));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testCheck_LeadingWhitespaceMultipleWithTabs() {
    String s = "  \t  leading whitespace";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(0));
    markers.add(Integer.valueOf(1));
    markers.add(Integer.valueOf(2));
    markers.add(Integer.valueOf(3));
    markers.add(Integer.valueOf(4));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(s.trim()));
    assertThat(pr.getActualValue(), equalTo(s.replaceAll("\\s", " ")));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testCheck_TrailingWhitespaceSingle() {
    String s = "trailing whitespace ";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(19));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(s.trim()));
    assertThat(pr.getActualValue(), equalTo(s));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testCheck_TrailingWhitespaceMultiple() {
    String s = "trailing whitespace     ";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(19));
    markers.add(Integer.valueOf(20));
    markers.add(Integer.valueOf(21));
    markers.add(Integer.valueOf(22));
    markers.add(Integer.valueOf(23));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(s.trim()));
    assertThat(pr.getActualValue(), equalTo(s));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testCheck_TrailingWhitespaceMultipleWithTabs() {
    String s = "trailing whitespace  \t  ";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(19));
    markers.add(Integer.valueOf(20));
    markers.add(Integer.valueOf(21));
    markers.add(Integer.valueOf(22));
    markers.add(Integer.valueOf(23));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(s.trim()));
    assertThat(pr.getActualValue(), equalTo(s.replaceAll("\\s", " ")));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testCheck_WhitespaceMultipleInternal() {
    String s = "multiple  internal  whitespace  repeated";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(8));
    markers.add(Integer.valueOf(9));
    markers.add(Integer.valueOf(18));
    markers.add(Integer.valueOf(19));
    markers.add(Integer.valueOf(30));
    markers.add(Integer.valueOf(31));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.5")));
    assertThat(pr.getExpectedValue(), equalTo(s.trim().replaceAll("\\s+", " ")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testCheck_LeadingAndTrailingWhitespaceMultipleInternal() {
    String s = "  multiple  internal  whitespace  ";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(2)));

    List<Integer> markersLT = new ArrayList<>();
    markersLT.add(Integer.valueOf(0));
    markersLT.add(Integer.valueOf(1));
    markersLT.add(Integer.valueOf(32));
    markersLT.add(Integer.valueOf(33));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(s.trim()));
    assertThat(pr.getActualValue(), equalTo(s));
    assertThat(pr.getPositionMarkers(), equalTo(markersLT));

    List<Integer> markersIN = new ArrayList<>();
    markersIN.add(Integer.valueOf(8));
    markersIN.add(Integer.valueOf(9));
    markersIN.add(Integer.valueOf(18));
    markersIN.add(Integer.valueOf(19));

    pr = report.get(1);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.5")));
    assertThat(pr.getExpectedValue(), equalTo(s.trim().replaceAll("\\s+", " ")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), equalTo(markersIN));
  }

  @Test
  public void testCheck_LeadingAndTrailingWhitespaceOnAllFields() {
    String albumTotalTracks = " 11 ";
    String discNumber = " 1/1 ";
    String title = " title ";
    String trackNumber = " 1 ";
    String year = " 2013 ";
    GenericTag tag =
        TypeUtilsForTests.setupTag(albumTotalTracks, discNumber, title, trackNumber, year, "genre", "track artist",
            "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(5)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTOTALTRACKS);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(0));
    markers.add(Integer.valueOf(3));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(albumTotalTracks.trim()));
    assertThat(pr.getActualValue(), equalTo(albumTotalTracks));
    assertThat(pr.getPositionMarkers(), equalTo(markers));

    report = reports.get(GenericTagFieldName.ALBUMDISCNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    markers = new ArrayList<>();
    markers.add(Integer.valueOf(0));
    markers.add(Integer.valueOf(4));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(discNumber.trim()));
    assertThat(pr.getActualValue(), equalTo(discNumber));
    assertThat(pr.getPositionMarkers(), equalTo(markers));

    report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    markers = new ArrayList<>();
    markers.add(Integer.valueOf(0));
    markers.add(Integer.valueOf(6));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(title.trim()));
    assertThat(pr.getActualValue(), equalTo(title));
    assertThat(pr.getPositionMarkers(), equalTo(markers));

    report = reports.get(GenericTagFieldName.TRACKNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    markers = new ArrayList<>();
    markers.add(Integer.valueOf(0));
    markers.add(Integer.valueOf(2));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(trackNumber.trim()));
    assertThat(pr.getActualValue(), equalTo(trackNumber));
    assertThat(pr.getPositionMarkers(), equalTo(markers));

    report = reports.get(GenericTagFieldName.ALBUMYEAR);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    markers = new ArrayList<>();
    markers.add(Integer.valueOf(0));
    markers.add(Integer.valueOf(5));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.4")));
    assertThat(pr.getExpectedValue(), equalTo(year.trim()));
    assertThat(pr.getActualValue(), equalTo(year));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testCheck_DiscNumber() {
    String s = "/";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", s, "title", "1", "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMDISCNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.6")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.7")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 2 */

    s = "1";
    tag =
        TypeUtilsForTests.setupTag("11", s, "title", "1", "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMDISCNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.6")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.7")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 3 */

    s = "1/";
    tag =
        TypeUtilsForTests.setupTag("11", s, "title", "1", "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMDISCNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.6")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.7")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 4 */

    s = "/1";
    tag =
        TypeUtilsForTests.setupTag("11", s, "title", "1", "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMDISCNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.6")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.7")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 5 */

    s = "one/two";
    tag =
        TypeUtilsForTests.setupTag("11", s, "title", "1", "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMDISCNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.6")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.7")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 6 */

    s = "11/9";
    tag =
        TypeUtilsForTests.setupTag("11", s, "title", "1", "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMDISCNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    List<Integer> positionMarkers = new ArrayList<>();
    positionMarkers.add(Integer.valueOf(0));
    positionMarkers.add(Integer.valueOf(1));
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.14")));
    assertThat(pr.getExpectedValue(),
        equalTo(String.format(Messages.getString("BasicTagChecker.15"), Integer.valueOf(9), Integer.valueOf(9))));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), equalTo(positionMarkers));
  }

  @Test
  public void testCheck_TrackNumber() {
    String s = "one";
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", "title", s, "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.TRACKNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.8")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.9")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 2 */

    s = "1 one";
    tag =
        TypeUtilsForTests.setupTag("11", "1/1", "title", s, "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.TRACKNUMBER);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.8")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.9")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheck_TotalTracksNumber() {
    String s = "one";
    GenericTag tag =
        TypeUtilsForTests.setupTag(s, "1/1", "title", "1", "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTOTALTRACKS);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.8")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.9")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 2 */

    s = "1 one";
    tag =
        TypeUtilsForTests.setupTag(s, "1/1", "title", "1", "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMTOTALTRACKS);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.8")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.9")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testCheck_Year() {
    String s = "three";
    GenericTag tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "title", "1", s, "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMYEAR);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.10")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.11")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 2 */

    s = "1";
    tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "title", "1", s, "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMYEAR);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.10")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.11")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 3 */

    s = "11";
    tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "title", "1", s, "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMYEAR);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.10")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.11")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 4 */

    s = "111";
    tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "title", "1", s, "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMYEAR);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.10")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.11")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 5 */

    s = "11111";
    tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "title", "1", s, "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMYEAR);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.10")));
    assertThat(pr.getExpectedValue(), equalTo(Messages.getString("BasicTagChecker.11")));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    int yearNow = Calendar.getInstance().get(Calendar.YEAR);
    int yearLow = 1500;
    String yearError = Messages.getString("BasicTagChecker.13") + " [" + yearLow + ", " + yearNow + "]";

    /* case 6 */

    s = (yearLow - 1) + "";
    tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "title", "1", s, "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMYEAR);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.12")));
    assertThat(pr.getExpectedValue(), equalTo(yearError));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

    /* case 7 */

    s = "" + (yearNow + 1);
    tag =
        TypeUtilsForTests
            .setupTag("11", "1/1", "title", "1", s, "genre", "track artist", "track title", "album artist");

    basicTagChecker.check(tag);

    reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    report = reports.get(GenericTagFieldName.ALBUMYEAR);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("BasicTagChecker.12")));
    assertThat(pr.getExpectedValue(), equalTo(yearError));
    assertThat(pr.getActualValue(), equalTo(s.trim()));
    assertThat(pr.getPositionMarkers(), nullValue());

  }

  @Test
  public void testCheck_Normal() {
    GenericTag tag =
        TypeUtilsForTests.setupTag("11", "1/1", "title", "1", "2013", "genre", "track artist", "track title",
            "album artist");

    basicTagChecker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }
}
