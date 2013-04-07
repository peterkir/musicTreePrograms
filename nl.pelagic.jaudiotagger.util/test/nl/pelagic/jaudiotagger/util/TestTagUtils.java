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
  public void testConcatenateTagFields_Two() {
    List<TagField> fieldValues = new LinkedList<>();
    String defaultValue = "default"; //$NON-NLS-1$

    String fieldId1 = "fieldId1"; //$NON-NLS-1$
    String fieldContent1 = "fieldContent1"; //$NON-NLS-1$
    TagField field1 = new ID3v1TagField(fieldId1, fieldContent1);
    String fieldId2 = "fieldId2"; //$NON-NLS-1$
    String fieldContent2 = "fieldContent2"; //$NON-NLS-1$
    TagField field2 = new ID3v1TagField(fieldId2, fieldContent2);
    fieldValues.add(field1);
    fieldValues.add(field2);

    String result = TagUtils.concatenateTagFields(fieldValues, defaultValue);

    assertThat(result, equalTo(fieldContent1 + " - " + fieldContent2)); //$NON-NLS-1$
  }

  @Test
  public void testConcatenateTagFields_SecondZeroLength() {
    List<TagField> fieldValues = new LinkedList<>();
    String defaultValue = "default"; //$NON-NLS-1$

    String fieldId1 = "fieldId1"; //$NON-NLS-1$
    String fieldContent1 = "fieldContent1"; //$NON-NLS-1$
    TagField field1 = new ID3v1TagField(fieldId1, fieldContent1);
    String fieldId2 = "fieldId2"; //$NON-NLS-1$
    String fieldContent2 = ""; //$NON-NLS-1$
    TagField field2 = new ID3v1TagField(fieldId2, fieldContent2);
    fieldValues.add(field1);
    fieldValues.add(field2);

    String result = TagUtils.concatenateTagFields(fieldValues, defaultValue);

    assertThat(result, equalTo(fieldContent1));
  }
}
