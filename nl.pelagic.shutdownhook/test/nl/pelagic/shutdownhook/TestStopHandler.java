package nl.pelagic.shutdownhook;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import sun.misc.Signal;
import sun.misc.SignalHandler;

@SuppressWarnings({
    "nls", "unused", "javadoc", "static-method", "restriction"
})
public class TestStopHandler extends ShutdownHook implements SignalHandler {
  static final String SIGHUP_NAME = "USR2";
  static final Signal SIGHUP = new Signal(SIGHUP_NAME);

  @Before
  public void setUp() {
    handledSignals.clear();
    calls = 0;
  }

  /*
   * SignalHandler
   */

  List<Signal> handledSignals = new LinkedList<>();

  @Override
  public void handle(Signal sig) {
    handledSignals.add(sig);
  }

  /*
   * ShutdownHook
   */

  int calls = 0;

  @Override
  void stopAllShutdownHookParticipants() {
    calls++;
  }

  /*
   * Tests
   */

  @Test(expected = ExceptionInInitializerError.class)
  public void testConstructThrowSignal() {
    StopHandler handler = new StopHandler(null, this);
  }

  @Test(expected = ExceptionInInitializerError.class)
  public void testConstructThrowHookNull() {
    StopHandler handler = new StopHandler(SIGHUP_NAME, null);
  }

  @Test
  public void testActivateDeactivate() throws InterruptedException {
    assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(0)));

    StopHandler handler = new StopHandler(SIGHUP_NAME, this);
    handler.activate();
    handler.activate();

    assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(0)));

    Signal.raise(SIGHUP);

    while (calls == 0) {
      Thread.sleep(10);
    }

    assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(1)));

    calls = 0;

    Signal.raise(SIGHUP);
    Signal.raise(SIGHUP);

    while (calls == 0) {
      Thread.sleep(10);
    }

    assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(2)));

    handler.deactivate();
    handler.deactivate();
  }

  @Test
  public void testHandleSignal() {
    StopHandler handler = new StopHandler(SIGHUP_NAME, this);
    handler.activate();

    handler.handle(SIGHUP);
    assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(1)));
    assertThat(Integer.valueOf(handledSignals.size()), equalTo(Integer.valueOf(0)));

    handler.deactivate();
  }

  @Test
  public void testHandleDifferentSignal() {
    StopHandler handler = new StopHandler(SIGHUP_NAME, this);
    handler.activate();

    handler.handle(new Signal("INT"));
    assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(0)));

    handler.deactivate();
  }

  @Test
  public void testHandleChainToDflt() {
    StopHandler handler = new StopHandler(SIGHUP_NAME, this);

    SignalHandler oldHandler = Signal.handle(SIGHUP, SIG_DFL);
    try {
      handler.activate();

      handler.handle(SIGHUP);
      assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(1)));
      assertThat(Integer.valueOf(handledSignals.size()), equalTo(Integer.valueOf(0)));

      handler.deactivate();
    }
    finally {
      Signal.handle(SIGHUP, oldHandler);
    }
  }

  @Test
  public void testHandleChainToIgn() {
    StopHandler handler = new StopHandler(SIGHUP_NAME, this);

    SignalHandler oldHandler = Signal.handle(SIGHUP, SIG_IGN);
    try {
      handler.activate();

      handler.handle(SIGHUP);
      assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(1)));
      assertThat(Integer.valueOf(handledSignals.size()), equalTo(Integer.valueOf(0)));

      handler.deactivate();
    }
    finally {
      Signal.handle(SIGHUP, oldHandler);
    }
  }

  @Test
  public void testHandleChainToThis() {
    StopHandler handler = new StopHandler(SIGHUP_NAME, this);

    SignalHandler oldHandler = Signal.handle(SIGHUP, this);
    try {
      handler.activate();

      handler.handle(SIGHUP);
      assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(1)));
      assertThat(Integer.valueOf(handledSignals.size()), equalTo(Integer.valueOf(1)));

      handler.deactivate();
    }
    finally {
      Signal.handle(SIGHUP, oldHandler);
    }
  }

  @Test
  public void testHandleChainThrows() {
    ThrowingSignalHandler throwingSignalHandler = new ThrowingSignalHandler();
    SignalHandler oldHandler = Signal.handle(SIGHUP, throwingSignalHandler);

    StopHandler handler = new StopHandler(SIGHUP_NAME, this);
    try {
      handler.activate();

      handler.handle(SIGHUP);
      assertThat(Integer.valueOf(calls), equalTo(Integer.valueOf(1)));
      assertThat(Integer.valueOf(handledSignals.size()), equalTo(Integer.valueOf(0)));

      handler.deactivate();
    }
    finally {
      Signal.handle(SIGHUP, oldHandler);
    }
  }
}