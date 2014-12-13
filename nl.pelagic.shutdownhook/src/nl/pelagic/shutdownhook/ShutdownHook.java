package nl.pelagic.shutdownhook;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import nl.pelagic.shutdownhook.api.ShutdownHookParticipant;
import sun.misc.Signal;
import aQute.bnd.annotation.component.Activate;
import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Deactivate;
import aQute.bnd.annotation.component.Reference;

/**
 * <p>
 * This component registers itself as a VM shutdown hook handler service: when
 * the VM shuts down the handler in this bundle is called, which again calls all
 * {@link ShutdownHookParticipant}s, so that they can stop running in a
 * controlled fashion.
 * </p>
 * <p>
 * The component will install signal handlers for the HUP, INT and TERM signals.
 * When either of these signals is received, the
 * {@link ShutdownHookParticipant#shutdownHook()} method is invoked on all
 * {@link ShutdownHookParticipant}s.
 * </p>
 */
@Component
public class ShutdownHook extends Thread {
  /** The registered shutdown hook participants */
  private Set<ShutdownHookParticipant> shutdownHookParticipants = new CopyOnWriteArraySet<>();

  /**
   * @param shutdownHookParticipant The shutdown hook participant to add
   */
  @Reference(type = '+')
  void addShutdownHookParticipant(ShutdownHookParticipant shutdownHookParticipant) {
    shutdownHookParticipants.add(shutdownHookParticipant);
  }

  /**
   * @param shutdownHookParticipant The shutdown hook participant to remove
   */
  void removeShutdownHookParticipant(ShutdownHookParticipant shutdownHookParticipant) {
    shutdownHookParticipants.remove(shutdownHookParticipant);
  }

  /** The stop handler for the HUP signal */
  private StopHandler stopHandler_HUP = new StopHandler(new Signal("HUP"), this); //$NON-NLS-1$

  /** The stop handler for the INT signal */
  private StopHandler stopHandler_INT = new StopHandler(new Signal("INT"), this); //$NON-NLS-1$

  /** The stop handler for the TERM signal */
  private StopHandler stopHandler_TERM = new StopHandler(new Signal("TERM"), this); //$NON-NLS-1$

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
      /* swallow and can't be covered in a test */
    }
  }

  /**
   * Invoke the callback on all registered shutdown hook participants
   */
  void stopAllShutdownHookParticipants() {
    for (ShutdownHookParticipant shutdownHookParticipant : this.shutdownHookParticipants) {
      try {
        shutdownHookParticipant.shutdownHook();
      }
      catch (Throwable e) {
        /* swallow */
      }
    }
  }

  @Override
  public void run() {
    stopAllShutdownHookParticipants();
  }
}