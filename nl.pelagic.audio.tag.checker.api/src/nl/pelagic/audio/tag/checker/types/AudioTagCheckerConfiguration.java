package nl.pelagic.audio.tag.checker.types;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

import nl.pelagic.audio.tag.checker.api.TagChecker;
import aQute.bnd.annotation.ProviderType;

/**
 * Configuration for the AudioTagChecker interface
 */
@ProviderType
public class AudioTagCheckerConfiguration {
  /** The default value for the recursiveScan setting */
  public static final boolean DEFAULT_RECURSIVESCAN = false;

  /** The default value for the regexInAllDirs setting */
  public static final boolean DEFAULT_REGEXINALLDIRS = false;

  /**
   * Default Constructor.
   */
  public AudioTagCheckerConfiguration() {
    super();
  }

  /**
   * Minimal Constructor.
   * 
   * @param checkPath the path to check
   */
  public AudioTagCheckerConfiguration(File checkPath) {
    super();
    this.checkPath = checkPath;
  }

  /** The path to check */
  private File checkPath = null;

  /**
   * @return the checkPath
   */
  public File getCheckPath() {
    return checkPath;
  }

  /**
   * @param checkPath the checkPath to set
   */
  public void setCheckPath(File checkPath) {
    this.checkPath = checkPath;
  }

  /** True when a recursive scan must be performed */
  private boolean recursiveScan = DEFAULT_RECURSIVESCAN;

  /**
   * @return the recursiveScan
   */
  public boolean isRecursiveScan() {
    return recursiveScan;
  }

  /**
   * @param recursiveScan the recursiveScan to set
   */
  public void setRecursiveScan(boolean recursiveScan) {
    this.recursiveScan = recursiveScan;
  }

  /**
   * True when the compiled regular expression must be applied in all
   * directories, false when it must only be applied in the checkPath directory.
   */
  private boolean regexInAllDirs = DEFAULT_REGEXINALLDIRS;

  /**
   * @return the regexInAllDirs
   */
  public boolean isRegexInAllDirs() {
    return regexInAllDirs;
  }

  /**
   * @param regexInAllDirs the regexInAllDirs to set
   */
  public void setRegexInAllDirs(boolean regexInAllDirs) {
    this.regexInAllDirs = regexInAllDirs;
  }

  /** The compiled regular expression to use for filename matching */
  private Pattern regexPattern = null;

  /**
   * @return the regexPattern
   */
  public Pattern getRegexPattern() {
    return regexPattern;
  }

  /**
   * @param regexPattern the regexPattern to set
   */
  public void setRegexPattern(Pattern regexPattern) {
    this.regexPattern = regexPattern;
  }

  /**
   * A list of enabled tag checkers
   * 
   * @see #isTagCheckerEnabled
   */
  private Set<String> enabledTagCheckers = null;

  /**
   * @return the enabledTagCheckers
   */
  public Set<String> getEnabledTagCheckers() {
    return enabledTagCheckers;
  }

  /**
   * @param enabledTagCheckers the enabledTagCheckers to set
   */
  public void setEnabledTagCheckers(Set<String> enabledTagCheckers) {
    this.enabledTagCheckers = enabledTagCheckers;
  }

  /**
   * A list of disabled tag checkers
   * 
   * @see #isTagCheckerDisabled
   */
  private Set<String> disabledTagCheckers = null;

  /**
   * @return the disabledTagCheckers
   */
  public Set<String> getDisabledTagCheckers() {
    return disabledTagCheckers;
  }

  /**
   * @param disabledTagCheckers the disabledTagCheckers to set
   */
  public void setDisabledTagCheckers(Set<String> disabledTagCheckers) {
    this.disabledTagCheckers = disabledTagCheckers;
  }

  /**
   * <p>
   * Determine if a tag checker is enabled:
   * <ul>
   * <li>When {@link #enabledTagCheckers} is not set (null or empty), or</li>
   * <li>When the specified tag checker is listed in {@link #enabledTagCheckers}
   * </li>
   * </ul>
   * 
   * A tag checker can be be listed by its:
   * <ul>
   * <li>TagChecker.getClass().getName(), or</li>
   * <li>TagChecker.getClass().getSimpleName()</li>
   * </ul>
   * </p>
   * 
   * @param tagChecker the tag checker
   * @return true when the specified tag checker is enabled
   */
  public boolean isTagCheckerEnabled(TagChecker tagChecker) {
    if (tagChecker == null) {
      return false;
    }

    return (enabledTagCheckers == null) || (enabledTagCheckers.size() == 0)
        || enabledTagCheckers.contains(tagChecker.getClass().getSimpleName())
        || enabledTagCheckers.contains(tagChecker.getClass().getName());
  }

  /**
   * <p>
   * Determine if a tag checker is disabled:
   * <ul>
   * <li>When {@link #disabledTagCheckers} is set (not null and not empty), and</li>
   * <li>When the specified tag checker is listed in
   * {@link #disabledTagCheckers}</li>
   * </ul>
   * 
   * A tag checker can be be listed by its:
   * <ul>
   * <li>TagChecker.getClass().getName(), or</li>
   * <li>TagChecker.getClass().getSimpleName()</li>
   * </ul>
   * </p>
   * 
   * @param tagChecker the tag checker
   * @return true when the specified tag checker is disabled
   */
  public boolean isTagCheckerDisabled(TagChecker tagChecker) {
    if (tagChecker == null) {
      return false;
    }

    return (disabledTagCheckers != null)
        && (disabledTagCheckers.size() != 0)
        && (disabledTagCheckers.contains(tagChecker.getClass().getSimpleName()) || disabledTagCheckers
            .contains(tagChecker.getClass().getName()));
  }
}
