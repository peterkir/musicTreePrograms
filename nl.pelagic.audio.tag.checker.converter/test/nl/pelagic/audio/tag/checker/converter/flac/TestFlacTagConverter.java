package nl.pelagic.audio.tag.checker.converter.flac;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;

import org.jaudiotagger.tag.FieldDataInvalidException;
import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.vorbiscomment.VorbisCommentTagField;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "nls", "javadoc", "static-method"
})
public class TestFlacTagConverter {

  private FlacTagConverter converter;

  @Before
  public void setUp() {
    converter = new FlacTagConverter();
  }

  @After
  public void tearDown() {
    converter = null;
  }

  @Test
  public void testFlacTagConverter() {
    Set<Class<? extends Object>> stc = converter.getSupportedTagClasses();

    assertThat(stc, notNullValue());
    assertThat(Integer.valueOf(stc.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(stc.contains(FlacTag.class)), equalTo(Boolean.TRUE));

    Map<Class<? extends Object>, Set<String>> utfns = converter.getUnknownTagFieldNames();
    assertThat(utfns, notNullValue());
    assertThat(Integer.valueOf(utfns.size()), equalTo(Integer.valueOf(1)));
    Set<String> utfns1 = utfns.get(FlacTag.class);
    assertThat(utfns1, notNullValue());
    assertThat(Integer.valueOf(utfns1.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testGetSupportedTagClasses() {
    Set<Class<? extends Object>> stc = converter.getSupportedTagClasses();

    assertThat(stc, notNullValue());
    assertThat(Integer.valueOf(stc.size()), equalTo(Integer.valueOf(1)));
    assertThat(Boolean.valueOf(stc.contains(FlacTag.class)), equalTo(Boolean.TRUE));
  }

  @Test
  public void testGetUnknownTagFieldNames_Initial() {
    Map<Class<? extends Object>, Set<String>> utfns = converter.getUnknownTagFieldNames();
    assertThat(utfns, notNullValue());
    assertThat(Integer.valueOf(utfns.size()), equalTo(Integer.valueOf(1)));
    Set<String> utfns1 = utfns.get(FlacTag.class);
    assertThat(utfns1, notNullValue());
    assertThat(Integer.valueOf(utfns1.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testConvert_NoFields() {
    GenericTag genericTag = new GenericTag();
    FlacTag tag = new FlacTag();

    Iterator<TagField> it = tag.getFields();
    while (it.hasNext()) {
      TagField f = it.next();
      tag.deleteField(f.getId());
    }

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));

    Map<GenericTagFieldName, Map<String, Set<String>>> fields = genericTag.getFields();
    assertThat(fields, notNullValue());
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(0)));

    /* no unknowns */
    Map<Class<? extends Object>, Set<String>> utfns = converter.getUnknownTagFieldNames();
    assertThat(utfns, notNullValue());
    assertThat(Integer.valueOf(utfns.size()), equalTo(Integer.valueOf(1)));
    Set<String> utfns1 = utfns.get(FlacTag.class);
    assertThat(utfns1, notNullValue());
    assertThat(Integer.valueOf(utfns1.size()), equalTo(Integer.valueOf(0)));
  }

  private FlacTag setupTag(String[] idNames, String[] idValues) throws FieldDataInvalidException {
    assert (idNames.length == idValues.length);

    FlacTag tag = new FlacTag();

    Iterator<TagField> it = tag.getFields();
    while (it.hasNext()) {
      TagField f = it.next();
      tag.deleteField(f.getId());
    }

    for (int i = 0; i < idNames.length; i++) {
      tag.addField(new VorbisCommentTagField(idNames[i], idValues[i]));
    }

    return tag;
  }

  private void checkFields(GenericTag genericTag, String[] idNames, String[] idExpectedValues,
      GenericTagFieldName[] genericids, String[] unknowns) {
    assert (idNames.length == idExpectedValues.length);
    assert (idNames.length == genericids.length);

    Map<GenericTagFieldName, Map<String, Set<String>>> fields = genericTag.getFields();
    assertThat(fields, notNullValue());
    assertThat(Integer.valueOf(fields.size()), equalTo(Integer.valueOf(idNames.length)));

    for (int i = 0; i < idNames.length; i++) {
      String idName = idNames[i];
      String idExpectedValue = idExpectedValues[i];
      GenericTagFieldName genericid = genericids[i];

      Map<String, Set<String>> valueNameMap = fields.get(genericid);
      assertThat(valueNameMap, notNullValue());
      assertThat(Integer.valueOf(valueNameMap.size()), equalTo(Integer.valueOf(1)));

      Set<String> tagIdNames = valueNameMap.get(idExpectedValue);
      assertThat(idNames, notNullValue());
      assertThat(Integer.valueOf(tagIdNames.size()), equalTo(Integer.valueOf(1)));
      assertThat(Boolean.valueOf(tagIdNames.contains(idName)), equalTo(Boolean.TRUE));
    }

    /* check unknowns */
    Map<Class<? extends Object>, Set<String>> utfns = converter.getUnknownTagFieldNames();
    assertThat(utfns, notNullValue());
    assertThat(Integer.valueOf(utfns.size()), equalTo(Integer.valueOf(1)));
    Set<String> utfns1 = utfns.get(FlacTag.class);
    assertThat(utfns1, notNullValue());
    assertThat(Integer.valueOf(utfns1.size()), equalTo(Integer.valueOf(unknowns.length)));
    for (String unknown : unknowns) {
      assertThat(Boolean.valueOf(utfns1.contains(unknown)), equalTo(Boolean.TRUE));
    }
  }

  @Test
  public void testConvert_AllFields() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames =
        {
            "DISCNUMBER",
            "GENRE",
            "ALBUMARTIST",
            "ALBUM",
            "TRACKTOTAL",
            "DATE",
            "ARTIST",
            "TRACKNUMBER",
            "TITLE",
            "THISFIELDDOESNTEXIST"
        };

    String[] idValues =
        {
            "DISCNUMBER value",
            "GENRE value",
            "ALBUMARTIST value",
            "ALBUM value",
            "TRACKTOTAL value",
            "DATE value",
            "ARTIST value",
            "TRACKNUMBER value",
            "TITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    String[] idExpectedValues =
        {
            "DISCNUMBER value",
            "GENRE value",
            "ALBUMARTIST value",
            "ALBUM value",
            "TRACKTOTAL value",
            "DATE value",
            "ARTIST value",
            "TRACKNUMBER value",
            "TITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    GenericTagFieldName[] genericids =
        {
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMARTIST,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.ALBUMTOTALTRACKS,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKNUMBER,
            GenericTagFieldName.TRACKTITLE,
            GenericTagFieldName.OTHER
        };

    String[] unknowns = {
      "THISFIELDDOESNTEXIST"
    };

    FlacTag tag = setupTag(idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(genericTag, idNames, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_SpecificFields() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
      "GENRE",
    };

    String[] idValues = {
      "5",
    };

    String[] idExpectedValues = {
      "Funk",
    };

    GenericTagFieldName[] genericids = {
      GenericTagFieldName.ALBUMGENRE
    };

    String[] unknowns = {};

    FlacTag tag = setupTag(idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(genericTag, idNames, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_DiscNumberAndTotal() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "DISCNUMBER", "DISCTOTAL"
    };

    String[] idValues = {
        "15", "53"
    };

    String[] idExpectedValues = {
        "15/53"
    };

    GenericTagFieldName[] genericids = {
        GenericTagFieldName.ALBUMDISCNUMBER
    };

    String[] unknowns = {};

    FlacTag tag = setupTag(idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    String[] idNames2 = {
        "DISCNUMBER"
    };
    checkFields(genericTag, idNames2, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_DiscNumberAndTotalOverrideOldFormat() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "DISCNUMBER", "DISCNUMBER", "DISCTOTAL"
    };

    String[] idValues = {
        "13/21", "15", "53"
    };

    String[] idExpectedValues = {
        "15/53"
    };

    GenericTagFieldName[] genericids = {
        GenericTagFieldName.ALBUMDISCNUMBER
    };

    String[] unknowns = {};

    FlacTag tag = setupTag(idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    String[] idNames2 = {
        "DISCNUMBER"
    };
    checkFields(genericTag, idNames2, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_OldFormatBetterThanDiscNumber() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "DISCNUMBER", "DISCNUMBER"
    };

    String[] idValues = {
        "15", "13/21"
    };

    String[] idExpectedValues = {
      "13/21"
    };

    GenericTagFieldName[] genericids = {
      GenericTagFieldName.ALBUMDISCNUMBER
    };

    String[] unknowns = {};

    FlacTag tag = setupTag(idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    String[] idNames2 = {
      "DISCNUMBER"
    };
    checkFields(genericTag, idNames2, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_OldFormatBetterThanDiscTotal() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "DISCTOTAL", "DISCNUMBER"
    };

    String[] idValues = {
        "15", "13/21"
    };

    String[] idExpectedValues = {
        "13/21"
    };

    GenericTagFieldName[] genericids = {
        GenericTagFieldName.ALBUMDISCNUMBER
    };

    String[] unknowns = {};

    FlacTag tag = setupTag(idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    String[] idNames2 = {
        "DISCNUMBER"
    };
    checkFields(genericTag, idNames2, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_OnlyDiscNumber() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
      "DISCNUMBER"
    };

    String[] idValues = {
      "15"
    };

    String[] idExpectedValues = {
        "15"
    };

    GenericTagFieldName[] genericids = {
        GenericTagFieldName.ALBUMDISCNUMBER
    };

    String[] unknowns = {};

    FlacTag tag = setupTag(idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(genericTag, idNames, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_OnlyDiscTotal() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
      "DISCTOTAL"
    };

    String[] idValues = {
      "15"
    };

    String[] idExpectedValues = {
      "/15"
    };

    GenericTagFieldName[] genericids = {
      GenericTagFieldName.ALBUMDISCNUMBER
    };

    String[] unknowns = {};

    FlacTag tag = setupTag(idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    String[] idNames2 = {
        "DISCNUMBER"
    };
    checkFields(genericTag, idNames2, idExpectedValues, genericids, unknowns);
  }

}
