package nl.pelagic.shutdownhook;

import nl.pelagic.shutdownhook.api.ShutdownHookParticipant;

import org.junit.Ignore;

@Ignore
@SuppressWarnings("javadoc")
public class DummyShutdownHookParticipant implements ShutdownHookParticipant {

  public boolean throwException = false;
  public int callCount = 0;

  @Override
  public void shutdownHook() {
    callCount++;
    if (throwException) {
      throw new RuntimeException();
    }
  }
}
