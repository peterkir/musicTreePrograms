package nl.pelagic.audio.tag.checker.types;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method"
})
public class TestProblemReport {

  private SecureRandom random = new SecureRandom();

  @Test
  public void testProblemReport() {
    ProblemReport pr = new ProblemReport();

    assertThat(pr.getMessage(), nullValue());
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testProblemReportStringStringStringListOfInteger() {
    String msg = new BigInteger(130, random).toString(32);
    String expected = new BigInteger(130, random).toString(32);
    String actual = new BigInteger(130, random).toString(32);
    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(new Random().nextInt(actual.length())));
    markers.add(Integer.valueOf(new Random().nextInt(actual.length())));
    markers.add(Integer.valueOf(new Random().nextInt(actual.length())));
    markers.add(Integer.valueOf(new Random().nextInt(actual.length())));
    markers.add(Integer.valueOf(new Random().nextInt(actual.length())));
    ProblemReport pr = new ProblemReport(msg, expected, actual, markers);

    assertThat(pr.getMessage(), equalTo(msg));
    assertThat(pr.getExpectedValue(), equalTo(expected));
    assertThat(pr.getActualValue(), equalTo(actual));
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testSetMessage() {
    String msg = new BigInteger(130, random).toString(32);
    ProblemReport pr = new ProblemReport();
    pr.setMessage(msg);

    assertThat(pr.getMessage(), equalTo(msg));
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testSetExpectedValue() {
    String expected = new BigInteger(130, random).toString(32);
    ProblemReport pr = new ProblemReport();
    pr.setExpectedValue(expected);

    assertThat(pr.getMessage(), nullValue());
    assertThat(pr.getExpectedValue(), equalTo(expected));
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testSetActualValue() {
    String actual = new BigInteger(130, random).toString(32);
    ProblemReport pr = new ProblemReport();
    pr.setActualValue(actual);

    assertThat(pr.getMessage(), nullValue());
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), equalTo(actual));
    assertThat(pr.getPositionMarkers(), nullValue());
  }

  @Test
  public void testSetPositionMarkers() {
    List<Integer> markers = new ArrayList<>();
    markers.add(Integer.valueOf(new Random().nextInt(100)));
    markers.add(Integer.valueOf(new Random().nextInt(100)));
    markers.add(Integer.valueOf(new Random().nextInt(100)));
    markers.add(Integer.valueOf(new Random().nextInt(100)));
    markers.add(Integer.valueOf(new Random().nextInt(100)));
    ProblemReport pr = new ProblemReport();
    pr.setPositionMarkers(markers);

    assertThat(pr.getMessage(), nullValue());
    assertThat(pr.getExpectedValue(), nullValue());
    assertThat(pr.getActualValue(), nullValue());
    assertThat(pr.getPositionMarkers(), equalTo(markers));
  }

  @Test
  public void testToString() {
    ProblemReport pr = new ProblemReport();
    assertThat(pr.toString(), notNullValue());
  }
}
