package nl.pelagic.audio.tag.checker.id3v1;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.pelagic.audio.tag.checker.id3v1.i18n.Messages;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.ProblemReport;
import nl.pelagic.audio.tag.checker.types.TypeUtilsForTests;

import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.ID3v11Tag;
import org.jaudiotagger.tag.id3.ID3v1Tag;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings({
    "nls", "static-method", "javadoc"
})
public class TestID3v1TagChecker {

  private static ID3v1TagChecker checker;

  @BeforeClass
  public static void setUpBeforeClass() {
    checker = new ID3v1TagChecker();
  }

  @AfterClass
  public static void tearDownAfterClass() {
    checker = null;
  }

  @Test
  public void testCheck_Null() {
    checker.check(null);
  }

  @Test
  public void testCheck_NoSourceClasses() {
    GenericTag tag = TypeUtilsForTests.setupTag("11", "1/1", "Title", "1", "2013", null, null, null, null);

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testCheck_NoId3SourceClasses() {
    GenericTag tag = TypeUtilsForTests.setupTag("11", "1/1", "Title", "1", "2013", null, null, null, null);
    tag.addSourceTagClass(FlacTag.class);

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testCheck_NullMap() {
    GenericTag tag = TypeUtilsForTests.setupTag("11", "1/1", "", "1", "2013", null, null, null, null);
    tag.addSourceTagClass(ID3v1Tag.class);

    /* set album title field to null */
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = tag.getFields();
    fields.put(GenericTagFieldName.ALBUMTITLE, null);

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testCheck_EmptyMap() {
    GenericTag tag = TypeUtilsForTests.setupTag("11", "1/1", "", "1", "2013", null, null, null, null);
    tag.addSourceTagClass(ID3v1Tag.class);

    /* set album title field to null */
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = tag.getFields();
    fields.put(GenericTagFieldName.ALBUMTITLE, new HashMap<String, Set<String>>());

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testCheck_Truncated() {
    Class<?>[] clazzes = {
        ID3v1Tag.class, ID3v11Tag.class, ID3v22Tag.class, ID3v23Tag.class, ID3v24Tag.class
    };

    String s = "123456789012345678901234567890";

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(29));

    for (Class<?> clazz : clazzes) {
      /* album title */
      GenericTag tag = TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", null, null, null, "ALBUMARTIST value");
      tag.addSourceTagClass(clazz);

      checker.check(tag);

      Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
      assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

      List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
      assertThat(report, notNullValue());
      assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

      ProblemReport pr = report.get(0);
      assertThat(pr, notNullValue());
      assertThat(pr.getMessage(), equalTo(Messages.getString("ID3v1TagChecker.0")));
      assertThat(pr.getExpectedValue(), nullValue());
      assertThat(pr.getActualValue(), equalTo(s));
      assertThat(pr.getPositionMarkers(), equalTo(markers));

      /* track artist */
      tag = TypeUtilsForTests.setupTag("11", "1/1", "title", "1", "2013", null, s, null, "ALBUMARTIST value");
      tag.addSourceTagClass(clazz);

      checker.check(tag);

      reports = tag.getReports();
      assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

      report = reports.get(GenericTagFieldName.TRACKARTIST);
      assertThat(report, notNullValue());
      assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

      pr = report.get(0);
      assertThat(pr, notNullValue());
      assertThat(pr.getMessage(), equalTo(Messages.getString("ID3v1TagChecker.0")));
      assertThat(pr.getExpectedValue(), nullValue());
      assertThat(pr.getActualValue(), equalTo(s));
      assertThat(pr.getPositionMarkers(), equalTo(markers));

      /* track title */
      tag = TypeUtilsForTests.setupTag("11", "1/1", "title", "1", "2013", null, "track artist", s, "ALBUMARTIST value");
      tag.addSourceTagClass(clazz);

      checker.check(tag);

      reports = tag.getReports();
      assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

      report = reports.get(GenericTagFieldName.TRACKTITLE);
      assertThat(report, notNullValue());
      assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

      pr = report.get(0);
      assertThat(pr, notNullValue());
      assertThat(pr.getMessage(), equalTo(Messages.getString("ID3v1TagChecker.0")));
      assertThat(pr.getExpectedValue(), nullValue());
      assertThat(pr.getActualValue(), equalTo(s));
      assertThat(pr.getPositionMarkers(), equalTo(markers));

      /* album artist */
      tag = TypeUtilsForTests.setupTag("11", "1/1", "title", "1", "2013", null, "track artist", "track title", s);
      tag.addSourceTagClass(clazz);

      checker.check(tag);

      reports = tag.getReports();
      assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

      report = reports.get(GenericTagFieldName.ALBUMARTIST);
      assertThat(report, notNullValue());
      assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

      pr = report.get(0);
      assertThat(pr, notNullValue());
      assertThat(pr.getMessage(), equalTo(Messages.getString("ID3v1TagChecker.0")));
      assertThat(pr.getExpectedValue(), nullValue());
      assertThat(pr.getActualValue(), equalTo(s));
      assertThat(pr.getPositionMarkers(), equalTo(markers));

      tag = null;
    }
  }
}
