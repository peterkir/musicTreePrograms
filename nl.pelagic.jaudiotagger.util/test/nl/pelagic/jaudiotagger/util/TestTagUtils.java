package nl.pelagic.jaudiotagger.util;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.LinkedList;
import java.util.List;

import org.jaudiotagger.tag.TagField;
import org.jaudiotagger.tag.id3.ID3v1TagField;
import org.junit.Test;

@SuppressWarnings({
    "javadoc", "static-method"
})
public class TestTagUtils {
  @Test
  public void testConcatenateTagFields_Null() {
    List<TagField> fieldValues = null;
    String defaultValue = "default"; //$NON-NLS-1$
    String result = TagUtils.concatenateTagFields(fieldValues, defaultValue);

    assertThat(result, equalTo(defaultValue));
  }

  @Test
  public void testConcatenateTagFields_Empty() {
    List<TagField> fieldValues = new LinkedList<>();
    String defaultValue = "default"; //$NON-NLS-1$
    String result = TagUtils.concatenateTagFields(fieldValues, defaultValue);

    assertThat(result, equalTo(defaultValue));
  }

  @Test
  public void testConcatenateTagFields_One() {
    List<TagField> fieldValues = new LinkedList<>();
    String defaultValue = "default"; //$NON-NLS-1$

    String fieldId1 = "fieldId1"; //$NON-NLS-1$
    String fieldContent1 = "fieldContent1"; //$NON-NLS-1$
    TagField field1 = new ID3v1TagField(fieldId1, fieldContent1);
    fieldValues.add(field1);

    String result = TagUtils.concatenateTagFields(fieldValues, defaultValue);

    assertThat(result, equalTo(fieldContent1));
  }

  @Test
  public void testConcatenateTagValues_Null() {
    List<String> fieldValues = null;
    String defaultValue = "default"; //$NON-NLS-1$
    String result = TagUtils.concatenateTagValues(fieldValues, defaultValue);

    assertThat(result, equalTo(defaultValue));
  }

  @Test
  public void testConcatenateTagValues_Empty() {
    List<String> fieldValues = new LinkedList<>();
    String defaultValue = "default"; //$NON-NLS-1$
    String result = TagUtils.concatenateTagValues(fieldValues, defaultValue);

    assertThat(result, equalTo(defaultValue));
  }

  @Test
  public void testConcatenateTagValues_One() {
    List<String> fieldValues = new LinkedList<>();
    String defaultValue = "default"; //$NON-NLS-1$

    String fieldContent1 = "fieldContent1"; //$NON-NLS-1$
    fieldValues.add(fieldContent1);

    String result = TagUtils.concatenateTagValues(fieldValues, defaultValue);

    assertThat(result, equalTo(fieldContent1));
  }

  @Test
  public void testConcatenateTagValues_Two() {
    List<String> fieldValues = new LinkedList<>();
    String defaultValue = "default"; //$NON-NLS-1$

    String fieldContent1 = "fieldContent1"; //$NON-NLS-1$
    String fieldContent2 = "fieldContent2"; //$NON-NLS-1$
    fieldValues.add(fieldContent1);
    fieldValues.add(fieldContent2);

    String result = TagUtils.concatenateTagValues(fieldValues, defaultValue);

    assertThat(result, equalTo(fieldContent1 + " - " + fieldContent2)); //$NON-NLS-1$
  }

  @Test
  public void testConcatenateTagValues_SecondZeroLength() {
    List<String> fieldValues = new LinkedList<>();
    String defaultValue = "default"; //$NON-NLS-1$

    String fieldContent1 = "fieldContent1"; //$NON-NLS-1$
    String fieldContent2 = ""; //$NON-NLS-1$
    fieldValues.add(fieldContent1);
    fieldValues.add(fieldContent2);

    String result = TagUtils.concatenateTagValues(fieldValues, defaultValue);

    assertThat(result, equalTo(fieldContent1));
  }
}
