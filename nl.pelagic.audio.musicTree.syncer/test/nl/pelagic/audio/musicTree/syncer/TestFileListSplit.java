package nl.pelagic.audio.musicTree.syncer;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import nl.pelagic.audio.musicTree.configuration.api.MusicTreeConstants;

import org.junit.Test;

@SuppressWarnings({
    "static-method", "javadoc", "nls"
})
public class TestFileListSplit {
  private static final File testDir = new File("testresources/testdata");
  private static final File testDirFileListSplit = new File(testDir, "FileListSplit");

  @Test
  public void testFileListSplit_Defaults() {
    FileListSplit fileListSplit = new FileListSplit(null, null, null);

    int musicFilesCount = 0;
    assertThat(fileListSplit.directory, nullValue());
    assertThat(Boolean.valueOf(fileListSplit.noDirectoryFiles), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(fileListSplit.directories.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(fileListSplit.musicFiles.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesWithoutExtensions.size()),
        equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesExtensions.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.covers.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(fileListSplit.otherFiles.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testFileListSplit_OnlyDir() {
    File dir = new File(".");

    FileListSplit fileListSplit = new FileListSplit(dir, null, null);

    int musicFilesCount = 0;
    assertThat(fileListSplit.directory, equalTo(dir));
    assertThat(Boolean.valueOf(fileListSplit.noDirectoryFiles), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(fileListSplit.directories.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(fileListSplit.musicFiles.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesWithoutExtensions.size()),
        equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesExtensions.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.covers.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(fileListSplit.otherFiles.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testFileListSplit_DirWithZeroFiles() {
    File dir = new File(".");

    FileListSplit fileListSplit = new FileListSplit(dir, new String[0], null);

    int musicFilesCount = 0;
    assertThat(fileListSplit.directory, equalTo(dir));
    assertThat(Boolean.valueOf(fileListSplit.noDirectoryFiles), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(fileListSplit.directories.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(fileListSplit.musicFiles.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesWithoutExtensions.size()),
        equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesExtensions.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.covers.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(fileListSplit.otherFiles.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testFileListSplit_DirWithSingleNullFile() {
    File dir = new File(".");

    FileListSplit fileListSplit = new FileListSplit(dir, new String[1], null);

    int musicFilesCount = 0;
    assertThat(fileListSplit.directory, equalTo(dir));
    assertThat(Boolean.valueOf(fileListSplit.noDirectoryFiles), equalTo(Boolean.TRUE));
    assertThat(Integer.valueOf(fileListSplit.directories.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(fileListSplit.musicFiles.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesWithoutExtensions.size()),
        equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesExtensions.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.covers.size()), equalTo(Integer.valueOf(0)));
    assertThat(Integer.valueOf(fileListSplit.otherFiles.size()), equalTo(Integer.valueOf(0)));
  }

  @Test
  public void testFileListSplit_NoExtension() {
    FileListSplit fileListSplit = new FileListSplit(testDirFileListSplit, testDirFileListSplit.list(), null);

    int directoriesCount = 2;
    int musicFilesCount = 0;
    int coversCount = 1;
    int otherCount = 8;
    assertThat(fileListSplit.directory, equalTo(testDirFileListSplit));
    assertThat(Boolean.valueOf(fileListSplit.noDirectoryFiles), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(fileListSplit.directories.size()), equalTo(Integer.valueOf(directoriesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFiles.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesWithoutExtensions.size()),
        equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesExtensions.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.covers.size()), equalTo(Integer.valueOf(coversCount)));
    assertThat(Integer.valueOf(fileListSplit.otherFiles.size()), equalTo(Integer.valueOf(otherCount)));

    List<String> covers = new LinkedList<>();
    List<String> directories = new LinkedList<>();
    List<String> musicFiles = new LinkedList<>();
    List<String> musicFilesExtensions = new LinkedList<>();
    List<String> musicFilesWithoutExtensions = new LinkedList<>();
    List<String> otherFiles = new LinkedList<>();

    covers.add(MusicTreeConstants.COVER);
    directories.add("dummydir1");
    directories.add("dummydir2");
    otherFiles.add("dummy1.flac");
    otherFiles.add("dummy1.mp3");
    otherFiles.add("dummy1.other");
    otherFiles.add("dummy1.txt");
    otherFiles.add("dummy2.flac");
    otherFiles.add("dummy2.mp3");
    otherFiles.add("dummy2.other");
    otherFiles.add("dummy2.txt");

    assertThat(fileListSplit.covers, equalTo(covers));
    assertThat(fileListSplit.directories, equalTo(directories));
    assertThat(fileListSplit.musicFiles, equalTo(musicFiles));
    assertThat(fileListSplit.musicFilesExtensions, equalTo(musicFilesExtensions));
    assertThat(fileListSplit.musicFilesWithoutExtensions, equalTo(musicFilesWithoutExtensions));
    assertThat(fileListSplit.otherFiles, equalTo(otherFiles));
  }

  @Test
  public void testFileListSplit_Normal() {
    FileListSplit fileListSplit =
        new FileListSplit(testDirFileListSplit, testDirFileListSplit.list(), MusicTreeConstants.FLACEXTENSION);

    int directoriesCount = 2;
    int musicFilesCount = 2;
    int coversCount = 1;
    int otherCount = 6;
    assertThat(fileListSplit.directory, equalTo(testDirFileListSplit));
    assertThat(Boolean.valueOf(fileListSplit.noDirectoryFiles), equalTo(Boolean.FALSE));
    assertThat(Integer.valueOf(fileListSplit.directories.size()), equalTo(Integer.valueOf(directoriesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFiles.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesWithoutExtensions.size()),
        equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.musicFilesExtensions.size()), equalTo(Integer.valueOf(musicFilesCount)));
    assertThat(Integer.valueOf(fileListSplit.covers.size()), equalTo(Integer.valueOf(coversCount)));
    assertThat(Integer.valueOf(fileListSplit.otherFiles.size()), equalTo(Integer.valueOf(otherCount)));

    List<String> covers = new LinkedList<>();
    List<String> directories = new LinkedList<>();
    List<String> musicFiles = new LinkedList<>();
    List<String> musicFilesExtensions = new LinkedList<>();
    List<String> musicFilesWithoutExtensions = new LinkedList<>();
    List<String> otherFiles = new LinkedList<>();

    covers.add(MusicTreeConstants.COVER);
    directories.add("dummydir1");
    directories.add("dummydir2");
    musicFiles.add("dummy1.flac");
    musicFiles.add("dummy2.flac");
    musicFilesExtensions.add(".flac");
    musicFilesExtensions.add(".flac");
    musicFilesWithoutExtensions.add("dummy1");
    musicFilesWithoutExtensions.add("dummy2");
    otherFiles.add("dummy1.mp3");
    otherFiles.add("dummy1.other");
    otherFiles.add("dummy1.txt");
    otherFiles.add("dummy2.mp3");
    otherFiles.add("dummy2.other");
    otherFiles.add("dummy2.txt");

    assertThat(fileListSplit.covers, equalTo(covers));
    assertThat(fileListSplit.directories, equalTo(directories));
    assertThat(fileListSplit.musicFiles, equalTo(musicFiles));
    assertThat(fileListSplit.musicFilesExtensions, equalTo(musicFilesExtensions));
    assertThat(fileListSplit.musicFilesWithoutExtensions, equalTo(musicFilesWithoutExtensions));
    assertThat(fileListSplit.otherFiles, equalTo(otherFiles));
  }
}
