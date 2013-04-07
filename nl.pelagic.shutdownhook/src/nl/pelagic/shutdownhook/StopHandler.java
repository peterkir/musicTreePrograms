package nl.pelagic.shutdownhook;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Handler for stop signals coming in from the OS
 */
public class StopHandler implements SignalHandler {
  /** the signal to handle */
  private Signal signal = null;

  /** the shutdown hook to call */
  private ShutdownHook shutdownHook = null;

  /** the original signal handler */
  private SignalHandler oldHandler = null;

  /**
   * Constructor
   * 
   * @param signal the signal to handle
   * @param shutdownHook the shutdown hook to run when the signal comes in
   */
  public StopHandler(String signal, ShutdownHook shutdownHook) {
    assert ((signal != null) && !signal.isEmpty());
    assert (shutdownHook != null);

    this.signal = new Signal(signal);
    this.shutdownHook = shutdownHook;
  }

  /**
   * Bundle activator
   */
  public void activate() {
    if (this.signal != null) {
      this.oldHandler = Signal.handle(this.signal, this);
    }
  }

  /**
   * Bundle deactivator
   */
  public void deactivate() {
    if (this.signal != null) {
      Signal.handle(this.signal, this.oldHandler);
    }
  }

  @Override
  public void handle(Signal signal) {
    if (signal.equals(this.signal)) {
      shutdownHook.run();
    }

    /* Chain back to previous handler, if one exists */
    if ((this.oldHandler != null) && (this.oldHandler != SIG_DFL) && (this.oldHandler != SIG_IGN)) {
      try {
        this.oldHandler.handle(signal);
      }
      catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}
