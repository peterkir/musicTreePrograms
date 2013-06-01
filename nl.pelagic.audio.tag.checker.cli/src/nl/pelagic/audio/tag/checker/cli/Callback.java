package nl.pelagic.audio.tag.checker.cli;

import java.io.File;
import java.io.PrintStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nl.pelagic.audio.tag.checker.api.AudioTagCheckerCallback;
import nl.pelagic.audio.tag.checker.cli.i18n.Messages;
import nl.pelagic.audio.tag.checker.types.GenericTag;
import nl.pelagic.audio.tag.checker.types.GenericTagFieldName;
import nl.pelagic.audio.tag.checker.types.ProblemReport;

import org.jaudiotagger.tag.Tag;

/**
 * This class implements reporting of progress/warnings/errors during tag
 * checking.
 */
public class Callback implements AudioTagCheckerCallback {
  /** the prefix used when printing problem reports */
  public static final String prefix = "  "; //$NON-NLS-1$

  /** the indent used when print problem report fields */
  public static final String fieldIndent = "  "; //$NON-NLS-1$

  /** the length of a line of dots printed as progress */
  public static final int LINE_LENGTH = 80;

  /** true is progress must be shown with a dot */
  private boolean verbose = false;

  /** true is progress must be shown with a filename */
  private boolean extraVerbose = false;

  /** the print stream for normal messages */
  private PrintStream out = System.out;

  /** the print stream for warning and error messages */
  private PrintStream err = System.err;

  /** the print position for the progress */
  private int pos = 0;

  /**
   * Default constructor
   */
  public Callback() {
    super();
  }

  /**
   * @return the verbose
   */
  public boolean isVerbose() {
    return verbose;
  }

  /**
   * @param verbose the verbose to set
   */
  public void setVerbose(boolean verbose) {
    this.verbose = verbose;
  }

  /**
   * @return the extraVerbose
   */
  public boolean isExtraVerbose() {
    return extraVerbose;
  }

  /**
   * @param extraVerbose the extraVerbose to set
   */
  public void setExtraVerbose(boolean extraVerbose) {
    this.extraVerbose = extraVerbose;
  }

  /**
   * @return the out
   */
  public PrintStream getOut() {
    return out;
  }

  /**
   * @param out the out to set
   */
  public void setOut(PrintStream out) {
    if (out == null) {
      throw new IllegalArgumentException(Messages.getString("Callback.0")); //$NON-NLS-1$
    }

    this.out = out;
  }

  /**
   * @return the err
   */
  public PrintStream getErr() {
    return err;
  }

  /**
   * @param err the err to set
   */
  public void setErr(PrintStream err) {
    if (err == null) {
      throw new IllegalArgumentException(Messages.getString("Callback.0")); //$NON-NLS-1$
    }

    this.err = err;
  }

  /*
   * Interface Methods
   */

  @Override
  public void unsupportedExtension(File file) {
    nonPassedProgress();
    err.printf(Messages.getString("Callback.1"), file.getPath()); //$NON-NLS-1$
  }

  @Override
  public void notReadable(File file, Exception e) {
    nonPassedProgress();
    err.printf(Messages.getString("Callback.2"), file.getPath()); //$NON-NLS-1$
  }

  @Override
  public void noTag(File file) {
    nonPassedProgress();
    err.printf(Messages.getString("Callback.3"), file.getPath()); //$NON-NLS-1$
  }

  @Override
  public void tagNotConverted(File file, Tag tag) {
    nonPassedProgress();
    err.printf(Messages.getString("Callback.4"), file.getPath()); //$NON-NLS-1$
    err.printf(Messages.getString("Callback.5"), tag.getClass().getName()); //$NON-NLS-1$
  }

  @Override
  public void checksFailed(GenericTag tag) {
    nonPassedProgress();
    printProblemReports(tag, prefix);
  }

  @Override
  public void checksPassed(GenericTag tag) {
    passedProgress();
    if (extraVerbose || verbose) {
      if (extraVerbose) {
        out.println(tag.getBackingFile().getPath());
      } else {
        out.printf("."); //$NON-NLS-1$
        pos++;
      }
    }
  }

  /*
   * Helper Methods
   */

  /**
   * Update the position for the non-'passed' callbacks
   */
  void nonPassedProgress() {
    if (verbose) {
      pos = 0;
      out.println();
      flush();
    }
  }

  /**
   * Update the position for the 'passed' callback
   */
  void passedProgress() {
    if (verbose) {
      if (pos >= LINE_LENGTH) {
        pos = 0;
        out.println();
        flush();
      }
    }
  }

  /**
   * Flush the outputs
   */
  void flush() {
    out.flush();
    err.flush();
  }

  /**
   * Convert a list of positions into a string with markers on the specified
   * positions.
   * 
   * @param positionMarkers the list of positions
   * @return a string with markers on the specified positions
   */
  static String positionsToMarkerString(List<Integer> positionMarkers) {
    if ((positionMarkers == null) || (positionMarkers.size() == 0)) {
      return ""; //$NON-NLS-1$
    }

    Collections.sort(positionMarkers);

    char[] s =
        String.format("%" + (positionMarkers.get(positionMarkers.size() - 1).intValue() + 1) + "s", "").toCharArray(); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    for (Integer positionMarker : positionMarkers) {
      s[positionMarker.intValue()] = '^';
    }

    return new String(s);
  }

  /**
   * Print all reports
   * 
   * @param tag the tag for which to print the problem reports
   * @param prefix the prefix to use during printing
   */
  void printProblemReports(GenericTag tag, String prefix) {
    if (tag == null) {
      return;
    }

    Map<GenericTagFieldName, List<ProblemReport>> reports = tag.getReports();
    assert (reports != null);

    if (reports.size() > 0) {
      err.println();
    }

    err.println(tag.getBackingFile().getPath());

    if (reports.size() > 0) {
      String fieldString = Messages.getString("Callback.6"); //$NON-NLS-1$
      String messageString = Messages.getString("Callback.7"); //$NON-NLS-1$
      String actualString = Messages.getString("Callback.8"); //$NON-NLS-1$
      String expectedString = Messages.getString("Callback.9"); //$NON-NLS-1$

      int stringWidth = Math.max(messageString.length(), Math.max(actualString.length(), expectedString.length()));
      String format = prefix + "%s%-" + stringWidth + "s%s%s%n"; //$NON-NLS-1$ //$NON-NLS-2$

      for (Entry<GenericTagFieldName, List<ProblemReport>> entry : reports.entrySet()) {
        err.printf("%s%s : %s%n", fieldIndent, fieldString, entry.getKey()); //$NON-NLS-1$
        int reportNr = 0;
        for (ProblemReport report : entry.getValue()) {
          if (reportNr > 0) {
            err.println();
          }
          reportNr++;

          err.printf(format, fieldIndent, messageString, " : ", report.getMessage()); //$NON-NLS-1$

          String actualValue = report.getActualValue();
          String expectedValue = report.getExpectedValue();
          List<Integer> positionMarkers = report.getPositionMarkers();

          if (actualValue != null) {
            err.printf(format, fieldIndent, actualString, " : ", actualValue); //$NON-NLS-1$
          }
          if (positionMarkers != null) {
            err.printf(format, fieldIndent, "", "   ", positionsToMarkerString(positionMarkers)); //$NON-NLS-1$ //$NON-NLS-2$
          }
          if (expectedValue != null) {
            err.printf(format, fieldIndent, expectedString, " : ", expectedValue); //$NON-NLS-1$
          }
        }
      }
    }
  }
}
