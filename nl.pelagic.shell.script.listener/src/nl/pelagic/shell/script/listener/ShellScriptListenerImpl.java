package nl.pelagic.shell.script.listener;

import java.io.PrintStream;
import java.util.List;

import nl.pelagic.shell.script.listener.api.ShellScriptListener;
import aQute.bnd.annotation.component.Component;

/**
 * This bundle is a listener that a generates a shell script on a PrintStream
 * (stdout by default).
 */
@Component
public class ShellScriptListenerImpl implements ShellScriptListener {
  /** the shell comment line prefix */
  public static final String SHELL_COMMENT_LINE_PREFIX = "# "; //$NON-NLS-1$

  /** whether verbose mode is enabled or not */
  private boolean verbose = false;

  /** whether quiet mode is enabled or not */
  private boolean quiet = false;

  @Override
  public void setVerbose(boolean verbose, boolean quiet) {
    this.quiet = quiet;
    this.verbose = verbose;
  }

  /** the output */
  private PrintStream out = System.out;

  /**
   * @param out the out to set
   */
  public void setOut(PrintStream out) {
    this.out = out;
  }

  /**
   * {@inheritDoc}
   * <p>
   * Note: the command is only output when verbose mode is enabled.
   * </p>
   */
  @Override
  public void addCommand(String command) {
    if ((out == null) || (command == null) || quiet || !verbose) {
      return;
    }

    out.println(command);
  }

  /**
   * {@inheritDoc}
   * <p>
   * Note: a shell comments character (#) is automatically added at the
   * beginning of the message.
   * </p>
   */
  @Override
  public void addMessage(String message) {
    if ((out == null) || (message == null) || quiet) {
      return;
    }

    out.println(SHELL_COMMENT_LINE_PREFIX + message);
  }

  /** the indent for command lines following the first line */
  private static final String indent = "  "; //$NON-NLS-1$

  @Override
  public String commandListToString(List<String> commandList, int prefixSpaces) {
    String prefix = (prefixSpaces == 0) ? "" : String.format("%" + Math.abs(prefixSpaces) + "s", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    if ((commandList == null) || commandList.isEmpty()) {
      return prefix;
    }

    StringBuilder sb = new StringBuilder();
    sb.append(prefix);

    int index = 0;
    boolean previousCommandStartsWithMinus = false;
    boolean previousCommandEndedWithEquals = false;
    for (String cmd : commandList) {
      boolean currentCommandStartsWithMinus = cmd.startsWith("-"); //$NON-NLS-1$

      boolean noNewline =
          (index == 0) || previousCommandEndedWithEquals
              || (previousCommandStartsWithMinus && !currentCommandStartsWithMinus);

      if (noNewline) {
        if ((index != 0) && !previousCommandEndedWithEquals) {
          /* add a space _between_ arguments */
          sb.append(" "); //$NON-NLS-1$
        }
      } else {
        /* put the next argument on a new line and indent it */
        sb.append(String.format(" \\%n%s%s", indent, prefix)); //$NON-NLS-1$
      }

      if (currentCommandStartsWithMinus) {
        /* cmd is a 'switch' */
        sb.append(cmd);
      } else {
        /* cmd is a regular argument, so quote it */
        sb.append("\""); //$NON-NLS-1$
        sb.append(cmd);
        sb.append("\""); //$NON-NLS-1$
      }

      previousCommandEndedWithEquals = cmd.endsWith("="); //$NON-NLS-1$
      previousCommandStartsWithMinus = currentCommandStartsWithMinus;
      index++;
    }

    return sb.toString();
  }
}
