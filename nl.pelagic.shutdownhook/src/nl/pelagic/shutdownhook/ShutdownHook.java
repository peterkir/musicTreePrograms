package nl.pelagic.shutdownhook;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import nl.pelagic.shutdownhook.api.ShutdownHookParticipant;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

/**
 * This bundle registers itself as a JVM shutdown hook: when the JVM shuts down,
 * it calls the handler in this bundle, which again calls all shutdown hook
 * participants, so that they call stop running in a controlled fashion.
 */
@Component
public class ShutdownHook extends Thread {
  /** the registered shutdown hook participants */
  private Set<ShutdownHookParticipant> shutdownHookParticipants = new CopyOnWriteArraySet<>();

  /**
   * @param shutdownHookParticipant the shutdown hook participant to add
   */
  @Reference(type = '+')
  void addShutdownHookParticipant(ShutdownHookParticipant shutdownHookParticipant) {
    shutdownHookParticipants.add(shutdownHookParticipant);
  }

  /**
   * @param shutdownHookParticipant the shutdown hook participant to remove
   */
  void removeShutdownHookParticipant(ShutdownHookParticipant shutdownHookParticipant) {
    shutdownHookParticipants.remove(shutdownHookParticipant);
  }

  /** the stop handler for the HUP signal */
  private StopHandler stopHandler_HUP = new StopHandler("HUP", this); //$NON-NLS-1$

  /** the stop handler for the INT signal */
  private StopHandler stopHandler_INT = new StopHandler("INT", this); //$NON-NLS-1$

  /** the stop handler for the TERM signal */
  private StopHandler stopHandler_TERM = new StopHandler("TERM", this); //$NON-NLS-1$

  /**
   * Bundle activator
   */
  @Activate
  public void activate() {
    Runtime.getRuntime().addShutdownHook(this);
    stopHandler_HUP.activate();
    stopHandler_INT.activate();
    stopHandler_TERM.activate();
  }

  /**
   * Bundle deactivator
   */
  @Deactivate
  public void deactivate() {
    stopHandler_TERM.deactivate();
    stopHandler_INT.deactivate();
    stopHandler_HUP.deactivate();
    try {
      Runtime.getRuntime().removeShutdownHook(this);
    }
    catch (Throwable e) {
      /*
       * swallow: this can throw 'java.lang.IllegalStateException: Shutdown in
       * progress'
       */
    }
  }

  @Override
  public void run() {
    /** loop over all registered shutdown hook participants */
    for (ShutdownHookParticipant shutdownHookParticipant : this.shutdownHookParticipants) {
      try {
        shutdownHookParticipant.shutdownHook();
      }
      catch (Throwable e) {
        /* swallow */
      }
    }
  }
}
