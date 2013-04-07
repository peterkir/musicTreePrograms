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

  private void verifyAddCommand(PrintStream out, String command, boolean verbose, boolean quiet, String result) {
    shellScriptListenerImpl.setOut(out);
    shellScriptListenerImpl.setVerbose(verbose, quiet);
    shellScriptListenerImpl.addCommand(command);
    assertThat(dummyPrintStream.sb.toString(), equalTo(result));
    dummyPrintStream.sb.delete(0, dummyPrintStream.sb.length());
  }

  @Test
  public void testAddCommand() {
    /**
     * <pre>
     *   out  command  verbose  result
     *  null     null    false  ""
     *  null     null     true  ""
     *  null    !null    false  ""
     *  null    !null     true  ""
     * !null     null    false  ""
     * !null     null     true  ""
     * !null    !null    false  ""
     * !null    !null     true  command
     * </pre>
     */

    String cmd = "command"; //$NON-NLS-1$

    verifyAddCommand(null, null, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, null, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(null, cmd, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, null, true, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, cmd, false, false, ""); //$NON-NLS-1$
    verifyAddCommand(dummyPrintStream, cmd, true, false, cmd);
  }

  private void verifyAddMessage(PrintStream out, String command, boolean verbose, boolean quiet, String result) {
    shellScriptListenerImpl.setOut(out);
    shellScriptListenerImpl.setVerbose(verbose, quiet);
    shellScriptListenerImpl.addMessage(command);
    assertThat(dummyPrintStream.sb.toString(), equalTo(result));
    dummyPrintStream.sb.delete(0, dummyPrintStream.sb.length());
  }

  @Test
  public void testAddMessage() {
    /**
     * <pre>
     *   out  message  verbose  result
     *  null     null    false  ""
     *  null     null     true  ""
     *  null    !null    false  ""
     *  null    !null     true  ""
     * !null     null    false  ""
     * !null     null     true  ""
     * !null    !null    false  "# " + message
     * !null    !null     true  "# " + message
     * </pre>
     */

    String cmd = "message"; //$NON-NLS-1$

    verifyAddMessage(null, null, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, null, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(null, cmd, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, false, false, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, null, true, false, ""); //$NON-NLS-1$
    verifyAddMessage(dummyPrintStream, cmd, false, false, ShellScriptListenerImpl.SHELL_COMMENT_LINE_PREFIX + cmd);
    verifyAddMessage(dummyPrintStream, cmd, true, false, ShellScriptListenerImpl.SHELL_COMMENT_LINE_PREFIX + cmd);
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
