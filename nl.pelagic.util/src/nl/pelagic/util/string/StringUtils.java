package nl.pelagic.util.string;

/**
 * String utilities
 */
public class StringUtils {
  /**
   * Escapes quotes (") in a string
   * 
   * @param s the string to escape
   * @return the escaped string or null when s was null
   */
  public static String escQuote(String s) {
    if (s == null) {
      return s;
    }
    return s.replaceAll("\"", "\\\\\""); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
