package nl.pelagic.util.file;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;

import nl.pelagic.util.TestConstants;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "nls"
})
public class TestFilenameFilterWithRegex {

  private String[][] checks = {

      /* / */

      {
          "/", "name"
      },

      /* /base */

      {
          "/", "base"
      }, {
          "/base", "name"
      }, {
          "/base", "path"
      }, {
          "/base/path", "name"
      }, {
          "/base/path", "subdir"
      }, {
          "/base/path/subdir", "name"
      },

      /* /base1 */

      {
          "/", "base1"
      }, {
          "/base1", "name"
      }, {
          "/base1", "path1"
      }, {
          "/base1/path1", "name"
      }, {
          "/base1/path1", "subdir1"
      }, {
          "/base1/path1/subdir1", "name"
      },

      /* /base/path1 */

      {
          "/base", "path1"
      }, {
          "/base/path1", "name"
      }, {
          "/base/path1", "subdir1"
      }, {
          "/base/path1/subdir1", "name"
      },

      /* /base/path/subdir1 */

      {
          "/base/path", "subdir1"
      }, {
          "/base/path/subdir1", "name"
      }
  };

  private void checkResults(FilenameFilterWithRegex filter, boolean[] results) {
    assert (checks.length == results.length);
    for (int j = 0; j < checks.length; j++) {
      String[] check = checks[j];
      assert (check.length == 2);

      assertThat(String.format("check = %s, %s, %s", check[0], check[1], Boolean.valueOf(results[j])),
          Boolean.valueOf(filter.accept(new File(TestConstants.tmpTestBaseDir3, check[0]), check[1])),
          equalTo(Boolean.valueOf(results[j])));
    }
  }

  @Test
  public void testFilenameFilterWithRegex_A() throws IOException {
    FilenameFilterWithRegex filter = new FilenameFilterWithRegex(null, null);

    boolean[] results = {
        /* / */
        true,

        /* /base */
        true, true, true, true, true, true,

        /* /base1 */
        true, true, true, true, true, true,

        /* /base/path1 */
        true, true, true, true,

        /* /base/path/subdir1 */
        true, true
    };

    checkResults(filter, results);
  }

  @Test
  public void testFilenameFilterWithRegex_B() throws IOException {
    FilenameFilterWithRegex filter = new FilenameFilterWithRegex(null, Pattern.compile("^[ns].*$"));

    boolean[] results = {
        /* / */
        true,

        /* /base */
        false, true, false, true, true, true,

        /* /base1 */
        false, true, false, true, true, true,

        /* /base/path1 */
        false, true, true, true,

        /* /base/path/subdir1 */
        true, true
    };

    checkResults(filter, results);
  }

  @Test
  public void testFilenameFilterWithRegex_C() throws IOException {
    FilenameFilterWithRegex filter =
        new FilenameFilterWithRegex(new File(TestConstants.tmpTestBaseDir3, "/base/path"), null);

    boolean[] results = {
        /* / */
        false,

        /* /base */
        false, false, true, true, true, true,

        /* /base1 */
        false, false, false, false, false, false,

        /* /base/path1 */
        false, false, false, false,

        /* /base/path/subdir1 */
        true, true
    };

    checkResults(filter, results);
  }

  @Test
  public void testFilenameFilterWithRegex_D() throws IOException {
    FilenameFilterWithRegex filter =
        new FilenameFilterWithRegex(new File(TestConstants.tmpTestBaseDir3, "/base/path"), Pattern.compile("^[ns].*$"));

    boolean[] results = {
        /* / */
        false,

        /* /base */
        false, false, false, true, true, true,

        /* /base1 */
        false, false, false, false, false, false,

        /* /base/path1 */
        false, false, false, false,

        /* /base/path/subdir1 */
        true, true
    };

    checkResults(filter, results);
  }
}
