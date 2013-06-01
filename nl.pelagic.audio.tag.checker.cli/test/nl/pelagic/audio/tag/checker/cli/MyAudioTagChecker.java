package nl.pelagic.audio.tag.checker.cli;

import java.io.IOException;

import nl.pelagic.audio.tag.checker.api.AudioTagChecker;
import nl.pelagic.audio.tag.checker.api.AudioTagCheckerCallback;
import nl.pelagic.audio.tag.checker.types.AudioTagCheckerConfiguration;
import nl.pelagic.shutdownhook.api.ShutdownHookParticipant;

import org.junit.Ignore;

@Ignore
@SuppressWarnings({
    "nls", "javadoc"
})
public class MyAudioTagChecker implements AudioTagChecker {

  boolean[] checkResults = {
    true
  };
  boolean[] checkExceptions = {
    false
  };
  boolean[] callShutdownHook = {
    false
  };
  int index = 0;

  ShutdownHookParticipant main = null;

  @Override
  public boolean check(AudioTagCheckerConfiguration configuration, AudioTagCheckerCallback callback) throws IOException {
    if (checkExceptions[index]) {
      index++;
      throw new IOException("test");
    }

    if (callShutdownHook[index]) {
      main.shutdownHook();
    }

    return checkResults[index++];
  }

}
