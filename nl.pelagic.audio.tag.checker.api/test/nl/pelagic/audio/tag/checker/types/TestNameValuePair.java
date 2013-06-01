package nl.pelagic.audio.tag.checker.types;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method"
})
public class TestNameValuePair {

  private SecureRandom random = new SecureRandom();

  @Test
  public void testNameValuePair() {
    NameValuePair nvp = new NameValuePair();
    assertThat(nvp.getName(), nullValue());
    assertThat(nvp.getValue(), nullValue());
  }

  @Test
  public void testNameValuePairStringString() {
    String name = new BigInteger(130, random).toString(32);
    String value = new BigInteger(130, random).toString(32);
    NameValuePair nvp = new NameValuePair(name, value);
    assertThat(nvp.getName(), equalTo(name));
    assertThat(nvp.getValue(), equalTo(value));
  }

  @Test
  public void testSetName() {
    String name = new BigInteger(130, random).toString(32);
    NameValuePair nvp = new NameValuePair();
    nvp.setName(name);
    assertThat(nvp.getName(), equalTo(name));
    assertThat(nvp.getValue(), nullValue());
  }

  @Test
  public void testSetValue() {
    String value = new BigInteger(130, random).toString(32);
    NameValuePair nvp = new NameValuePair();
    nvp.setValue(value);
    assertThat(nvp.getName(), nullValue());
    assertThat(nvp.getValue(), equalTo(value));
  }

  @Test
  public void testToString() {
    NameValuePair nvp = new NameValuePair();
    assertThat(nvp.toString(), notNullValue());
  }
}
