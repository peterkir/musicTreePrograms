package nl.pelagic.audio.tag.checker.types;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.jaudiotagger.tag.images.Artwork;

import aQute.bnd.annotation.ProviderType;

/**
 * A generic tag that is used to abstract actual music file tags.
 */
@ProviderType
public class GenericTag {
  /** A flag to signal whether the tag contains hasArtwork */
  private boolean hasArtwork = false;

  /** The backing file of the tag */
  private File backingFile = null;

  /**
   * A set with classes from which this information was constructed
   */
  private Set<Class<? extends Object>> sourceTagClasses = new HashSet<>();

  /**
   * A map of the generalised tag field names against a map of the field VALUES
   * against a set of the actual field NAMES using those values
   */
  private Map<GenericTagFieldName, Map<String, Set<String>>> fields = new TreeMap<>();

  /**
   * A map of the generalised tag field NAMES against a set of check reports for
   * that field
   */
  private Map<GenericTagFieldName, List<ProblemReport>> reports = new TreeMap<>();

  /**
   * Constructor
   */
  public GenericTag() {
    super();
  }

  /**
   * @return true when the tag has artwork
   */
  public boolean hasArtwork() {
    return hasArtwork;
  }

  /**
   * @param artwork the artwork list (note that the artwork is not stored, only
   *          a flag that reveals whether or not the artwork list had entries)
   */
  public void setArtwork(List<Artwork> artwork) {
    this.hasArtwork = (artwork != null) && (artwork.size() > 0);
  }

  /**
   * @return the backingFile
   */
  public File getBackingFile() {
    return backingFile;
  }

  /**
   * @param backingFile the backingFile to set
   */
  public void setBackingFile(File backingFile) {
    this.backingFile = backingFile;
  }

  /**
   * @return the sourceTagClasses
   */
  public Set<Class<? extends Object>> getSourceTagClasses() {
    return sourceTagClasses;
  }

  /**
   * @param sourceTagClass the sourceTagClass to add
   */
  public void addSourceTagClass(Class<? extends Object> sourceTagClass) {
    if (sourceTagClass != null) {
      this.sourceTagClasses.add(sourceTagClass);
    }
  }

  /**
   * @return the fields
   */
  public Map<GenericTagFieldName, Map<String, Set<String>>> getFields() {
    return fields;
  }

  /**
   * Add a name to a value/name map. Creates a new name set in the map if the
   * map did not contain one for the specified value.
   * 
   * @param valueNameMap the value/name map
   * @param name the name
   * @param value the value
   */
  static void addToValueNameSetMap(Map<String, Set<String>> valueNameMap, String name, String value) {
    if ((valueNameMap == null) || (name == null) || (value == null)) {
      return;
    }

    Set<String> nameSet = valueNameMap.get(value);
    if (nameSet == null) {
      nameSet = new TreeSet<>();
      valueNameMap.put(value, nameSet);
    }

    /* add the id to the generic tag fieldName id set */
    nameSet.add(name);
  }

  /**
   * <p>
   * Add a tag field
   * </p>
   * <p>
   * When the specified name is the same as the one of the previous invocation
   * (as contained in prevTagNameValuePair), then value is appended (with a
   * " - " prefix) to the previous value that's in previousNameValuePair
   * </p>
   * <p>
   * This functionality basically exists because there is a bug in the EasyTag
   * application that splits fields values like 'aa - bb' into 2 tag fields with
   * the same name but different values in the file. This functionality allows
   * the application to merge those back together.
   * </p>
   * 
   * @param previousNameValuePair the previous tag name/value pair (can be null:
   *          no previous value)
   * @param genericName the generic tag field name
   * @param name the non-generic tag field name
   * @param value the value for name
   * @return the new tag name/value pair. Null when genericName, name or value
   *         (or any combination of those) is null.
   */
  public NameValuePair addField(NameValuePair previousNameValuePair, GenericTagFieldName genericName, String name,
      String value) {
    if ((genericName == null) || (name == null) || (value == null)) {
      return null;
    }

    /* get the previous values */
    String previousName = null;
    String previousValue = null;
    if (previousNameValuePair != null) {
      previousName = previousNameValuePair.getName();
      previousValue = previousNameValuePair.getValue();
    }

    /* create the value/name map for the generic name, if needed */
    Map<String, Set<String>> valueNameSetMap = fields.get(genericName);
    if (valueNameSetMap == null) {
      valueNameSetMap = new TreeMap<>();
      fields.put(genericName, valueNameSetMap);
    }

    /* setup the result in advance */
    NameValuePair result = new NameValuePair(name, value);

    if ((previousName == null) /* no previous name */
        || (previousValue == null) /* no previous value */
        || valueNameSetMap.isEmpty() /* no value/name map for the genericName */
        || !name.equals(previousName) /* previous name != new name */) {
      /* just create a new mapping, if needed */
      addToValueNameSetMap(valueNameSetMap, name, value);
      return result;
    }

    /*
     * The previous name is the same as the new name: we need to append the new
     * value to the previous value.
     */

    Set<String> nameSet = valueNameSetMap.remove(previousValue);
    if (nameSet == null) {
      /* there was no mapping for the previous value: just create a new mapping */
      addToValueNameSetMap(valueNameSetMap, name, value);
      return result;
    }

    boolean removed = nameSet.remove(name);
    if (!removed) {
      /* the name was not in the name set: just add it */
      addToValueNameSetMap(valueNameSetMap, name, value);
      return result;
    }

    if (nameSet.size() > 0) {
      /* restore */
      valueNameSetMap.put(previousValue, nameSet);
    }

    nameSet = new HashSet<>();
    nameSet.add(name);

    /* append and put */
    result.setValue(previousValue.concat(" - ").concat(value)); //$NON-NLS-1$
    valueNameSetMap.put(result.getValue(), nameSet);

    return result;
  }

  /**
   * @return the reports
   */
  public Map<GenericTagFieldName, List<ProblemReport>> getReports() {
    return reports;
  }

  /**
   * Add a number of reports about a tag field
   * 
   * @param genericTagFieldName the generic tag field name (non-null)
   * @param reportsToAdd the reports to add (non-null)
   */
  public void addReports(GenericTagFieldName genericTagFieldName, List<ProblemReport> reportsToAdd) {
    if ((genericTagFieldName == null) || (reportsToAdd == null)) {
      return;
    }

    /* get the reports for the genericTagFieldName */
    List<ProblemReport> contents = reports.get(genericTagFieldName);
    if (contents == null) {
      contents = new LinkedList<>();
      reports.put(genericTagFieldName, contents);
    }

    /* add the reports */
    contents.addAll(reportsToAdd);
  }

  /**
   * Add a report about a tag field
   * 
   * @param genericTagFieldName the generic tag field name (non-null)
   * @param message the message to add (non-null)
   * @param actual the actual value
   * @param positionMarkers the position markers
   * @param expected the expected value
   */
  public void addReport(GenericTagFieldName genericTagFieldName, String message, String actual,
      List<Integer> positionMarkers, String expected) {
    if ((genericTagFieldName == null) || (message == null)) {
      return;
    }

    List<ProblemReport> report = new ArrayList<>(1);
    report.add(new ProblemReport(message, expected, actual, positionMarkers));
    addReports(genericTagFieldName, report);
  }
}
