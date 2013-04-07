package nl.pelagic.audio.musicTree.syncer.testhelpers;

import java.io.File;
import java.util.Set;

import nl.pelagic.audio.conversion.flac2mp3.api.Flac2Mp3Configuration;
import nl.pelagic.audio.musicTree.syncer.api.Syncer;

import org.junit.Ignore;

@Ignore
@SuppressWarnings("javadoc")
public class MySyncer implements Syncer {
  public boolean returnValue = false;

  @Override
  public boolean syncFlac2Mp3(Flac2Mp3Configuration flac2Mp3Configuration, File flacDir, File mp3Dir,
      Set<String> extensionsList, Set<String> fileNamesList, boolean simulate) {
    return returnValue;
  }
}
