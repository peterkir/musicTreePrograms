package nl.pelagic.shutdownhook.api;

import aQute.bnd.annotation.ConsumerType;

/**
 * The interface that a participant of the VM shutdown hook must implement
 */
@ConsumerType
public interface ShutdownHookParticipant {
  /**
   * Called when the VM shuts down. Note: it's called on it's own thread.
   */
  void shutdownHook();
}
