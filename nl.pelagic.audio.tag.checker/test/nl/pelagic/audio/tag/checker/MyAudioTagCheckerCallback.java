package nl.pelagic.audio.tag.checker;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import nl.pelagic.audio.tag.checker.api.AudioTagCheckerCallback;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.shutdownhook.api.ShutdownHookParticipant;

import org.jaudiotagger.tag.Tag;
import org.junit.Ignore;

@Ignore
@SuppressWarnings("javadoc")
public class MyAudioTagCheckerCallback implements AudioTagCheckerCallback {
  public List<File> unsupportedExtensions = new LinkedList<>();
  public List<File> notReadables = new LinkedList<>();
  public List<File> noTags = new LinkedList<>();
  public List<File> tagNotConverteds = new LinkedList<>();
  public List<GenericTag> checksFaileds = new LinkedList<>();
  public List<GenericTag> checksPasseds = new LinkedList<>();

  public ShutdownHookParticipant atc = null;
  public int shutdownAfterNumberOfCalls = 0;

  @Override
  public void unsupportedExtension(File file) {
    unsupportedExtensions.add(file);
  }

  @Override
  public void notReadable(File file, Exception e) {
    notReadables.add(file);
  }

  @Override
  public void noTag(File file) {
    noTags.add(file);
  }

  @Override
  public void tagNotConverted(File file, Tag tag) {
    tagNotConverteds.add(file);
    if (atc != null) {
      shutdownAfterNumberOfCalls--;
      if (shutdownAfterNumberOfCalls <= 0) {
        atc.shutdownHook();
      }
    }
  }

  @Override
  public void checksFailed(GenericTag tag) {
    checksFaileds.add(tag);
  }

  @Override
  public void checksPassed(GenericTag tag) {
    checksPasseds.add(tag);
  }
}
