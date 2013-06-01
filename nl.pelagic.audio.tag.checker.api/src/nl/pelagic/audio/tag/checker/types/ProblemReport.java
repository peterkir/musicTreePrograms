package nl.pelagic.audio.tag.checker.types;

import java.util.List;

import aQute.bnd.annotation.ProviderType;

/**
 * A problem report
 */
@ProviderType
public class ProblemReport {
  /** The message */
  private String message = null;

  /** The expected value */
  private String expectedValue = null;

  /** The actual value */
  private String actualValue = null;

  /** A list of problem positions in {@link #actualValue} */
  private List<Integer> positionMarkers = null;

  /**
   * Default constructor
   */
  public ProblemReport() {
    super();
  }

  /**
   * Constructor
   * 
   * @param message the message
   * @param expectedValue the expectedValue value
   * @param actualValue the actual value
   * @param positionMarkers a list of problem positions in {@link #actualValue}
   */
  public ProblemReport(String message, String expectedValue, String actualValue, List<Integer> positionMarkers) {
    super();
    this.message = message;
    this.expectedValue = expectedValue;
    this.actualValue = actualValue;
    this.positionMarkers = positionMarkers;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the expectedValue
   */
  public String getExpectedValue() {
    return expectedValue;
  }

  /**
   * @param expectedValue the expectedValue to set
   */
  public void setExpectedValue(String expectedValue) {
    this.expectedValue = expectedValue;
  }

  /**
   * @return the actualValue
   */
  public String getActualValue() {
    return actualValue;
  }

  /**
   * @param actualValue the actualValue to set
   */
  public void setActualValue(String actualValue) {
    this.actualValue = actualValue;
  }

  /**
   * @return the positionMarkers
   */
  public List<Integer> getPositionMarkers() {
    return positionMarkers;
  }

  /**
   * @param positionMarkers the positionMarkers to set
   */
  public void setPositionMarkers(List<Integer> positionMarkers) {
    this.positionMarkers = positionMarkers;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("ProblemReport [message="); //$NON-NLS-1$
    builder.append(message);
    builder.append(", expectedValue="); //$NON-NLS-1$
    builder.append(expectedValue);
    builder.append(", actualValue="); //$NON-NLS-1$
    builder.append(actualValue);
    builder.append(", positionMarkers="); //$NON-NLS-1$
    builder.append(positionMarkers);
    builder.append("]"); //$NON-NLS-1$
    return builder.toString();
  }
}
