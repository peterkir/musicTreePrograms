package nl.pelagic.audio.tag.checker.cli;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import nl.pelagic.audio.tag.checker.cli.i18n.Messages;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.ProblemReport;

import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "nls", "javadoc", "static-method"
})
public class TestCallback {

  private MyPrintStream out = null;
  private MyPrintStream err = null;
  private Callback cb = null;
  private String outfile = "testresources/out";
  private String errfile = "testresources/err";

  @Before
  public void setUp() throws Exception {
    out = new MyPrintStream(outfile);
    err = new MyPrintStream(errfile);
    cb = new Callback();
    cb.setVerbose(false);
    cb.setExtraVerbose(false);
    cb.setOut(out);
    cb.setErr(err);
  }

  @After
  public void tearDown() {
    err.close();
    out.close();
    err = null;
    out = null;
    cb = null;
    new File(outfile).delete();
    new File(errfile).delete();
  }

  @Test
  public void testDefaults() {
    cb = new Callback();
    assertThat(Boolean.valueOf(cb.isVerbose()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(cb.isExtraVerbose()), equalTo(Boolean.FALSE));
    assertThat(cb.getOut(), equalTo(System.out));
    assertThat(cb.getErr(), equalTo(System.err));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNull_Out() {
    cb.setOut(null);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testSetNull_Err() {
    cb.setErr(null);
  }

  @Test
  public void testUnsupportedExtension_No_Progress() {
    File f = new File("dummy");

    cb.unsupportedExtension(f);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(1)));
    String s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.1"), f.getPath())));
  }

  @Test
  public void testUnsupportedExtension_Progress() {
    File f = new File("dummy");
    cb.setVerbose(true);

    cb.unsupportedExtension(f);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(2)));
    String s = out.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(""));
    s = out.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(2)));
    s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));
    s = err.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.1"), f.getPath())));
  }

  @Test
  public void testNotReadable_No_Progress() {
    File f = new File("dummy");
    Exception e = new IOException("dummy");

    cb.notReadable(f, e);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(1)));
    String s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.2"), f.getPath())));
  }

  @Test
  public void testNotReadable_Progress() {
    File f = new File("dummy");
    Exception e = new IOException("dummy");
    cb.setVerbose(true);

    cb.notReadable(f, e);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(2)));
    String s = out.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(""));
    s = out.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(2)));
    s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));
    s = err.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.2"), f.getPath())));
  }

  @Test
  public void testNoTag_No_Progress() {
    File f = new File("dummy");

    cb.noTag(f);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(1)));
    String s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.3"), f.getPath())));
  }

  @Test
  public void testNoTag_Progress() {
    File f = new File("dummy");
    cb.setVerbose(true);

    cb.noTag(f);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(2)));
    String s = out.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(""));
    s = out.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(2)));
    s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));
    s = err.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.3"), f.getPath())));
  }

  @Test
  public void testTagNotConverted_No_Progress() {
    File f = new File("dummy");
    Tag tag = new FlacTag();

    cb.tagNotConverted(f, tag);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(2)));
    String s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.4"), f.getPath())));
    s = err.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.5"), tag.getClass().getName())));
  }

  @Test
  public void testTagNotConverted_Progress() {
    File f = new File("dummy");
    cb.setVerbose(true);
    Tag tag = new FlacTag();

    cb.tagNotConverted(f, tag);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(2)));
    String s = out.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(""));
    s = out.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(3)));
    s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));
    s = err.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.4"), f.getPath())));
    s = err.strings.get(2);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(String.format(Messages.getString("Callback.5"), tag.getClass().getName())));
  }

  @Test
  public void testChecksFailed_No_Progress() {
    GenericTag tag = new GenericTag();
    File bf = new File("dummy");
    tag.setBackingFile(bf);

    cb.checksFailed(tag);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(1)));
    String s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(bf.getPath()));
  }

  @Test
  public void testChecksFailed_Progress() {
    cb.setVerbose(true);
    GenericTag tag = new GenericTag();
    File bf = new File("dummy");
    tag.setBackingFile(bf);

    cb.checksFailed(tag);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(2)));
    String s = out.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(""));
    s = out.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(2)));
    s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));
    s = err.strings.get(1);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(bf.getPath()));
  }

  @Test
  public void testChecksPassed_No_Progress() {
    GenericTag tag = new GenericTag();

    cb.checksPassed(tag);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testChecksPassed_Progress() {
    cb.setVerbose(true);
    GenericTag tag = new GenericTag();
    int cntmax = Callback.LINE_LENGTH + 2;
    int cnt = cntmax;
    while (cnt-- > 0) {
      cb.checksPassed(tag);
    }

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(cntmax + 2)));
    cnt = 0;
    String s;
    while (cnt < Callback.LINE_LENGTH) {
      s = out.strings.get(cnt++);
      assertThat(s, notNullValue());
      assertThat(s, equalTo("."));
    }
    s = out.strings.get(cnt++);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(""));
    s = out.strings.get(cnt++);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));
    while (cnt < (cntmax + 2)) {
      s = out.strings.get(cnt++);
      assertThat(s, notNullValue());
      assertThat(s, equalTo("."));
    }

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(1)));
    s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(MyPrintStream.FLUSH));
  }

  @Test
  public void testChecksPassed_ExtraVerbose() {
    cb.setExtraVerbose(true);
    GenericTag tag = new GenericTag();
    File bf = new File("backingfile");
    tag.setBackingFile(bf);
    int cntmax = Callback.LINE_LENGTH + 2;
    int cnt = cntmax;
    while (cnt-- > 0) {
      cb.checksPassed(tag);
    }

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(cntmax)));
    cnt = 0;
    String s;
    while (cnt < cntmax) {
      s = out.strings.get(cnt++);
      assertThat(s, notNullValue());
      assertThat(s, equalTo(bf.getPath()));
    }

    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testPositionsToMarkerString() {
    String s = Callback.positionsToMarkerString(null);
    assertThat(s, equalTo(""));

    List<Integer> markers = new ArrayList<>();
    s = Callback.positionsToMarkerString(markers);
    assertThat(s, equalTo(""));

    markers.add(Integer.valueOf(14));
    markers.add(Integer.valueOf(1));
    markers.add(Integer.valueOf(7));
    markers.add(Integer.valueOf(3));

    s = Callback.positionsToMarkerString(markers);
    assertThat(s, equalTo(" ^ ^   ^      ^"));

    markers.add(Integer.valueOf(0));
    markers.remove(Integer.valueOf(1));
    s = Callback.positionsToMarkerString(markers);
    assertThat(s, equalTo("^  ^   ^      ^"));
  }

  @Test
  public void testPrintProblemReports_Null() {
    cb.printProblemReports(null, Callback.prefix);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testPrintProblemReports_No_Reports() {
    GenericTag tag = new GenericTag();
    tag.getReports().clear();
    File bf = new File("dummy");
    tag.setBackingFile(bf);

    cb.printProblemReports(tag, Callback.prefix);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(1)));
    String s = err.strings.get(0);
    assertThat(s, notNullValue());
    assertThat(s, equalTo(bf.getPath()));
  }

  @Test
  public void testPrintProblemReports_Simple_Report() {
    GenericTag tag = new GenericTag();
    tag.getReports().clear();
    File bf = new File("dummy");
    tag.setBackingFile(bf);
    List<Integer> positionMarkers = new LinkedList<>();
    positionMarkers.add(Integer.valueOf(0));
    ProblemReport pr = new ProblemReport("message", null, null, null);
    List<ProblemReport> prlist = new LinkedList<>();
    prlist.add(pr);
    tag.getReports().put(GenericTagFieldName.FILE, prlist);

    cb.printProblemReports(tag, Callback.prefix);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(4)));

    String format = Callback.prefix + "%s%-" + 8 + "s%s%s%n";
    String exp[] =
        {
            "",
            bf.getPath(),
            String.format("%s%s : %s%n", Callback.fieldIndent, Messages.getString("Callback.6"),
                GenericTagFieldName.FILE),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.7"), " : ", pr.getMessage()),
        };

    int i = 0;
    for (String s : err.strings) {
      assertThat(s, notNullValue());
      assertThat(s, equalTo(exp[i++]));
    }
  }

  @Test
  public void testPrintProblemReports_One_Report() {
    GenericTag tag = new GenericTag();
    tag.getReports().clear();
    File bf = new File("dummy");
    tag.setBackingFile(bf);
    List<Integer> positionMarkers = new LinkedList<>();
    positionMarkers.add(Integer.valueOf(0));
    ProblemReport pr = new ProblemReport("message", "expectedValue", "actualValue", positionMarkers);
    List<ProblemReport> prlist = new LinkedList<>();
    prlist.add(pr);
    tag.getReports().put(GenericTagFieldName.FILE, prlist);

    cb.printProblemReports(tag, Callback.prefix);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(1 + 1 + 5)));

    String format = Callback.prefix + "%s%-" + 8 + "s%s%s%n";
    String exp[] =
        {
            "",
            bf.getPath(),
            String.format("%s%s : %s%n", Callback.fieldIndent, Messages.getString("Callback.6"),
                GenericTagFieldName.FILE),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.7"), " : ", pr.getMessage()),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.8"), " : ", pr.getActualValue()),
            String.format(format, Callback.fieldIndent, "", "   ", "^"),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.9"), " : ", pr.getExpectedValue())
        };

    int i = 0;
    for (String s : err.strings) {
      assertThat(s, notNullValue());
      assertThat(s, equalTo(exp[i++]));
    }
  }

  @Test
  public void testPrintProblemReports_Three_Reports() {
    GenericTag tag = new GenericTag();
    tag.getReports().clear();
    File bf = new File("dummy");
    tag.setBackingFile(bf);
    List<Integer> positionMarkers = new LinkedList<>();
    positionMarkers.add(Integer.valueOf(0));
    ProblemReport pr = new ProblemReport("message", "expectedValue", "actualValue", positionMarkers);
    List<ProblemReport> prlist = new LinkedList<>();
    prlist.add(pr);
    prlist.add(pr);
    prlist.add(pr);
    tag.getReports().put(GenericTagFieldName.FILE, prlist);

    cb.printProblemReports(tag, Callback.prefix);

    assertThat(Integer.valueOf(out.strings.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(err.strings.size()), equalTo(Integer.valueOf(1 + 1 + 5 + 5 + 5)));

    String format = Callback.prefix + "%s%-" + 8 + "s%s%s%n";
    String exp[] =
        {
            "",
            bf.getPath(),
            String.format("%s%s : %s%n", Callback.fieldIndent, Messages.getString("Callback.6"),
                GenericTagFieldName.FILE),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.7"), " : ", pr.getMessage()),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.8"), " : ", pr.getActualValue()),
            String.format(format, Callback.fieldIndent, "", "   ", "^"),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.9"), " : ", pr.getExpectedValue()),
            "",
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.7"), " : ", pr.getMessage()),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.8"), " : ", pr.getActualValue()),
            String.format(format, Callback.fieldIndent, "", "   ", "^"),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.9"), " : ", pr.getExpectedValue()),
            "",
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.7"), " : ", pr.getMessage()),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.8"), " : ", pr.getActualValue()),
            String.format(format, Callback.fieldIndent, "", "   ", "^"),
            String.format(format, Callback.fieldIndent, Messages.getString("Callback.9"), " : ", pr.getExpectedValue())
        };

    int i = 0;
    for (String s : err.strings) {
      assertThat(s, notNullValue());
      assertThat(s, equalTo(exp[i++]));
    }
  }
}
