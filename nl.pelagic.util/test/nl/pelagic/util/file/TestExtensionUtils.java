package nl.pelagic.util.file;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method"
})
public class TestExtensionUtils {
  @Test
  public void testSplit_Null() {
    String[] result = ExtensionUtils.split(null, false);
    assertThat(result[0], equalTo(null));
    assertThat(result[1], equalTo(ExtensionUtils.emptyString));

    result = ExtensionUtils.split(null, true);
    assertThat(result[0], equalTo(null));
    assertThat(result[1], equalTo(ExtensionUtils.emptyString));
  }

  @Test
  public void testSplit_Empty() {
    String[] result = ExtensionUtils.split("", false); //$NON-NLS-1$
    assertThat(result[0], equalTo("")); //$NON-NLS-1$
    assertThat(result[1], equalTo(ExtensionUtils.emptyString));

    result = ExtensionUtils.split("", true); //$NON-NLS-1$
    assertThat(result[0], equalTo("")); //$NON-NLS-1$
    assertThat(result[1], equalTo(ExtensionUtils.emptyString));
  }

  @Test
  public void testSplit_NoExtension() {
    String filename = "file without extension"; //$NON-NLS-1$
    String[] result = ExtensionUtils.split(filename, false);
    assertThat(result[0], equalTo(filename));
    assertThat(result[1], equalTo(ExtensionUtils.emptyString));

    result = ExtensionUtils.split(filename, true);
    assertThat(result[0], equalTo(filename));
    assertThat(result[1], equalTo(ExtensionUtils.emptyString));
  }

  @Test
  public void testSplit_EmptyExtension() {
    String filename = "file with empty extension."; //$NON-NLS-1$
    String[] result = ExtensionUtils.split(filename, false);
    assertThat(result[0], equalTo(filename.subSequence(0, filename.length() - 1)));
    assertThat(result[1], equalTo(ExtensionUtils.emptyString));

    result = ExtensionUtils.split(filename, true);
    assertThat(result[0], equalTo(filename.subSequence(0, filename.length() - 1)));
    assertThat(result[1], equalTo(".")); //$NON-NLS-1$
  }

  @Test
  public void testSplit_OneExtension() {
    String file = "file with one"; //$NON-NLS-1$
    String extension = "extension"; //$NON-NLS-1$
    String filename = file + "." + extension; //$NON-NLS-1$
    String[] result = ExtensionUtils.split(filename, false);
    assertThat(result[0], equalTo(file));
    assertThat(result[1], equalTo(extension));

    result = ExtensionUtils.split(filename, true);
    assertThat(result[0], equalTo(file));
    assertThat(result[1], equalTo("." + extension)); //$NON-NLS-1$
  }

  @Test
  public void testSplit_MultipleExtensions() {
    String file = "file with.two"; //$NON-NLS-1$
    String extension = "extensions"; //$NON-NLS-1$
    String filename = file + "." + extension; //$NON-NLS-1$
    String[] result = ExtensionUtils.split(filename, false);
    assertThat(result[0], equalTo(file));
    assertThat(result[1], equalTo(extension));

    result = ExtensionUtils.split(filename, true);
    assertThat(result[0], equalTo(file));
    assertThat(result[1], equalTo("." + extension)); //$NON-NLS-1$
  }

  @Test
  public void testReplaceExtension_Null() {
    String newext = ".something"; //$NON-NLS-1$

    String result = ExtensionUtils.replaceExtension(null, null);
    assertThat(result, equalTo(null));

    result = ExtensionUtils.replaceExtension(null, ""); //$NON-NLS-1$
    assertThat(result, equalTo(null));

    result = ExtensionUtils.replaceExtension(null, newext);
    assertThat(result, equalTo(null));
  }

  @Test
  public void testReplaceExtension_Empty() {
    String file = ""; //$NON-NLS-1$
    String newext = ".something"; //$NON-NLS-1$

    String result = ExtensionUtils.replaceExtension(file, null);
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(file, ""); //$NON-NLS-1$
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(file, newext);
    assertThat(result, equalTo("")); //$NON-NLS-1$
  }

  @Test
  public void testReplaceExtension_NoExtension() {
    String file = "filename"; //$NON-NLS-1$
    String newext = ".something"; //$NON-NLS-1$

    String result = ExtensionUtils.replaceExtension(file, null);
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(file, ""); //$NON-NLS-1$
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(file, newext);
    assertThat(result, equalTo(file));
  }

  @Test
  public void testReplaceExtension_EmptyExtension() {
    String file = "filename"; //$NON-NLS-1$
    String ext = ""; //$NON-NLS-1$
    String newext = ".something"; //$NON-NLS-1$
    String filename = file + "." + ext; //$NON-NLS-1$

    String result = ExtensionUtils.replaceExtension(filename, null);
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(filename, ""); //$NON-NLS-1$
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(filename, newext);
    assertThat(result, equalTo(file + newext));
  }

  @Test
  public void testReplaceExtension_OneExtension() {
    String file = "filename"; //$NON-NLS-1$
    String ext = "flac"; //$NON-NLS-1$
    String newext = ".something"; //$NON-NLS-1$
    String filename = file + "." + ext; //$NON-NLS-1$

    String result = ExtensionUtils.replaceExtension(filename, null);
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(filename, ""); //$NON-NLS-1$
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(filename, newext);
    assertThat(result, equalTo(file + newext));
  }

  @Test
  public void testReplaceExtension_MultipleExtensions() {
    String file = "filename.ext"; //$NON-NLS-1$
    String ext = "flac"; //$NON-NLS-1$
    String newext = ".something"; //$NON-NLS-1$
    String filename = file + "." + ext; //$NON-NLS-1$

    String result = ExtensionUtils.replaceExtension(filename, null);
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(filename, ""); //$NON-NLS-1$
    assertThat(result, equalTo(file));

    result = ExtensionUtils.replaceExtension(filename, newext);
    assertThat(result, equalTo(file + newext));
  }
}
