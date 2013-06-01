package nl.pelagic.audio.tag.checker.common;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.regex.Matcher;

import org.junit.Test;

@SuppressWarnings({
    "nls", "javadoc", "static-method"
})
public class TestRegularExpressions {
  @Test
  public void testREGEX_LEADING_WHITESPACE() {
    assertThat(Boolean.valueOf("".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("a".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("a  a".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.FALSE));

    assertThat(Boolean.valueOf(" ".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("a ".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(" a".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("  ".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("a  ".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("  a".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("  a  ".matches(RegularExpressions.REGEX_LEADING_WHITESPACE)), equalTo(Boolean.TRUE));
  }

  @Test
  public void testREGEX_TRAILING_WHITESPACE() {
    assertThat(Boolean.valueOf("".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("a".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("a  a".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.FALSE));

    assertThat(Boolean.valueOf(" ".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("a ".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(" a".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("  ".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("a  ".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("  a".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("  a  ".matches(RegularExpressions.REGEX_TRAILING_WHITESPACE)), equalTo(Boolean.TRUE));
  }

  private void incrementalTestRegexMultiWhitespace(String s, int count, String[] exp) {
    int i = 0;
    Matcher m = RegularExpressions.patternMultipleWhitespace.matcher(s);
    while (i < count) {
      assertThat(Boolean.valueOf(m.find()), equalTo(Boolean.TRUE));
      assertThat(Integer.valueOf(m.groupCount()), equalTo(Integer.valueOf(1)));
      assertThat(m.group(1), equalTo(exp[i]));
      i++;
    }
    assertThat(Boolean.valueOf(m.find()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testREGEX_MULTI_WHITESPACE() {
    String[] exp1 = {
      "  "
    };

    String[] exp3 = {
        "  ", "  ", "  "
    };

    String[] exp3a = {
        "    ", "  ", "   "
    };

    incrementalTestRegexMultiWhitespace("a  ", 1, exp1);
    incrementalTestRegexMultiWhitespace("  a", 1, exp1);
    incrementalTestRegexMultiWhitespace("a  a", 1, exp1);
    incrementalTestRegexMultiWhitespace("  a  a  ", 3, exp3);
    incrementalTestRegexMultiWhitespace("    a  a   ", 3, exp3a);

    incrementalTestRegexMultiWhitespace("a", 0, null);
    incrementalTestRegexMultiWhitespace("a ", 0, null);
    incrementalTestRegexMultiWhitespace(" a", 0, null);
    incrementalTestRegexMultiWhitespace("a b", 0, null);
    incrementalTestRegexMultiWhitespace(" a b ", 0, null);
  }

  @Test
  public void testREGEX_YEAR() {
    assertThat(Boolean.valueOf("2013".matches(RegularExpressions.REGEX_YEAR)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(" 2013 ".matches(RegularExpressions.REGEX_YEAR)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("  0000  ".matches(RegularExpressions.REGEX_YEAR)), equalTo(Boolean.TRUE));

    assertThat(Boolean.valueOf("a".matches(RegularExpressions.REGEX_YEAR)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("-1".matches(RegularExpressions.REGEX_YEAR)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("111".matches(RegularExpressions.REGEX_YEAR)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("11111".matches(RegularExpressions.REGEX_YEAR)), equalTo(Boolean.FALSE));
  }

  @Test
  public void testREGEX_SIMPLE_NUMBER() {
    assertThat(Boolean.valueOf("2013".matches(RegularExpressions.REGEX_SIMPLE_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(" 2013 ".matches(RegularExpressions.REGEX_SIMPLE_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("  2013  ".matches(RegularExpressions.REGEX_SIMPLE_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("0".matches(RegularExpressions.REGEX_SIMPLE_NUMBER)), equalTo(Boolean.TRUE));

    assertThat(Boolean.valueOf("a".matches(RegularExpressions.REGEX_SIMPLE_NUMBER)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("-1".matches(RegularExpressions.REGEX_SIMPLE_NUMBER)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("a111".matches(RegularExpressions.REGEX_SIMPLE_NUMBER)), equalTo(Boolean.FALSE));
  }

  @Test
  public void testREGEX_DISC_NUMBER() {
    assertThat(Boolean.valueOf("1/1".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("1 /1".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("1/ 1".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("1 / 1".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(" 1 /1".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(" 1/ 1 ".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(" 1 / 1 ".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("12/18".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.TRUE));

    assertThat(Boolean.valueOf("a".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("1".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("/".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("/1".matches(RegularExpressions.REGEX_DISC_NUMBER)), equalTo(Boolean.FALSE));
  }

  @Test
  public void testREGEX_MP3_TRACK_NUMBER() {
    assertThat(Boolean.valueOf("1".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(" 1".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("  1  ".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(" 1 /1".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(" 1 / 1 ".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("  1  /  1  ".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf("12/18".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.TRUE));

    assertThat(Boolean.valueOf("a".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("/".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("a/b".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf("/1".matches(RegularExpressions.REGEX_MP3_TRACK_NUMBER)), equalTo(Boolean.FALSE));
  }

  private void incrementalTestRegexMp3TextValue(String s, int count, String[][] exp) {
    int i = 0;
    Matcher m = RegularExpressions.patternMp3TextValue.matcher(s);
    while (i < count) {
      String[] nameValue = exp[i];
      assertThat(Boolean.valueOf(m.find()), equalTo(Boolean.TRUE));
      assertThat(Integer.valueOf(m.groupCount()), equalTo(Integer.valueOf(2)));
      assertThat(m.group(1), equalTo(nameValue[0]));
      assertThat(m.group(2), equalTo(nameValue[1]));
      i++;
    }
    assertThat(Boolean.valueOf(m.find()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testREGEX_MP3_TEXT_VALUE() {

    String[][] exp0 = {
      {
          "name", "value"
      }
    };
    String[][] exp1 = {
        {
            "Language", "English"
        }, {
            "Lyrics", "Give me"
        }
    };

    incrementalTestRegexMp3TextValue("  name  =  \"value\" ;  ", 1, exp0);
    incrementalTestRegexMp3TextValue("Language=\"English\"; Lyrics=\"Give me\";", 2, exp1);

    incrementalTestRegexMp3TextValue("    ", 0, null);
    incrementalTestRegexMp3TextValue("  name  ;  ", 0, null);
    incrementalTestRegexMp3TextValue("  name = ;  ", 0, null);
    incrementalTestRegexMp3TextValue("  name = value  ", 0, null);
    incrementalTestRegexMp3TextValue("  name  =  value ;  ", 0, null);
  }

  @Test
  public void testCompiledPatterns() {
    assertThat(RegularExpressions.patternLeadingWhitespace, notNullValue());
    assertThat(RegularExpressions.patternTrailingWhitespace, notNullValue());
    assertThat(RegularExpressions.patternMultipleWhitespace, notNullValue());
    assertThat(RegularExpressions.patternYear, notNullValue());
    assertThat(RegularExpressions.patternSimpleNumber, notNullValue());
    assertThat(RegularExpressions.patternDiscNumber, notNullValue());
    assertThat(RegularExpressions.patternMp3TrackNumber, notNullValue());
    assertThat(RegularExpressions.patternMp3TextValue, notNullValue());
  }
}
