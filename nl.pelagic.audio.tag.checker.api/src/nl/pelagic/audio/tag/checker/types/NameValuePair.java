package nl.pelagic.audio.tag.checker.types;

import aQute.bnd.annotation.ProviderType;

/**
 * Stores the values of a tag field name/value pair
 */
@ProviderType
public class NameValuePair {
  /** The name */
  private String name = null;

  /** The value */
  private String value = null;

  /**
   * Default Constructor
   */
  public NameValuePair() {
    super();
  }

  /**
   * Initialising Constructor
   * 
   * @param name the name
   * @param value the value
   */
  public NameValuePair(String name, String value) {
    super();
    this.name = name;
    this.value = value;
  }

  /**
   * @return the name
   */
  public String getName() {
    return name;
  }

  /**
   * @param name the name to set
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * @return the value
   */
  public String getValue() {
    return value;
  }

  /**
   * @param value the value to set
   */
  public void setValue(String value) {
    this.value = value;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("NameValuePair [name="); //$NON-NLS-1$
    builder.append(name);
    builder.append(", value="); //$NON-NLS-1$
    builder.append(value);
    builder.append("]"); //$NON-NLS-1$
    return builder.toString();
  }
}
