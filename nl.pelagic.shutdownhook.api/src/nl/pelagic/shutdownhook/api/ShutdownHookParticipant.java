package nl.pelagic.shutdownhook.api;

import aQute.bnd.annotation.ConsumerType;

/**
 * A participant of the VM shutdown hook handler service must implement this
 * interface
 */
@ConsumerType
public interface ShutdownHookParticipant {
  /**
   * <p>
   * Called when the VM shuts down.
   * </p>
   * <p>
   * <b>Note:</b> the call is invoked from the thread of the VM shutdown hook.
   * The VM is in a fragile state at that time so be careful with the work that
   * is performed in the call: keep the work to an absolute minimum.
   * </p>
   */
  void shutdownHook();
}