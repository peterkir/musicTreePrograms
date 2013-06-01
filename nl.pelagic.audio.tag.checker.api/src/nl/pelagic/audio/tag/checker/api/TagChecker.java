package nl.pelagic.audio.tag.checker.api;

import nl.pelagic.audio.tag.checker.types.GenericTag;
import aQute.bnd.annotation.ConsumerType;

/**
 * Interface of a bundle that checks a generic tag against certain constraints
 */
@ConsumerType
public interface TagChecker {
  /**
   * Checks a generic tag against certain constraints. When a constraint is not
   * met, one or more reports are created (in the generic tag) for each field
   * that fails the constraints.
   * 
   * @param genericTag the generic tag to check (and to add reports to)
   */
  void check(GenericTag genericTag);
}
