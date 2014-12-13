package nl.pelagic.shutdownhook;

import sun.misc.Signal;
import sun.misc.SignalHandler;

/**
 * Handler for stop signals received from the OS
 */
public class StopHandler implements SignalHandler {
  /** The signal to handle */
  private Signal signal = null;

  /** The shutdown hook to call when the signal is received */
  private ShutdownHook shutdownHook = null;

  /** The original signal handler */
  private SignalHandler oldHandler = null;

  /**
   * Constructor
   *
   * @param signal The signal to handle
   * @param shutdownHook The shutdown hook to run when the signal is received
   * @throws ExceptionInInitializerError when signal is null or when
   *           shutdownHook is null
   */
  public StopHandler(Signal signal, ShutdownHook shutdownHook) throws ExceptionInInitializerError {
    if ((signal == null) || (shutdownHook == null)) {
      throw new ExceptionInInitializerError(String.format( //
          "Null parameter(s):%s%s", //
          (signal == null) ? " signal" : "", //
          (shutdownHook == null) ? " shutdownHook" : ""));
    }

    this.signal = signal;
    this.shutdownHook = shutdownHook;
  }

  /**
   * Bundle activator
   */
  public void activate() {
    if (this.oldHandler == null) {
      this.oldHandler = Signal.handle(this.signal, this);
      assert (this.oldHandler != null);
    }
  }

  /**
   * Bundle deactivator
   */
  public void deactivate() {
    if (this.oldHandler != null) {
      Signal.handle(this.signal, this.oldHandler);
      this.oldHandler = null;
    }
  }

  @Override
  public void handle(Signal signal) {
    if (signal.equals(this.signal)) {
      shutdownHook.stopAllShutdownHookParticipants();
    }

    /* chain back to previous handler, if one exists */
    if ((this.oldHandler != SIG_DFL) //
        && (this.oldHandler != SIG_IGN)) {
      try {
        this.oldHandler.handle(signal);
      }
      catch (Throwable e) {
        /* swallow */
      }
    }
  }
}