package nl.pelagic.audio.tag.checker;

import java.util.List;

import nl.pelagic.audio.tag.checker.api.TagChecker;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.ProblemReport;

import org.junit.Ignore;

@Ignore
@SuppressWarnings("javadoc")
public class MyTagChecker implements TagChecker {
  public GenericTagFieldName key = null;
  public List<ProblemReport> value = null;

  @Override
  public void check(GenericTag genericTag) {
    if (key != null) {
      genericTag.getReports().put(key, value);
    }
  }
}
