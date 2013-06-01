package nl.pelagic.audio.tag.checker.types;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import nl.pelagic.audio.tag.checker.api.TagChecker;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings({
    "nls", "javadoc"
})
public class TestAudioTagCheckerConfiguration {

  private AudioTagCheckerConfiguration config = null;

  @Before
  public void setUp() {
    config = new AudioTagCheckerConfiguration();
  }

  @After
  public void tearDown() {
    config = null;
  }

  @Test
  public void testAudioTagCheckerConfiguration_default() {
    assertThat(Boolean.valueOf(config.isRecursiveScan()),
        equalTo(Boolean.valueOf(AudioTagCheckerConfiguration.DEFAULT_RECURSIVESCAN)));
    assertThat(Boolean.valueOf(config.isRegexInAllDirs()),
        equalTo(Boolean.valueOf(AudioTagCheckerConfiguration.DEFAULT_REGEXINALLDIRS)));
    assertThat(config.getRegexPattern(), nullValue());
    assertThat(config.getCheckPath(), nullValue());
    assertThat(config.getEnabledTagCheckers(), nullValue());
    assertThat(config.getDisabledTagCheckers(), nullValue());
  }

  @Test
  public void testAudioTagCheckerConfiguration_checkPath() {
    File checkPath = new File("/some/path");
    config = new AudioTagCheckerConfiguration(checkPath);

    assertThat(Boolean.valueOf(config.isRecursiveScan()),
        equalTo(Boolean.valueOf(AudioTagCheckerConfiguration.DEFAULT_RECURSIVESCAN)));
    assertThat(Boolean.valueOf(config.isRegexInAllDirs()),
        equalTo(Boolean.valueOf(AudioTagCheckerConfiguration.DEFAULT_REGEXINALLDIRS)));
    assertThat(config.getRegexPattern(), nullValue());
    assertThat(config.getCheckPath(), equalTo(checkPath));
    assertThat(config.getEnabledTagCheckers(), nullValue());
    assertThat(config.getDisabledTagCheckers(), nullValue());
  }

  @Test
  public void testSetRecursiveScan() {
    assertThat(Boolean.valueOf(config.isRecursiveScan()),
        equalTo(Boolean.valueOf(AudioTagCheckerConfiguration.DEFAULT_RECURSIVESCAN)));
    config.setRecursiveScan(!AudioTagCheckerConfiguration.DEFAULT_RECURSIVESCAN);
    assertThat(Boolean.valueOf(config.isRecursiveScan()),
        equalTo(Boolean.valueOf(!AudioTagCheckerConfiguration.DEFAULT_RECURSIVESCAN)));
  }

  @Test
  public void testSetRegexInAllDirs() {
    assertThat(Boolean.valueOf(config.isRegexInAllDirs()),
        equalTo(Boolean.valueOf(AudioTagCheckerConfiguration.DEFAULT_REGEXINALLDIRS)));
    config.setRegexInAllDirs(!AudioTagCheckerConfiguration.DEFAULT_REGEXINALLDIRS);
    assertThat(Boolean.valueOf(config.isRegexInAllDirs()),
        equalTo(Boolean.valueOf(!AudioTagCheckerConfiguration.DEFAULT_REGEXINALLDIRS)));
  }

  @Test
  public void testSetRegexPattern() {
    assertThat(config.getRegexPattern(), nullValue());
    Pattern pattern = Pattern.compile("^.*$");
    config.setRegexPattern(pattern);
    assertThat(config.getRegexPattern(), equalTo(pattern));
  }

  @Test
  public void testSetCheckPath() {
    assertThat(config.getCheckPath(), nullValue());
    File checkPath = new File("/some/file");
    config.setCheckPath(checkPath);
    assertThat(config.getCheckPath(), equalTo(checkPath));
  }

  @Test
  public void testIsTagCheckerEnabled() {
    assertThat(config.getEnabledTagCheckers(), nullValue());

    assertThat(Boolean.valueOf(config.isTagCheckerEnabled(null)), equalTo(Boolean.FALSE));

    Set<String> checkers = new HashSet<>();
    TagChecker tagChecker = new DummyTagChecker();

    /* null */
    assertThat(Boolean.valueOf(config.isTagCheckerEnabled(tagChecker)), equalTo(Boolean.TRUE));

    /* size == 0 */
    checkers.clear();
    config.setEnabledTagCheckers(checkers);
    assertThat(Boolean.valueOf(config.isTagCheckerEnabled(tagChecker)), equalTo(Boolean.TRUE));

    /* contains(tagChecker.getClass().getSimpleName()) */
    checkers.clear();
    checkers.add(tagChecker.getClass().getSimpleName());
    assertThat(Boolean.valueOf(config.isTagCheckerEnabled(tagChecker)), equalTo(Boolean.TRUE));

    /* contains(tagChecker.getClass().getName()) */
    checkers.clear();
    checkers.add(tagChecker.getClass().getName());
    assertThat(Boolean.valueOf(config.isTagCheckerEnabled(tagChecker)), equalTo(Boolean.TRUE));

    /* not contains(tagChecker.getClass().getSimpleName()) */
    checkers.clear();
    checkers.add(DummyTagChecker2.class.getSimpleName());
    assertThat(Boolean.valueOf(config.isTagCheckerEnabled(tagChecker)), equalTo(Boolean.FALSE));

    /* not contains(tagChecker.getClass().getName()) */
    checkers.clear();
    checkers.add(DummyTagChecker2.class.getName());
    assertThat(Boolean.valueOf(config.isTagCheckerEnabled(tagChecker)), equalTo(Boolean.FALSE));
  }

  @Test
  public void testIsTagCheckerDisabled() {
    assertThat(config.getDisabledTagCheckers(), nullValue());

    assertThat(Boolean.valueOf(config.isTagCheckerDisabled(null)), equalTo(Boolean.FALSE));

    Set<String> checkers = new HashSet<>();
    TagChecker tagChecker = new DummyTagChecker();

    /* null */
    assertThat(Boolean.valueOf(config.isTagCheckerDisabled(tagChecker)), equalTo(Boolean.FALSE));

    /* size == 0 */
    checkers.clear();
    config.setDisabledTagCheckers(checkers);
    assertThat(Boolean.valueOf(config.isTagCheckerDisabled(tagChecker)), equalTo(Boolean.FALSE));

    /* contains(tagChecker.getClass().getSimpleName()) */
    checkers.clear();
    checkers.add(tagChecker.getClass().getSimpleName());
    assertThat(Boolean.valueOf(config.isTagCheckerDisabled(tagChecker)), equalTo(Boolean.TRUE));

    /* contains(tagChecker.getClass().getName()) */
    checkers.clear();
    checkers.add(tagChecker.getClass().getName());
    assertThat(Boolean.valueOf(config.isTagCheckerDisabled(tagChecker)), equalTo(Boolean.TRUE));

    /* not contains(tagChecker.getClass().getSimpleName()) */
    checkers.clear();
    checkers.add(DummyTagChecker2.class.getSimpleName());
    assertThat(Boolean.valueOf(config.isTagCheckerDisabled(tagChecker)), equalTo(Boolean.FALSE));

    /* not contains(tagChecker.getClass().getName()) */
    checkers.clear();
    checkers.add(DummyTagChecker2.class.getName());
    assertThat(Boolean.valueOf(config.isTagCheckerDisabled(tagChecker)), equalTo(Boolean.FALSE));
  }
}
