package nl.pelagic.audio.tag.checker.common;

import java.util.regex.Pattern;

import aQute.bnd.annotation.ProviderType;

/**
 * RegularExpressions and compiled regular expressions for use in the audio tag
 * checker
 */
@ProviderType
public class RegularExpressions {
  /** regular expression for leading whitespace, for full string matching only */
  public static final String REGEX_LEADING_WHITESPACE = "^(\\s+).*$"; //$NON-NLS-1$

  /** regular expression for trailing whitespace, for full string matching only */
  public static final String REGEX_TRAILING_WHITESPACE = "^.*?(\\s+)$"; //$NON-NLS-1$

  /**
   * regular expression for multiple consecutive whitespace characters, for
   * incremental matching
   */
  public static final String REGEX_MULTI_WHITESPACE = ".*?(\\s{2,})"; //$NON-NLS-1$

  /** regular expression for a year: 4 digits, for full string matching only */
  public static final String REGEX_YEAR = "^\\s*\\d{4}\\s*$"; //$NON-NLS-1$

  /**
   * regular expression for a simple number: digits only, for full string
   * matching only
   */
  public static final String REGEX_SIMPLE_NUMBER = "^\\s*\\d+\\s*$"; //$NON-NLS-1$

  /**
   * regular expression for a disc number: digits/digits, for full string
   * matching only
   */
  public static final String REGEX_DISC_NUMBER = "^\\s*\\d+\\s*/\\s*\\d+\\s*$"; //$NON-NLS-1$

  /**
   * regular expression for an mp3 track number: digits, optionally followed by
   * /digits, for full string matching only
   */
  public static final String REGEX_MP3_TRACK_NUMBER = "^\\s*\\d+\\s*(/\\s*\\d+)?\\s*$"; //$NON-NLS-1$

  /** regular expression for an mp3 text value, for incremental matching */
  public static final String REGEX_MP3_TEXT_VALUE = "\\s*([^\\s]+)\\s*=\\s*\"(.*?)\"\\s*;"; //$NON-NLS-1$

  /** Compiled {@link #REGEX_LEADING_WHITESPACE} */
  public static final Pattern patternLeadingWhitespace;

  /** Compiled {@link #REGEX_TRAILING_WHITESPACE} */
  public static final Pattern patternTrailingWhitespace;

  /** Compiled {@link #REGEX_MULTI_WHITESPACE} */
  public static final Pattern patternMultipleWhitespace;

  /** Compiled {@link #REGEX_YEAR} */
  public static final Pattern patternYear;

  /** Compiled {@link #REGEX_SIMPLE_NUMBER} */
  public static final Pattern patternSimpleNumber;

  /** Compiled {@link #REGEX_DISC_NUMBER} */
  public static final Pattern patternDiscNumber;

  /** Compiled {@link #REGEX_MP3_TRACK_NUMBER} */
  public static final Pattern patternMp3TrackNumber;

  /** Compiled {@link #REGEX_MP3_TEXT_VALUE} */
  public static final Pattern patternMp3TextValue;

  /**
   * Compile regular expressions
   */
  static {
    try {
      patternLeadingWhitespace = Pattern.compile(REGEX_LEADING_WHITESPACE);
      patternTrailingWhitespace = Pattern.compile(REGEX_TRAILING_WHITESPACE);
      patternMultipleWhitespace = Pattern.compile(REGEX_MULTI_WHITESPACE);
      patternYear = Pattern.compile(REGEX_YEAR);
      patternSimpleNumber = Pattern.compile(REGEX_SIMPLE_NUMBER);
      patternDiscNumber = Pattern.compile(REGEX_DISC_NUMBER);
      patternMp3TrackNumber = Pattern.compile(REGEX_MP3_TRACK_NUMBER);
      patternMp3TextValue = Pattern.compile(REGEX_MP3_TEXT_VALUE);
    }
    catch (Throwable e) {
      /* can't be covered by a test */
      throw new ExceptionInInitializerError(e);
    }
  }
}
