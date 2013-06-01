package nl.pelagic.audio.tag.checker.cli;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import nl.pelagic.audio.tag.checker.api.TagConverter;
import nl.pelagic.audio.tag.checker.types.GenericTag;

import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.jaudiotagger.tag.id3.ID3v24Tag;
import org.junit.Ignore;

@Ignore
@SuppressWarnings({
    "nls", "javadoc"
})
public class MyTagConverter implements TagConverter {
  Set<Class<? extends Object>> supportedTagClasses = new HashSet<>();

  public MyTagConverter() {
    super();
    supportedTagClasses.add(FlacTag.class);
    supportedTagClasses.add(ID3v24Tag.class);
    Set<String> set1 = new TreeSet<>();
    Set<String> set2 = new TreeSet<>();
    set1.add("unknown field 1");
    set1.add("unknown field 2");
    set2.add("unknown field 1");
    set2.add("unknown field 2");
    unknownTagFieldNames.put(FlacTag.class, set1);
    unknownTagFieldNames.put(ID3v24Tag.class, set2);
  }

  @Override
  public Set<Class<? extends Object>> getSupportedTagClasses() {
    return supportedTagClasses;
  }

  Map<Class<? extends Object>, Set<String>> unknownTagFieldNames = new HashMap<>();

  @Override
  public Map<Class<? extends Object>, Set<String>> getUnknownTagFieldNames() {
    return unknownTagFieldNames;
  }

  boolean converted = false;

  @Override
  public boolean convert(GenericTag genericTag, Tag tag) {
    return converted;
  }
}
