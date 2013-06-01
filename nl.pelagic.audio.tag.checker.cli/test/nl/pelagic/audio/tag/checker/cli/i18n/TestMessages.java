package nl.pelagic.audio.tag.checker.cli.i18n;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method"
})
public class TestMessages {

  @Test
  public void testGetString_Null() {
    String s = Messages.getString(null);
    assertThat(s, equalTo(null));
  }

  @Test
  public void testGetString_Tests() {
    String in = "Tests.0"; //$NON-NLS-1$
    String s = Messages.getString(in);
    assertThat(s, equalTo("Do not translate or remove; used in tests")); //$NON-NLS-1$
  }

  @Test
  public void testGetString_NotThere() {
    String in = "Tests.Is.Not.There"; //$NON-NLS-1$
    String s = Messages.getString(in);
    assertThat(s, equalTo('!' + in + '!'));
  }
}
