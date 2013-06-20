package nl.pelagic.util.file;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

import nl.pelagic.util.TestConstants;

import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method", "nls"
})
public class TestFileUtils {
  @Test
  public void testIsFileBelowDirectory_Nulls() {
    boolean r = FileUtils.isFileBelowDirectory(null, null, true);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));

    r = FileUtils.isFileBelowDirectory(TestConstants.tmpTestBaseDir, null, true);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testIsFileBelowDirectory_Directory_DoesNotExist() {
    boolean r =
        FileUtils.isFileBelowDirectory(new File("somedirectorythatdoesnotexist"), new File(
            TestConstants.tmpTestBaseDir, ".gitignore"), true);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testIsFileBelowDirectory_Directory_IsFile() {
    boolean r = FileUtils.isFileBelowDirectory(new File(TestConstants.tmpTestBaseDir, ".gitignore"), null, true);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testIsFileBelowDirectory_Directory_NameAlike() {
    boolean r =
        FileUtils.isFileBelowDirectory(TestConstants.tmpTestBaseDir, new File(TestConstants.tmpTestBaseDir2,
            ".gitignore"), true);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testIsFileBelowDirectory_Directory_Normal_Same() {
    boolean r = FileUtils.isFileBelowDirectory(TestConstants.tmpTestBaseDir, TestConstants.tmpTestBaseDir, true);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    r = FileUtils.isFileBelowDirectory(TestConstants.tmpTestBaseDir, TestConstants.tmpTestBaseDir, false);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.FALSE));
  }

  @Test
  public void testIsFileBelowDirectory_Directory_Normal_Below() {
    boolean r =
        FileUtils.isFileBelowDirectory(TestConstants.tmpTestBaseDir, new File(TestConstants.tmpTestBaseDir,
            ".gitignore"), true);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
  }

  @Test
  public void testDeleteWithException_FileNotThere() throws IOException {
    FileUtils.deleteWithException(new File(TestConstants.tmpTestBaseDir, "dummy.not.there"));
  }

  /* too dangerous, only enable when you're really sure it is correct */
  @Ignore
  @Test(expected = IllegalArgumentException.class)
  public void testDeleteWithException_Root() throws IOException {
    FileUtils.deleteWithException(new File("/"));
  }

  @Test
  public void testDeleteWithException_File() throws IOException {
    File tmpFile = File.createTempFile("testDeleteWithException_File", ".tmp", TestConstants.tmpTestBaseDir);
    tmpFile.deleteOnExit();

    FileUtils.deleteWithException(tmpFile);

    assertThat(Boolean.valueOf(tmpFile.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testDeleteWithException_Directory() throws IOException {
    File tmpDir = new File(TestConstants.tmpTestBaseDir, "testDeleteWithException_Directory");
    tmpDir.mkdirs();

    FileUtils.deleteWithException(tmpDir);

    assertThat(Boolean.valueOf(tmpDir.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testDeleteWithException_DirectoryWithFile() throws IOException {
    File tmpDir = new File(TestConstants.tmpTestBaseDir, "testDeleteWithException_DirectoryWithFile");
    tmpDir.mkdirs();
    File tmpFile = File.createTempFile("testDeleteWithException_DirectoryWithFile", ".tmp", tmpDir);
    tmpFile.deleteOnExit();

    FileUtils.deleteWithException(tmpDir);

    assertThat(Boolean.valueOf(tmpFile.exists()), equalTo(Boolean.FALSE));
    assertThat(Boolean.valueOf(tmpDir.exists()), equalTo(Boolean.FALSE));
  }

  @Test
  public void testDelete_FileNotThere() {
    boolean r = FileUtils.delete(new File(TestConstants.tmpTestBaseDir, "dummy.not.there"));
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));
  }

  @Test
  public void testDelete_File() throws IOException {
    File tmpFile = File.createTempFile("testDelete_File", ".tmp", TestConstants.tmpTestBaseDir);
    tmpFile.deleteOnExit();

    boolean r = FileUtils.delete(tmpFile);
    assertThat(Boolean.valueOf(r), equalTo(Boolean.TRUE));

    assertThat(Boolean.valueOf(tmpFile.exists()), equalTo(Boolean.FALSE));
  }

  @Test(expected = FileNotFoundException.class)
  public void testCopy_FileNotThere() throws FileAlreadyExistsException, FileNotFoundException, IOException {
    FileUtils.copy(new File(TestConstants.tmpTestBaseDir, "dummy.not.there.src"), new File(
        TestConstants.tmpTestBaseDir, "dummy.not.there.dst"));
  }

  @Test(expected = FileAlreadyExistsException.class)
  public void testCopy_DstExists() throws FileAlreadyExistsException, FileNotFoundException, IOException {
    File srcFile = File.createTempFile("testCopy_DstExists", ".src", TestConstants.tmpTestBaseDir);
    srcFile.deleteOnExit();
    File dstFile = File.createTempFile("testCopy_DstExists", ".dst", TestConstants.tmpTestBaseDir);
    dstFile.deleteOnExit();

    FileUtils.copy(srcFile, new File(dstFile, "testCopy_DstExists.dst"));
  }

  @Test
  public void testCopy_File() throws FileAlreadyExistsException, FileNotFoundException, IOException {
    File srcFile = new File("bnd.bnd");
    File dstFile = File.createTempFile("testCopy_File", ".dst", TestConstants.tmpTestBaseDir);
    dstFile.deleteOnExit();

    FileUtils.copy(srcFile, dstFile);

    assertThat(Boolean.valueOf(dstFile.exists()), equalTo(Boolean.TRUE));
    assertThat(Long.valueOf(dstFile.length()), equalTo(Long.valueOf(srcFile.length())));
  }

  @Test
  public void testCopy_Directory() throws FileAlreadyExistsException, FileNotFoundException, IOException {
    File srcDir = TestConstants.tmpTestBaseDir;
    File dstDir = new File("test", "testCopy_Directory.dst");

    FileUtils.copy(srcDir, dstDir);

    try {
      File ignoreSrc = new File(TestConstants.tmpTestBaseDir, ".gitignore");
      File ignoreDst = new File(dstDir, ".gitignore");
      assertThat(Boolean.valueOf(ignoreDst.exists()), equalTo(Boolean.TRUE));
      assertThat(Long.valueOf(ignoreDst.length()), equalTo(Long.valueOf(ignoreSrc.length())));
    }
    finally {
      FileUtils.delete(dstDir);
    }
  }

  @Test
  public void testCopy_EmptyDirectory() throws FileAlreadyExistsException, FileNotFoundException, IOException {
    File srcDir = new File("test", "testCopy_EmptyDirectory.src");
    if (!srcDir.mkdirs()) {
      throw new IOException("could not create srcDir " + srcDir.getPath());
    }
    File dstDir = new File("test", "testCopy_EmptyDirectory.dst");

    FileUtils.copy(srcDir, dstDir);

    try {
      assertThat(Boolean.valueOf(dstDir.exists()), equalTo(Boolean.TRUE));
      String[] dstDirFiles = dstDir.list();
      assertThat(Integer.valueOf(dstDirFiles.length), equalTo(Integer.valueOf(0)));
    }
    finally {
      FileUtils.delete(srcDir);
      FileUtils.delete(dstDir);
    }
  }
}
