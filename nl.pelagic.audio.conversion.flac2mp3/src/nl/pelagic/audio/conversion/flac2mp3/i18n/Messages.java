package nl.pelagic.audio.conversion.flac2mp3.i18n;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * i18n messages
 */
public class Messages {
  /** the bundle name */
  private static final String BUNDLE_NAME = "nl.pelagic.audio.conversion.flac2mp3.i18n.messages"; //$NON-NLS-1$

  /** the resource bundle */
  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

  /**
   * Get a message string
   * 
   * @param key string key
   * @return string
   */
  public static String getString(String key) {
    if (key == null) {
      return null;
    }
    try {
      return RESOURCE_BUNDLE.getString(key);
    }
    catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}
