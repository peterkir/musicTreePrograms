package nl.pelagic.util.string;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method"
})
public class TestStringUtils {
  @Test
  public void testEscQuote_Null() {
    String result = StringUtils.escQuote(null);
    assertThat(result, equalTo(null));
  }

  @Test
  public void testEscQuote_Empty() {
    String result = StringUtils.escQuote(""); //$NON-NLS-1$
    assertThat(result, equalTo("")); //$NON-NLS-1$
  }

  @Test
  public void testEscQuote_NoQuotes() {
    String result = StringUtils.escQuote("no quotes in this string"); //$NON-NLS-1$
    assertThat(result, equalTo("no quotes in this string")); //$NON-NLS-1$
  }

  @Test
  public void testEscQuote_Quotes() {
    String result = StringUtils.escQuote("some \"quotes\" in this string"); //$NON-NLS-1$
    assertThat(result, equalTo("some \\\"quotes\\\" in this string")); //$NON-NLS-1$
  }
}
