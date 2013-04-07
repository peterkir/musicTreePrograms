package nl.pelagic.util.file;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import nl.pelagic.util.TestConstants;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method"
})
public class TestDirUtils {
  @Test
  public void testMkdir_Null() throws FileAlreadyExistsException {
    Boolean result = Boolean.valueOf(DirUtils.mkdir(null));
    assertThat(result, equalTo(Boolean.FALSE));
  }

  @Test
  public void testMkdir_DirExists() throws FileAlreadyExistsException {
    File dst = TestConstants.tmpTestBaseDir;
    Long lm = Long.valueOf(dst.lastModified());
    assertThat(Boolean.valueOf(dst.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(dst.isDirectory()), equalTo(Boolean.TRUE));

    Boolean result = Boolean.valueOf(DirUtils.mkdir(dst));
    assertThat(result, equalTo(Boolean.TRUE));

    /* dst was not touched (already exists) */
    assertThat(Long.valueOf(dst.lastModified()), equalTo(lm));
  }

  @Test(expected = FileAlreadyExistsException.class)
  public void testMkdir_FileExists() throws IOException, FileAlreadyExistsException {
    File dst = File.createTempFile("testMkdir_FileExists.", ".tmp", TestConstants.tmpTestBaseDir); //$NON-NLS-1$ //$NON-NLS-2$
    dst.deleteOnExit();
    assertThat(Boolean.valueOf(dst.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(dst.isDirectory()), equalTo(Boolean.FALSE));

    DirUtils.mkdir(dst);
    assert (false);
  }

  @Test
  public void testMkdir_DirDoesNotExist() throws FileAlreadyExistsException {
    File dst = new File(TestConstants.tmpTestBaseDir, "testMkdir_DirDoesNotExist"); //$NON-NLS-1$
    Long lm = Long.valueOf(dst.lastModified());
    dst.deleteOnExit();
    assertThat(Boolean.valueOf(dst.exists()), equalTo(Boolean.FALSE));

    Boolean result = Boolean.valueOf(DirUtils.mkdir(dst));
    assertThat(result, equalTo(Boolean.TRUE));

    /* dst was created */
    assertThat(Boolean.valueOf(dst.exists()), equalTo(Boolean.TRUE));
    assertThat(Boolean.valueOf(dst.isDirectory()), equalTo(Boolean.TRUE));
    assertThat(Long.valueOf(dst.lastModified()), not(equalTo(lm)));
  }
}
