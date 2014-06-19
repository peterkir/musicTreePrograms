package nl.pelagic.audio.tag.checker.converter.mp3;

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
import org.jaudiotagger.tag.id3.AbstractID3v2Frame;
import org.jaudiotagger.tag.id3.AbstractID3v2Tag;
import org.jaudiotagger.tag.id3.ID3v22Frame;
import org.jaudiotagger.tag.id3.ID3v22Tag;
import org.jaudiotagger.tag.id3.ID3v23Frame;
import org.jaudiotagger.tag.id3.ID3v23Tag;
import org.jaudiotagger.tag.id3.ID3v24Frame;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.jaudiotagger.tag.id3.framebody.AbstractID3v2FrameBody;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTALB;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTCON;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTDRC;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTIT2;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTOPE;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPE1;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPE2;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTPOS;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTRCK;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTSSE;
import org.jaudiotagger.tag.id3.framebody.FrameBodyTYER;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "nls", "javadoc", "static-method"
})
public class TestMp3TagConverter {

  private Mp3TagConverter converter;

  @Before
  public void setUp() {
    converter = new Mp3TagConverter();
  }

  @After
  public void tearDown() {
    converter = null;
  }

  @Test
  public void testMp3TagConverter() {
    Set<Class<? extends Object>> stc = converter.getSupportedTagClasses();

    assertThat(stc, notNullValue());
    assertThat(Integer.valueOf(stc.size()), equalTo(Integer.valueOf(3)));
    assertThat(Boolean.valueOf(stc.contains(ID3v22Tag.class)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(stc.contains(ID3v23Tag.class)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(stc.contains(ID3v24Tag.class)), equalTo(Boolean.TRUE));

    Map<Class<? extends Object>, Set<String>> utfns = converter.getUnknownTagFieldNames();
    assertThat(utfns, notNullValue());
    assertThat(Integer.valueOf(utfns.size()), equalTo(Integer.valueOf(3)));
    Set<String> utfns22 = utfns.get(ID3v22Tag.class);
    assertThat(utfns22, notNullValue());
    assertThat(Integer.valueOf(utfns22.size()), equalTo(Integer.valueOf(0)));
    Set<String> utfns23 = utfns.get(ID3v22Tag.class);
    assertThat(utfns23, notNullValue());
    assertThat(Integer.valueOf(utfns23.size()), equalTo(Integer.valueOf(0)));
    Set<String> utfns24 = utfns.get(ID3v22Tag.class);
    assertThat(utfns24, notNullValue());
    assertThat(Integer.valueOf(utfns24.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testGetSupportedTagClasses() {
    Set<Class<? extends Object>> stc = converter.getSupportedTagClasses();

    assertThat(stc, notNullValue());
    assertThat(Integer.valueOf(stc.size()), equalTo(Integer.valueOf(3)));
    assertThat(Boolean.valueOf(stc.contains(ID3v22Tag.class)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(stc.contains(ID3v23Tag.class)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(stc.contains(ID3v24Tag.class)), equalTo(Boolean.TRUE));
  }

  @Test
  public void testGetUnknownTagFieldNames_Initial() {
    Map<Class<? extends Object>, Set<String>> utfns = converter.getUnknownTagFieldNames();
    assertThat(utfns, notNullValue());
    assertThat(Integer.valueOf(utfns.size()), equalTo(Integer.valueOf(3)));
    Set<String> utfns22 = utfns.get(ID3v22Tag.class);
    assertThat(utfns22, notNullValue());
    assertThat(Integer.valueOf(utfns22.size()), equalTo(Integer.valueOf(0)));
    Set<String> utfns23 = utfns.get(ID3v22Tag.class);
    assertThat(utfns23, notNullValue());
    assertThat(Integer.valueOf(utfns23.size()), equalTo(Integer.valueOf(0)));
    Set<String> utfns24 = utfns.get(ID3v22Tag.class);
    assertThat(utfns24, notNullValue());
    assertThat(Integer.valueOf(utfns24.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testExtractValues_Null() {
    String nameIn = "nameIn";
    String valueIn = null;
    String valueOut = converter.extractValues(nameIn, valueIn);

    assertThat(valueOut, equalTo(valueIn));
  }

  @Test
  public void testExtractValues_Empty() {
    String nameIn = "nameIn";
    String valueIn = "";
    String valueOut = converter.extractValues(nameIn, valueIn);

    assertThat(valueOut, equalTo(valueIn));
  }

  @Test
  public void testExtractValues_NonMatching() {
    String nameIn = "nameIn";
    String valueIn = "some string that doesn't match the regex";
    String valueOut = converter.extractValues(nameIn, valueIn);

    assertThat(valueOut, equalTo(valueIn));
  }

  @Test
  public void testExtractValues_SingleMatch() {
    String nameIn = "nameIn";
    String valueIn = "Language=\"English\";";
    String valueOut = converter.extractValues(nameIn, valueIn);

    assertThat(valueOut, equalTo("English"));
  }

  @Test
  public void testExtractValues_MultiMatch() {
    String nameIn = "nameIn";
    String valueIn = "Language=\"English\"; Lyrics=\"Give me\";";
    String valueOut = converter.extractValues(nameIn, valueIn);

    assertThat(valueOut, equalTo("English - Give me"));
  }

  @Test
  public void testConvert_NoFields() {
    GenericTag genericTag = new GenericTag();
    ID3v24Tag tag = new ID3v24Tag();

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
    assertThat(Integer.valueOf(utfns.size()), equalTo(Integer.valueOf(3)));
    Set<String> utfns22 = utfns.get(ID3v22Tag.class);
    assertThat(utfns22, notNullValue());
    assertThat(Integer.valueOf(utfns22.size()), equalTo(Integer.valueOf(0)));
    Set<String> utfns23 = utfns.get(ID3v22Tag.class);
    assertThat(utfns23, notNullValue());
    assertThat(Integer.valueOf(utfns23.size()), equalTo(Integer.valueOf(0)));
    Set<String> utfns24 = utfns.get(ID3v22Tag.class);
    assertThat(utfns24, notNullValue());
    assertThat(Integer.valueOf(utfns24.size()), equalTo(Integer.valueOf(0)));
  }

  private Object setupTag(int version, String[] idNames, String[] idValues) throws FieldDataInvalidException {
    assert (idNames.length == idValues.length);

    AbstractID3v2Tag tag;
    switch (version) {
      case 24:
        tag = new ID3v24Tag();
        break;
      case 23:
        tag = new ID3v23Tag();
        break;
      case 22:
        tag = new ID3v22Tag();
        break;
      default:
        throw new IllegalArgumentException("Wrong version " + version);
    }

    Iterator<TagField> it = tag.getFields();
    while (it.hasNext()) {
      TagField f = it.next();
      tag.deleteField(f.getId());
    }

    for (int i = 0; i < idNames.length; i++) {
      AbstractID3v2Frame frame;
      switch (version) {
        case 24:
          frame = new ID3v24Frame(idNames[i]);
          break;
        case 23:
          frame = new ID3v23Frame(idNames[i]);
          break;
        case 22:
          frame = new ID3v22Frame(idNames[i]);
          break;
        default:
          throw new IllegalArgumentException("Wrong version " + version);
      }

      AbstractID3v2FrameBody frameBody;
      switch (idNames[i]) {
        case "TPOS":
          frameBody = new FrameBodyTPOS();
          break;

        case "TCON":
          frameBody = new FrameBodyTCON();
          break;

        case "TOPE":
          frameBody = new FrameBodyTOPE();
          break;

        case "TPE2":
          frameBody = new FrameBodyTPE2();
          break;

        case "TALB":
          frameBody = new FrameBodyTALB();
          break;

        case "TRCK":
          frameBody = new FrameBodyTRCK();
          break;

        case "TYER":
          frameBody = new FrameBodyTYER();
          break;

        case "TDRC":
          frameBody = new FrameBodyTDRC();
          break;

        case "TPE1":
          frameBody = new FrameBodyTPE1();
          break;

        case "TIT2":
          frameBody = new FrameBodyTIT2();
          break;

        case "TSSE":
          frameBody = new FrameBodyTSSE();
          break;

        default:
          throw new IllegalArgumentException("Wrong name " + idNames[i]);
      }
      frameBody.setObjectValue("Text", idValues[i]);

      frame.setBody(frameBody);
      tag.addField(frame);
    }

    return tag;
  }

  private void checkFields(int version, GenericTag genericTag, String[] idNames, String[] idExpectedValues,
      GenericTagFieldName[] genericids, String[] unknowns) {
    assert (idNames.length == idExpectedValues.length);
    assert (idNames.length == genericids.length);

    Class<? extends Object> clazz;
    switch (version) {
      case 24:
        clazz = ID3v24Tag.class;
        break;
      case 23:
        clazz = ID3v23Tag.class;
        break;
      case 22:
        clazz = ID3v22Tag.class;
        break;
      default:
        throw new IllegalArgumentException("Wrong version " + version);
    }

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
    assertThat(Integer.valueOf(utfns.size()), equalTo(Integer.valueOf(3)));
    Set<String> utfns1 = utfns.get(clazz);
    assertThat(utfns1, notNullValue());
    assertThat(Integer.valueOf(utfns1.size()), equalTo(Integer.valueOf(unknowns.length)));
    for (String unknown : unknowns) {
      assertThat(Boolean.valueOf(utfns1.contains(unknown)), equalTo(Boolean.TRUE));
    }
  }

  @Test
  public void testConvert_AllFields24() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TDRC", "TPE1", "TIT2", "TSSE"
    };

    String[] idValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "14/28",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    String[] idNamesCheck = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TRCK", "TDRC", "TPE1", "TIT2", "TSSE"
    };

    String[] idExpectedValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "28",
            "14",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    GenericTagFieldName[] genericids =
        {
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMARTIST,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.ALBUMTOTALTRACKS,
            GenericTagFieldName.TRACKNUMBER,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKTITLE,
            GenericTagFieldName.OTHER
        };

    String[] unknowns = {
      "TSSE"
    };

    ID3v24Tag tag = (ID3v24Tag) setupTag(24, idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(24, genericTag, idNamesCheck, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_AllFields23() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "TPOS", "TCON", "TOPE", "TALB", "TRCK", "TYER", "TPE1", "TIT2", "TSSE"
    };

    String[] idValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "14/28",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    String[] idNamesCheck = {
        "TPOS", "TCON", "TOPE", "TALB", "TRCK", "TRCK", "TYER", "TPE1", "TIT2", "TSSE"
    };

    String[] idExpectedValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "28",
            "14",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    GenericTagFieldName[] genericids =
        {
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMARTIST,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.ALBUMTOTALTRACKS,
            GenericTagFieldName.TRACKNUMBER,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKTITLE,
            GenericTagFieldName.OTHER
        };

    String[] unknowns = {
      "TSSE"
    };

    ID3v23Tag tag = (ID3v23Tag) setupTag(23, idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(23, genericTag, idNamesCheck, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_AllFields22() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TDRC", "TPE1", "TIT2", "TSSE"
    };

    String[] idValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "14/28",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    String[] idNamesCheck = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TRCK", "TDRC", "TPE1", "TIT2", "TSSE"
    };

    String[] idExpectedValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "28",
            "14",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    GenericTagFieldName[] genericids =
        {
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMARTIST,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.ALBUMTOTALTRACKS,
            GenericTagFieldName.TRACKNUMBER,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKTITLE,
            GenericTagFieldName.OTHER
        };

    String[] unknowns = {
      "TSSE"
    };

    ID3v22Tag tag = (ID3v22Tag) setupTag(22, idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(22, genericTag, idNamesCheck, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_AllFields24NumericGenre() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TDRC", "TPE1", "TIT2", "TSSE"
    };

    String[] idValues =
        {
            "ALBUMDISCNUMBER value",
            "5",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "14/28",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    String[] idNamesCheck = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TRCK", "TDRC", "TPE1", "TIT2", "TSSE"
    };

    String[] idExpectedValues =
        {
            "ALBUMDISCNUMBER value",
            "Funk",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "28",
            "14",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    GenericTagFieldName[] genericids =
        {
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMARTIST,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.ALBUMTOTALTRACKS,
            GenericTagFieldName.TRACKNUMBER,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKTITLE,
            GenericTagFieldName.OTHER
        };

    String[] unknowns = {
      "TSSE"
    };

    ID3v24Tag tag = (ID3v24Tag) setupTag(24, idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(24, genericTag, idNamesCheck, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_AllFields24NonMatchingTrackNumber() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TDRC", "TPE1", "TIT2"
    };

    String[] idValues =
        {
            "ALBUMDISCNUMBER value",
            "5",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "NonMatchingTrackNumber",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value"
        };

    String[] idNamesCheck = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TDRC", "TPE1", "TIT2"
    };

    String[] idExpectedValues =
        {
            "ALBUMDISCNUMBER value",
            "Funk",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "NonMatchingTrackNumber",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value"
        };

    // ALBUMDISCNUMBER={ALBUMDISCNUMBER value=[TPOS]}
    // ALBUMGENRE={Funk=[TCON]}
    // ALBUMTITLE={ALBUMTITLE value=[TALB]}
    // ALBUMYEAR={ALBUMYEAR value=[TDRC]}
    // TRACKARTIST={TRACKARTIST value=[TPE1]}
    // TRACKTITLE={TRACKTITLE value=[TIT2]}
    // ALBUMARTIST={ALBUMARTIST value=[TPE2]}
    // OTHER={NonMatchingTrackNumber=[TRCK]

    GenericTagFieldName[] genericids =
        {
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMARTIST,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.OTHER,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKTITLE
        };

    String[] unknowns = {};

    ID3v24Tag tag = (ID3v24Tag) setupTag(24, idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(24, genericTag, idNamesCheck, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_AllFields24TrackNumber1() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TDRC", "TPE1", "TIT2", "TSSE"
    };

    String[] idValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "14",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    String[] idNamesCheck = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TDRC", "TPE1", "TIT2", "TSSE"
    };

    String[] idExpectedValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "14",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value",
            "THISFIELDDOESNTEXIST value"
        };

    GenericTagFieldName[] genericids =
        {
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMARTIST,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.TRACKNUMBER,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKTITLE,
            GenericTagFieldName.OTHER
        };

    String[] unknowns = {
      "TSSE"
    };

    ID3v24Tag tag = (ID3v24Tag) setupTag(24, idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(24, genericTag, idNamesCheck, idExpectedValues, genericids, unknowns);
  }

  @Test
  public void testConvert_AllFields24TrackNumber2() throws FieldDataInvalidException {
    GenericTag genericTag = new GenericTag();

    String[] idNames = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TDRC", "TPE1", "TIT2"
    };

    String[] idValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "/28",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value"
        };

    String[] idNamesCheck = {
        "TPOS", "TCON", "TPE2", "TALB", "TRCK", "TDRC", "TPE1", "TIT2"
    };

    String[] idExpectedValues =
        {
            "ALBUMDISCNUMBER value",
            "ALBUMGENRE value",
            "ALBUMARTIST value",
            "ALBUMTITLE value",
            "/28",
            "ALBUMYEAR value",
            "TRACKARTIST value",
            "TRACKTITLE value"
        };

    GenericTagFieldName[] genericids =
        {
            GenericTagFieldName.ALBUMDISCNUMBER,
            GenericTagFieldName.ALBUMGENRE,
            GenericTagFieldName.ALBUMARTIST,
            GenericTagFieldName.ALBUMTITLE,
            GenericTagFieldName.OTHER,
            GenericTagFieldName.ALBUMYEAR,
            GenericTagFieldName.TRACKARTIST,
            GenericTagFieldName.TRACKTITLE
        };

    String[] unknowns = {};

    ID3v24Tag tag = (ID3v24Tag) setupTag(24, idNames, idValues);

    boolean result = converter.convert(genericTag, tag);

    assertThat(Boolean.valueOf(result), equalTo(Boolean.TRUE));
    checkFields(24, genericTag, idNamesCheck, idExpectedValues, genericids, unknowns);
  }

}
