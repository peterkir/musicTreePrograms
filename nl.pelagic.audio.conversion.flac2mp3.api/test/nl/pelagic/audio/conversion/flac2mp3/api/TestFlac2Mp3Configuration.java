package nl.pelagic.audio.conversion.flac2mp3.api;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "nls", "javadoc", "static-method"
})
public class TestFlac2Mp3Configuration {

  private Flac2Mp3Configuration flac2Mp3Configuration = null;

  @Before
  public void setUp() {
    flac2Mp3Configuration = new Flac2Mp3Configuration();
  }

  @After
  public void tearDown() {
    flac2Mp3Configuration = null;
  }

  private static List<String> toList(String str) {
    return Arrays.asList(str.trim().split("\\s+"));
  }

  @Test
  public void testTryProgramRun_Null() {
    boolean r = Flac2Mp3Configuration.tryProgramRun(null);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testTryProgramRun_Null_In_First() {
    String[] program = {
        null, "some.program.that.doesnt.exist"
    };
    boolean r = Flac2Mp3Configuration.tryProgramRun(program);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testTryProgramRun_Empty_In_First() {
    String[] program = {
        "", "some.program.that.doesnt.exist"
    };
    boolean r = Flac2Mp3Configuration.tryProgramRun(program);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testTryProgramRun_NotFound() {
    String[] program = {
      "some.program.that.doesnt.exist"
    };
    boolean r = Flac2Mp3Configuration.tryProgramRun(program);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testTryProgramRun_Found() {
    String[] program = {
      "echo"
    };
    boolean r = Flac2Mp3Configuration.tryProgramRun(program);

    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidate_NotFound_Flac() {
    Flac2Mp3Configuration config = new Flac2Mp3Configuration();
    config.setFlacExecutable(null);
    config.setLameExecutable("echo");
    config.validate();
  }

  @Test(expected = IllegalArgumentException.class)
  public void testValidate_NotFound_Lame() {
    Flac2Mp3Configuration config = new Flac2Mp3Configuration();
    config.setFlacExecutable("echo");
    config.setLameExecutable(null);
    config.validate();
  }

  @Test
  public void testValidate_Normal() {
    Flac2Mp3Configuration config = new Flac2Mp3Configuration();
    config.setFlacExecutable("echo");
    config.setLameExecutable("echo");
    config.validate();
  }

  @Test
  public void testGetFlacOptions_Defaults() {
    List<String> dfo = toList(Flac2Mp3Configuration.DEFAULT_FLAC_OPTIONS);
    List<String> dlo = toList(Flac2Mp3Configuration.DEFAULT_LAME_OPTIONS);

    String fe = flac2Mp3Configuration.getFlacExecutable();
    List<String> fo = flac2Mp3Configuration.getFlacOptions();
    String le = flac2Mp3Configuration.getLameExecutable();
    List<String> lo = flac2Mp3Configuration.getLameOptions();

    assertThat(fe, equalTo(Flac2Mp3Configuration.DEFAULT_FLAC_EXECUTABLE));
    assertThat(fo, equalTo(dfo));
    assertThat(le, equalTo(Flac2Mp3Configuration.DEFAULT_LAME_EXECUTABLE));
    assertThat(lo, equalTo(dlo));
  }

  @Test
  public void testSetFlacOptions() {
    String in = "  a -b 123 --d  ";
    List<String> expected = toList(in);
    flac2Mp3Configuration.setFlacOptions("  a -b 123 --d  ");
    List<String> result = flac2Mp3Configuration.getFlacOptions();

    assertThat(result, equalTo(expected));
  }

  @Test
  public void testSetLameOptions() {
    String in = "  a -b 123 --d  ";
    List<String> expected = toList(in);
    flac2Mp3Configuration.setLameOptions("  a -b 123 --d  ");
    List<String> result = flac2Mp3Configuration.getLameOptions();

    assertThat(result, equalTo(expected));
  }
}
