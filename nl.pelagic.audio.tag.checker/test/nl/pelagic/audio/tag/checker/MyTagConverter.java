package nl.pelagic.audio.tag.checker;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import nl.pelagic.audio.tag.checker.api.TagConverter;
import nl.pelagic.audio.tag.checker.types.GenericTag;

import org.jaudiotagger.tag.Tag;
import org.jaudiotagger.tag.flac.FlacTag;
import org.junit.Ignore;

@Ignore
@SuppressWarnings("javadoc")
public class MyTagConverter implements TagConverter {

  private Set<Class<? extends Object>> stcs = new HashSet<>();

  public MyTagConverter() {
    super();
    stcs.add(FlacTag.class);
  }

  @Override
  public Set<Class<? extends Object>> getSupportedTagClasses() {
    return stcs;
  }

  @Override
  public Map<Class<? extends Object>, Set<String>> getUnknownTagFieldNames() {
    return null;
  }

  public GenericTag genericTag = new GenericTag();
  public boolean retval = false;

  @Override
  public boolean convert(GenericTag genericTag, Tag tag) {
    if (!retval) {
      return false;
    }

    genericTag.getFields().putAll(this.genericTag.getFields());
    genericTag.getReports().putAll(this.genericTag.getReports());
    genericTag.getSourceTagClasses().addAll(this.genericTag.getSourceTagClasses());
    return true;
  }
}
