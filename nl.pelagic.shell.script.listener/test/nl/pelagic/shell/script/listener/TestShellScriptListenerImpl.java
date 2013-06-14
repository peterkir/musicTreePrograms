package nl.pelagic.shell.script.listener;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

@SuppressWarnings("javadoc")
public class TestShellScriptListenerImpl {

  private File tmpFile;
  private DummyPrintStream dummyPrintStream;
  private ShellScriptListenerImpl shellScriptListenerImpl;

  @Before
  public void setUp() throws Exception {
    tmpFile = File.createTempFile(this.getClass().getSimpleName(), ".tmp"); //$NON-NLS-1$
    dummyPrintStream = new DummyPrintStream(tmpFile);
    shellScriptListenerImpl = new ShellScriptListenerImpl();
    shellScriptListenerImpl.setOut(dummyPrintStream);
  }

  @After
  public void tearDown() {
    shellScriptListenerImpl.setOut(System.out);
    shellScriptListenerImpl = null;
    dummyPrintStream = null;
    tmpFile.delete();
  }

  @Test
  public void testSetVerbose_Default() {
    String cmd = "command"; //$NON-NLS-1$
    shellScriptListenerImpl.addCommand(cmd);
    assertThat(dummyPrintStream.sb.toString(), equalTo("")); //$NON-NLS-1$
  }

  private void verifyAddCommand(PrintStream out, String command, boolean quiet, boolean verbose, boolean extraVerbose,
      String result) {
    shellScriptListenerImpl.setOut(out);
    shellScriptListenerImpl.setVerbose(verbose, extraVerbose, quiet);
    shellScriptListenerImpl.addCommand(command);
    assertThat(dummyPrintStream.sb.toString(), equalTo(result));
    dummyPrintStream.sb.delete(0, dummyPrintStream.sb.length());
  }

  @Test
  public void testAddCommand() {
    /**
     * <pre>
     *   out  command    quiet   verbose  extraverbose  result
     *  null     null    false     false         false  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null     null    false      TRUE*         true  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null     null    false      true         false  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null     null    false      TRUE*         true  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null    !null    false     false         false  ""
     *  null    !null     true     FALSE#        FALSE# ""
     *  null    !null    false      TRUE*         true  ""
     *  null    !null     true     FALSE#        FALSE# ""
     *  null    !null    false      true         false  ""
     *  null    !null     true     FALSE#        FALSE# ""
     *  null    !null    false      TRUE*         true  ""
     *  null    !null     true     FALSE#        FALSE# ""
     * !null     null    false     false         false  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null     null    false      TRUE*         true  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null     null    false      true         false  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null     null    false      TRUE*         true  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null    !null    false     false         false  ""
     * !null    !null     true     FALSE#        FALSE# ""
     * !null    !null    false      TRUE*         true  command
     * !null    !null     true     FALSE#        FALSE# ""
     * !null    !null    false      true         false  ""
     * !null    !null     true     FALSE#        FALSE# ""
     * !null    !null    false      TRUE*         true  command
     * !null    !null     true     FALSE#        FALSE# ""
     * 
     * # = implied by quiet
     * * = implied by extraVerbose
     * </pre>
     */

    String cmd = "command"; //$NON-NLS-1$

    verifyAddCommand(null, null, false, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, null, true, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, null, false, false, true, ""); //$NON-NLS-1$
    verifyAddCommand(null, null, true, false, true, ""); //$NON-NLS-1$
    verifyAddCommand(null, null, false, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, null, true, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, null, false, true, true, ""); //$NON-NLS-1$
    verifyAddCommand(null, null, true, true, true, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, false, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, true, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, false, false, true, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, true, false, true, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, false, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, true, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, false, true, true, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, true, true, true, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, false, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, true, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, false, false, true, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, true, false, true, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, false, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, true, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, false, true, true, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, true, true, true, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, cmd, false, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, cmd, true, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, cmd, false, false, true, cmd);
    verifyAddCommand(dummyPrintStream, cmd, true, false, true, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, cmd, false, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, cmd, true, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, cmd, false, true, true, cmd);
    verifyAddCommand(dummyPrintStream, cmd, true, true, true, ""); //$NON-NLS-1$
  }

  private void verifyAddMessage(PrintStream out, String command, boolean quiet, boolean verbose, boolean extraVerbose,
      String result) {
    shellScriptListenerImpl.setOut(out);
    shellScriptListenerImpl.setVerbose(verbose, extraVerbose, quiet);
    shellScriptListenerImpl.addMessage(command);
    assertThat(dummyPrintStream.sb.toString(), equalTo(result));
    dummyPrintStream.sb.delete(0, dummyPrintStream.sb.length());
  }

  @Test
  public void testAddMessage() {
    /**
     * <pre>
     *   out  message    quiet   verbose  extraverbose  result
     *  null     null    false     false         false  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null     null    false      TRUE*         true  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null     null    false      true         false  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null     null    false      TRUE*         true  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null    !null    false     false         false  ""
     *  null    !null     true     FALSE#        FALSE# ""
     *  null    !null    false      TRUE*         true  ""
     *  null    !null     true     FALSE#        FALSE# ""
     *  null    !null    false      true         false  ""
     *  null    !null     true     FALSE#        FALSE# ""
     *  null    !null    false      TRUE*         true  ""
     *  null    !null     true     FALSE#        FALSE# ""
     * !null     null    false     false         false  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null     null    false      TRUE*         true  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null     null    false      true         false  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null     null    false      TRUE*         true  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null    !null    false     false         false  "# " + message
     * !null    !null     true     FALSE#        FALSE# ""
     * !null    !null    false      TRUE*         true  "# " + message
     * !null    !null     true     FALSE#        FALSE# ""
     * !null    !null    false      true         false  "# " + message
     * !null    !null     true     FALSE#        FALSE# ""
     * !null    !null    false      TRUE*         true  "# " + message
     * !null    !null     true     FALSE#        FALSE# ""
     * 
     * # = implied by quiet
     * * = implied by extraVerbose
     * </pre>
     */

    String cmd = "message"; //$NON-NLS-1$

    verifyAddMessage(null, null, false, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, null, true, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, null, false, false, true, ""); //$NON-NLS-1$
    verifyAddMessage(null, null, true, false, true, ""); //$NON-NLS-1$
    verifyAddMessage(null, null, false, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, null, true, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, null, false, true, true, ""); //$NON-NLS-1$
    verifyAddMessage(null, null, true, true, true, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, false, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, true, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, false, false, true, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, true, false, true, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, false, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, true, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, false, true, true, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, true, true, true, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, false, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, true, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, false, false, true, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, true, false, true, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, false, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, true, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, false, true, true, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, true, true, true, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, cmd, false, false, false, ShellScriptListenerImpl.SHELL_COMMENT_LINE_PREFIX
        + cmd);
    verifyAddMessage(dummyPrintStream, cmd, true, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, cmd, false, false, true, ShellScriptListenerImpl.SHELL_COMMENT_LINE_PREFIX + cmd);
    verifyAddMessage(dummyPrintStream, cmd, true, false, true, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, cmd, false, true, false, ShellScriptListenerImpl.SHELL_COMMENT_LINE_PREFIX + cmd);
    verifyAddMessage(dummyPrintStream, cmd, true, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, cmd, false, true, true, ShellScriptListenerImpl.SHELL_COMMENT_LINE_PREFIX + cmd);
    verifyAddMessage(dummyPrintStream, cmd, true, true, true, ""); //$NON-NLS-1$
  }

  private void verifyAddVerboseMessage(PrintStream out, String command, boolean quiet, boolean verbose,
      boolean extraVerbose, String result) {
    shellScriptListenerImpl.setOut(out);
    shellScriptListenerImpl.setVerbose(verbose, extraVerbose, quiet);
    shellScriptListenerImpl.addVerboseMessage(command);
    assertThat(dummyPrintStream.sb.toString(), equalTo(result));
    dummyPrintStream.sb.delete(0, dummyPrintStream.sb.length());
  }

  @Test
  public void testAddVerboseMessage() {
    /**
     * <pre>
     *   out  message    quiet   verbose  extraverbose  result
     *  null     null    false     false         false  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null     null    false      TRUE*         true  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null     null    false      true         false  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null     null    false      TRUE*         true  ""
     *  null     null     true     FALSE#        FALSE# ""
     *  null    !null    false     false         false  ""
     *  null    !null     true     FALSE#        FALSE# ""
     *  null    !null    false      TRUE*         true  ""
     *  null    !null     true     FALSE#        FALSE# ""
     *  null    !null    false      true         false  ""
     *  null    !null     true     FALSE#        FALSE# ""
     *  null    !null    false      TRUE*         true  ""
     *  null    !null     true     FALSE#        FALSE# ""
     * !null     null    false     false         false  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null     null    false      TRUE*         true  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null     null    false      true         false  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null     null    false      TRUE*         true  ""
     * !null     null     true     FALSE#        FALSE# ""
     * !null    !null    false     false         false  ""
     * !null    !null     true     FALSE#        FALSE# ""
     * !null    !null    false      TRUE*         true  "# " + message
     * !null    !null     true     FALSE#        FALSE# ""
     * !null    !null    false      true         false  "# " + message
     * !null    !null     true     FALSE#        FALSE# ""
     * !null    !null    false      TRUE*         true  "# " + message
     * !null    !null     true     FALSE#        FALSE# ""
     * 
     * # = implied by quiet
     * * = implied by extraVerbose
     * </pre>
     */

    String cmd = "message"; //$NON-NLS-1$

    verifyAddVerboseMessage(null, null, false, false, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, null, true, false, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, null, false, false, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, null, true, false, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, null, false, true, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, null, true, true, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, null, false, true, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, null, true, true, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, cmd, false, false, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, cmd, true, false, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, cmd, false, false, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, cmd, true, false, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, cmd, false, true, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, cmd, true, true, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, cmd, false, true, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(null, cmd, true, true, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, null, false, false, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, null, true, false, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, null, false, false, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, null, true, false, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, null, false, true, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, null, true, true, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, null, false, true, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, null, true, true, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, cmd, false, false, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, cmd, true, false, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, cmd, false, false, true,
        ShellScriptListenerImpl.SHELL_COMMENT_LINE_PREFIX + cmd);
    verifyAddVerboseMessage(dummyPrintStream, cmd, true, false, true, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, cmd, false, true, false,
        ShellScriptListenerImpl.SHELL_COMMENT_LINE_PREFIX + cmd);
    verifyAddVerboseMessage(dummyPrintStream, cmd, true, true, false, ""); //$NON-NLS-1$
    verifyAddVerboseMessage(dummyPrintStream, cmd, false, true, true, ShellScriptListenerImpl.SHELL_COMMENT_LINE_PREFIX
        + cmd);
    verifyAddVerboseMessage(dummyPrintStream, cmd, true, true, true, ""); //$NON-NLS-1$
  }

  @Test
  public void testCommandListToString_Null() {
    List<String> commandList = null;
    int prefixSpaces = 0;

    String result = shellScriptListenerImpl.commandListToString(commandList, prefixSpaces);

    assertThat(result, equalTo("")); //$NON-NLS-1$
  }

  @Test
  public void testCommandListToString_Empty() {
    List<String> commandList = new LinkedList<>();
    int prefixSpaces = 0;

    String result = shellScriptListenerImpl.commandListToString(commandList, prefixSpaces);

    assertThat(result, equalTo("")); //$NON-NLS-1$
  }

  @Test
  public void testCommandListToString_Prefix() {
    List<String> commandList = new LinkedList<>();
    int prefixSpaces = 3;

    String result = shellScriptListenerImpl.commandListToString(commandList, prefixSpaces);

    assertThat(result, equalTo("   ")); //$NON-NLS-1$
  }

  @Test
  public void testCommandListToString_Normal() {
    List<String> commandList = new LinkedList<>();
    commandList.add("arg1"); //$NON-NLS-1$
    commandList.add("arg2"); //$NON-NLS-1$
    commandList.add("-q"); //$NON-NLS-1$
    commandList.add("-t"); //$NON-NLS-1$
    commandList.add("arg3"); //$NON-NLS-1$
    commandList.add("-equals="); //$NON-NLS-1$
    commandList.add("test"); //$NON-NLS-1$
    commandList.add("arg4"); //$NON-NLS-1$
    int prefixSpaces = 3;

    String result = shellScriptListenerImpl.commandListToString(commandList, prefixSpaces);

    assertThat(result, equalTo("   \"arg1\" \\\n" + "     \"arg2\" \\\n" + "     -q \\\n" + "     -t \"arg3\" \\\n" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        + "     -equals=\"test\" \\\n" + "     \"arg4\"")); //$NON-NLS-1$ //$NON-NLS-2$
  }
}
