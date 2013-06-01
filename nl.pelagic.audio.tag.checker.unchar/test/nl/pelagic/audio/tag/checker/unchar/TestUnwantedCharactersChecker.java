package nl.pelagic.audio.tag.checker.unchar;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.ProblemReport;
import nl.pelagic.audio.tag.checker.types.TypeUtilsForTests;
import nl.pelagic.audio.tag.checker.unchar.i18n.Messages;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

@SuppressWarnings({
    "nls", "static-method", "javadoc"
})
public class TestUnwantedCharactersChecker {

  private static UnwantedCharactersChecker checker;

  @BeforeClass
  public static void setUpBeforeClass() {
    checker = new UnwantedCharactersChecker();
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
  public void testCheck_NullMap() {
    GenericTag tag = TypeUtilsForTests.setupTag("11", "1/1", "", "1", "2013", null, null, null, null);

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

    /* set album title field to null */
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = tag.getFields();
    fields.put(GenericTagFieldName.ALBUMTITLE, new HashMap<String, Set<String>>());

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testCheck_Unwanted() {
    String s = "  multipleX XXinternalXX XunwantedXXcharacters  ";
    char[] sa = s.toCharArray();
    sa[10] = 0x9a;
    sa[12] = 0x9b;
    sa[13] = 0x9c;
    sa[22] = 0x02;
    sa[23] = 0x03;
    sa[25] = 0x04;
    sa[34] = 0x19;
    sa[35] = 0x18;
    s = new String(sa);

    GenericTag tag = TypeUtilsForTests.setupTag("11", "1/1", s, "1", "2013", null, null, null, null);

    checker.check(tag);

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));

    List<ProblemReport> report = reports.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(report, notNullValue());
    assertThat(Integer.valueOf(report.size()), equalTo(Integer.valueOf(1)));

    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(10));
    markers.add(Integer.valueOf(12));
    markers.add(Integer.valueOf(13));
    markers.add(Integer.valueOf(22));
    markers.add(Integer.valueOf(23));
    markers.add(Integer.valueOf(25));
    markers.add(Integer.valueOf(34));
    markers.add(Integer.valueOf(35));

    ProblemReport pr = report.get(0);
    assertThat(pr, notNullValue());
    assertThat(pr.getMessage(), equalTo(Messages.getString("UnwantedCharactersChecker.0")));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), equalTo(s));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }
}
