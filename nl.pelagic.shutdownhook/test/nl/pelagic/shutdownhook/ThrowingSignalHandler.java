package nl.pelagic.shutdownhook;

import org.junit.Ignore;

import sun.misc.Signal;
import sun.misc.SignalHandler;

@Ignore
@SuppressWarnings({
    "javadoc", "restriction"
})
public class ThrowingSignalHandler implements SignalHandler {
  @Override
  public void handle(Signal sig) {
    throw new RuntimeException();
  }
}