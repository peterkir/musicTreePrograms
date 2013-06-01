package nl.pelagic.audio.tag.checker.types;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.images.Artwork;
import org.jaudiotagger.tag.images.StandardArtwork;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method", "nls", "null"
})
public class TestGenericTag {

  GenericTag tag;

  @Before
  public void setUp() {
    tag = new GenericTag();
  }

  @After
  public void tearDown() {
    tag = null;
  }

  @Test
  public void testGenericTag() {
    assertThat(Boolean.valueOf(tag.hasArtwork()), equalTo(Boolean.FALSE));
    assertThat(tag.getBackingFile(), nullValue());
    assertThat(tag.getSourceTagClasses(), notNullValue());
    assertThat(Integer.valueOf(tag.getSourceTagClasses().size()), equalTo(Integer.valueOf(0)));
    assertThat(tag.getFields(), notNullValue());
    assertThat(Integer.valueOf(tag.getFields().size()), equalTo(Integer.valueOf(0)));
    assertThat(tag.getReports(), notNullValue());
    assertThat(Integer.valueOf(tag.getReports().size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testSetArtwork() {
    tag.setArtwork(null);
    assertThat(Boolean.valueOf(tag.hasArtwork()), equalTo(Boolean.FALSE));

    List<Artwork> lst = new ArrayList<>();
    tag.setArtwork(lst);
    assertThat(Boolean.valueOf(tag.hasArtwork()), equalTo(Boolean.FALSE));

    Artwork art = new StandardArtwork();
    lst.add(art);
    tag.setArtwork(lst);
    assertThat(Boolean.valueOf(tag.hasArtwork()), equalTo(Boolean.TRUE));
  }

  @Test
  public void testSetBackingFile() {
    File f = null;
    tag.setBackingFile(f);
    assertThat(tag.getBackingFile(), nullValue());

    f = new File("dummy");
    tag.setBackingFile(f);
    assertThat(tag.getBackingFile(), equalTo(f));
  }

  @Test
  public void testAddSourceTagClass() {
    tag.addSourceTagClass(null);
    assertThat(tag.getSourceTagClasses(), notNullValue());
    assertThat(Integer.valueOf(tag.getSourceTagClasses().size()), equalTo(Integer.valueOf(0)));

    tag.addSourceTagClass(FlacTag.class);
    tag.addSourceTagClass(ID3v24Tag.class);
    assertThat(tag.getSourceTagClasses(), notNullValue());
    assertThat(Integer.valueOf(tag.getSourceTagClasses().size()), equalTo(Integer.valueOf(2)));
    assertThat(Boolean.valueOf(tag.getSourceTagClasses().contains(FlacTag.class)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(tag.getSourceTagClasses().contains(ID3v24Tag.class)), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddToValueNameMap() {
    Map<String, Set<String>> valueNameMap = null;
    String name = null;
    String value = null;

    GenericTag.addToValueNameSetMap(valueNameMap, name, value);
    assertThat(valueNameMap, nullValue());

    valueNameMap = new TreeMap<>();
    name = null;
    value = null;
    GenericTag.addToValueNameSetMap(valueNameMap, name, value);
    assertThat(valueNameMap, notNullValue());
    assertThat(Integer.valueOf(valueNameMap.size()), equalTo(Integer.valueOf(0)));

    valueNameMap.clear();
    name = "name";
    value = null;
    GenericTag.addToValueNameSetMap(valueNameMap, name, value);
    assertThat(valueNameMap, notNullValue());
    assertThat(Integer.valueOf(valueNameMap.size()), equalTo(Integer.valueOf(0)));

    valueNameMap.clear();
    name = "name";
    value = "value";
    GenericTag.addToValueNameSetMap(valueNameMap, name, value);
    assertThat(valueNameMap, notNullValue());
    assertThat(Integer.valueOf(valueNameMap.size()), equalTo(Integer.valueOf(1)));
    Set<String> keys = valueNameMap.keySet();
    assertThat(Integer.valueOf(keys.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(keys.contains(value)), equalTo(Boolean.TRUE));
    Set<String> values = valueNameMap.get(value);
    assertThat(Integer.valueOf(values.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(values.contains(name)), equalTo(Boolean.TRUE));

    valueNameMap.clear();
    String name1 = "name1";
    value = "value";
    GenericTag.addToValueNameSetMap(valueNameMap, name1, value);
    String name2 = "name2";
    value = "value";
    GenericTag.addToValueNameSetMap(valueNameMap, name2, value);
    assertThat(valueNameMap, notNullValue());
    assertThat(Integer.valueOf(valueNameMap.size()), equalTo(Integer.valueOf(1)));
    keys = valueNameMap.keySet();
    assertThat(Integer.valueOf(keys.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(keys.contains(value)), equalTo(Boolean.TRUE));
    values = valueNameMap.get(value);
    assertThat(Integer.valueOf(values.size()), equalTo(Integer.valueOf(2)));
    assertThat(Boolean.valueOf(values.contains(name1)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(values.contains(name2)), equalTo(Boolean.TRUE));

    valueNameMap.clear();
    name1 = "name1";
    String value1 = "value1";
    GenericTag.addToValueNameSetMap(valueNameMap, name1, value1);
    name2 = "name2";
    String value2 = "value2";
    GenericTag.addToValueNameSetMap(valueNameMap, name2, value2);
    assertThat(valueNameMap, notNullValue());
    assertThat(Integer.valueOf(valueNameMap.size()), equalTo(Integer.valueOf(2)));
    keys = valueNameMap.keySet();
    assertThat(Integer.valueOf(keys.size()), equalTo(Integer.valueOf(2)));
    assertThat(Boolean.valueOf(keys.contains(value1)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(keys.contains(value2)), equalTo(Boolean.TRUE));
    values = valueNameMap.get(value1);
    assertThat(Integer.valueOf(values.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(values.contains(name1)), equalTo(Boolean.TRUE));
    values = valueNameMap.get(value2);
    assertThat(Integer.valueOf(values.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(values.contains(name2)), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddField_Null() {
    GenericTag gt = new GenericTag();

    NameValuePair inp = new NameValuePair("name", "value");
    NameValuePair prev;

    prev = gt.addField(null, null, null, null);
    assertThat(prev, nullValue());
    assertThat(Integer.valueOf(gt.getFields().size()), equalTo(Integer.valueOf(0)));

    prev = gt.addField(null, GenericTagFieldName.ALBUMTITLE, null, null);
    assertThat(prev, nullValue());
    assertThat(Integer.valueOf(gt.getFields().size()), equalTo(Integer.valueOf(0)));

    prev = gt.addField(null, GenericTagFieldName.ALBUMTITLE, inp.getName(), null);
    assertThat(prev, nullValue());
    assertThat(Integer.valueOf(gt.getFields().size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testAddField_AddToEmpty_NoPrevious() {
    GenericTag gt = new GenericTag();

    NameValuePair inp = new NameValuePair("name", "value");
    NameValuePair prev;

    prev = gt.addField(null, GenericTagFieldName.ALBUMTITLE, inp.getName(), inp.getValue());
    assertThat(prev, notNullValue());
    assertThat(prev.getName(), equalTo(inp.getName()));
    assertThat(prev.getValue(), equalTo(inp.getValue()));
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = gt.getFields();
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(1)));
    Map<String, Set<String>> first = fields.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(1)));
    Set<String> value = first.get(inp.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp.getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddField_AddToEmpty_NoPreviousName() {
    GenericTag gt = new GenericTag();

    NameValuePair prev0 = new NameValuePair(null, "value");
    NameValuePair inp = new NameValuePair("name", "value");
    NameValuePair prev;

    prev = gt.addField(prev0, GenericTagFieldName.ALBUMTITLE, inp.getName(), inp.getValue());
    assertThat(prev, notNullValue());
    assertThat(prev.getName(), equalTo(inp.getName()));
    assertThat(prev.getValue(), equalTo(inp.getValue()));
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = gt.getFields();
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(1)));
    Map<String, Set<String>> first = fields.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(1)));
    Set<String> value = first.get(inp.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp.getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddField_AddToEmpty_NoPreviousValue() {
    GenericTag gt = new GenericTag();

    NameValuePair prev0 = new NameValuePair("name", null);
    NameValuePair inp = new NameValuePair("name", "value");
    NameValuePair prev;

    prev = gt.addField(prev0, GenericTagFieldName.ALBUMTITLE, inp.getName(), inp.getValue());
    assertThat(prev, notNullValue());
    assertThat(prev.getName(), equalTo(inp.getName()));
    assertThat(prev.getValue(), equalTo(inp.getValue()));
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = gt.getFields();
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(1)));
    Map<String, Set<String>> first = fields.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(1)));
    Set<String> value = first.get(inp.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp.getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddField_AddToEmpty_NonMatchingPreviousSameValue() {
    GenericTag gt = new GenericTag();

    NameValuePair inp0 = new NameValuePair("nameprev", "value");
    NameValuePair inp = new NameValuePair("name", "value");
    NameValuePair prev = new NameValuePair("nameprev", "value");

    prev = gt.addField(null, GenericTagFieldName.ALBUMTITLE, inp0.getName(), inp0.getValue());
    prev = gt.addField(prev, GenericTagFieldName.ALBUMTITLE, inp.getName(), inp.getValue());
    assertThat(prev, notNullValue());
    assertThat(prev.getName(), equalTo(inp.getName()));
    assertThat(prev.getValue(), equalTo(inp.getValue()));
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = gt.getFields();
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(1)));
    Map<String, Set<String>> first = fields.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(1)));
    Set<String> value = first.get(prev.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(2)));
    assertThat(Boolean.valueOf(value.contains(inp0.getName())), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(value.contains(inp.getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddField_AddToEmpty_NonMatchingPrevious() {
    GenericTag gt = new GenericTag();

    NameValuePair prev0 = new NameValuePair("name0", "value0");
    NameValuePair inp = new NameValuePair("name", "value");
    NameValuePair prev = new NameValuePair("nameprev", "valueprev");

    prev = gt.addField(prev0, GenericTagFieldName.ALBUMTITLE, prev.getName(), prev.getValue());
    prev = gt.addField(prev, GenericTagFieldName.ALBUMTITLE, inp.getName(), inp.getValue());
    assertThat(prev, notNullValue());
    assertThat(prev.getName(), equalTo(inp.getName()));
    assertThat(prev.getValue(), equalTo(inp.getValue()));
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = gt.getFields();
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(1)));
    Map<String, Set<String>> first = fields.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(2)));
    Set<String> value = first.get(prev.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp.getName())), equalTo(Boolean.TRUE));
    value = first.get(inp.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp.getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddField_AddToEmpty_MatchingPrevious() {
    GenericTag gt = new GenericTag();

    NameValuePair inp0 = new NameValuePair("name", "valueprev");
    NameValuePair inp1 = new NameValuePair("name", "valueinp");
    NameValuePair prev = new NameValuePair("name", "valueprev");

    prev = gt.addField(null, GenericTagFieldName.ALBUMTITLE, inp0.getName(), inp0.getValue());
    prev = gt.addField(prev, GenericTagFieldName.ALBUMTITLE, inp1.getName(), inp1.getValue());
    assertThat(prev, notNullValue());
    assertThat(prev.getName(), equalTo(inp1.getName()));
    assertThat(prev.getValue(), equalTo(inp0.getValue() + " - " + inp1.getValue()));
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = gt.getFields();
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(1)));
    Map<String, Set<String>> first = fields.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(1)));
    Set<String> value = first.get(prev.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp1.getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddField_AddToEmpty_MatchingPrevious_WithExtraSameValue() {
    GenericTag gt = new GenericTag();

    NameValuePair inp00 = new NameValuePair("name00", "valueprev");
    NameValuePair inp01 = new NameValuePair("name01", "valueinp");
    NameValuePair inp1 = new NameValuePair("name", "valueinp");
    NameValuePair prev;

    prev = gt.addField(null, GenericTagFieldName.ALBUMTITLE, inp00.getName(), inp00.getValue());
    prev = gt.addField(prev, GenericTagFieldName.ALBUMTITLE, inp01.getName(), inp01.getValue());
    prev = gt.addField(prev, GenericTagFieldName.ALBUMTITLE, inp1.getName(), inp1.getValue());
    prev = gt.addField(prev, GenericTagFieldName.ALBUMTITLE, inp1.getName(), inp1.getValue());

    assertThat(prev, notNullValue());
    assertThat(prev.getName(), equalTo(inp1.getName()));
    assertThat(prev.getValue(), equalTo(inp1.getValue() + " - " + inp1.getValue()));
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = gt.getFields();
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(1)));
    Map<String, Set<String>> first = fields.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(3)));

    assertThat(Boolean.valueOf(first.keySet().contains(inp00.getValue())), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(first.keySet().contains(inp01.getValue())), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(first.keySet().contains(inp1.getValue() + " - " + inp1.getValue())),
        equalTo(Boolean.TRUE));

    Set<String> value = first.get(inp00.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp00.getName())), equalTo(Boolean.TRUE));

    value = first.get(inp01.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp01.getName())), equalTo(Boolean.TRUE));

    value = first.get(inp1.getValue() + " - " + inp1.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp1.getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddField_AddToEmpty_FakedPrevious() {
    GenericTag gt = new GenericTag();

    NameValuePair inp0 = new NameValuePair("name", "valueprev");
    NameValuePair inp1 = new NameValuePair("name", "valueinp");
    NameValuePair prev = new NameValuePair("name", "valueprev");

    prev = gt.addField(null, GenericTagFieldName.ALBUMTITLE, inp0.getName(), inp0.getValue());
    prev = gt.addField(inp1, GenericTagFieldName.ALBUMTITLE, inp1.getName(), inp1.getValue());
    assertThat(prev, notNullValue());
    assertThat(prev.getName(), equalTo(inp1.getName()));
    assertThat(prev.getValue(), equalTo(inp1.getValue()));
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = gt.getFields();
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(1)));
    Map<String, Set<String>> first = fields.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(2)));
    Set<String> value = first.get(inp1.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp1.getName())), equalTo(Boolean.TRUE));
    value = first.get(inp0.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp0.getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddField_AddToEmpty_FakedPrevious2() {
    GenericTag gt = new GenericTag();

    NameValuePair inp1 = new NameValuePair("name", "valueinp");
    NameValuePair prev = new NameValuePair("name", "valueprev");

    prev = gt.addField(inp1, GenericTagFieldName.ALBUMTITLE, inp1.getName(), inp1.getValue());
    Map<GenericTagFieldName, Map<String, Set<String>>> fields = gt.getFields();
    Map<String, Set<String>> first = fields.get(GenericTagFieldName.ALBUMTITLE);
    Set<String> value = first.get(inp1.getValue());
    value.clear();
    prev = gt.addField(inp1, GenericTagFieldName.ALBUMTITLE, inp1.getName(), inp1.getValue());

    assertThat(prev, notNullValue());
    assertThat(prev.getName(), equalTo(inp1.getName()));
    assertThat(prev.getValue(), equalTo(inp1.getValue()));
    fields = gt.getFields();
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(1)));

    first = fields.get(GenericTagFieldName.ALBUMTITLE);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(1)));

    value = first.get(inp1.getValue());
    assertThat(value, notNullValue());
    assertThat(Integer.valueOf(value.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(value.contains(inp1.getName())), equalTo(Boolean.TRUE));
  }

  @Test
  public void testAddReports_Null() {
    GenericTag gt = new GenericTag();

    gt.addReports(null, null);
    Map<GenericTagFieldName, List<ProblemReport>> reports = gt.getReports();
    assertThat(reports, notNullValue());
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));

    gt.addReports(GenericTagFieldName.ALBUMARTIST, null);
    reports = gt.getReports();
    assertThat(reports, notNullValue());
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testAddReports_Normal() {
    GenericTag gt = new GenericTag();

    String msg1 = "msg1";
    String exp1 = "exp1";
    String act1 = "act1";
    ProblemReport pr1 = new ProblemReport(msg1, exp1, act1, null);

    List<ProblemReport> newReports = new ArrayList<>();
    newReports.add(pr1);
    gt.addReports(GenericTagFieldName.ALBUMARTIST, newReports);

    Map<GenericTagFieldName, List<ProblemReport>> reports = gt.getReports();
    assertThat(reports, notNullValue());
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));
    List<ProblemReport> first = reports.get(GenericTagFieldName.ALBUMARTIST);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(1)));
    assertThat(first.get(0), equalTo(pr1));

    gt.addReports(GenericTagFieldName.ALBUMARTIST, newReports);

    reports = gt.getReports();
    assertThat(reports, notNullValue());
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));
    first = reports.get(GenericTagFieldName.ALBUMARTIST);
    assertThat(first, notNullValue());
    assertThat(Integer.valueOf(first.size()), equalTo(Integer.valueOf(2)));
    assertThat(first.get(0), equalTo(pr1));
    assertThat(first.get(1), equalTo(pr1));
  }

  @Test
  public void testAddReport_Null() {
    GenericTag gt = new GenericTag();

    gt.addReport(null, null, null, null, null);
    Map<GenericTagFieldName, List<ProblemReport>> reports = gt.getReports();
    assertThat(reports, notNullValue());
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));

    gt.addReport(GenericTagFieldName.ALBUMARTIST, null, null, null, null);
    reports = gt.getReports();
    assertThat(reports, notNullValue());
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(0)));

    gt.addReport(GenericTagFieldName.ALBUMARTIST, "msg", null, null, null);
    reports = gt.getReports();
    assertThat(reports, notNullValue());
    assertThat(Integer.valueOf(reports.size()), equalTo(Integer.valueOf(1)));
  }
}
