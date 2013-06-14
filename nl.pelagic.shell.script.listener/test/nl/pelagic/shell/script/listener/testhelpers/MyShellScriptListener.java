package nl.pelagic.shell.script.listener.testhelpers;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import nl.pelagic.shell.script.listener.api.ShellScriptListener;

import org.junit.Ignore;

@Ignore
@SuppressWarnings({
    "nls", "javadoc"
})
public class MyShellScriptListener implements ShellScriptListener {
  public class Pair {
    public int type = -1;
    public String string = null;

    public Pair(int type, String string) {
      super();
      this.type = type;
      this.string = string;
    }
  }

  public static final int TYPE_COMMAND = 0;
  public static final int TYPE_VERBOSEMESSAGE = 1;
  public static final int TYPE_MESSAGE = 2;
  public static final int TYPE_VERBOSE = 3;

  public List<Pair> received = new LinkedList<>();

  public List<String> stallPoints = new LinkedList<>();
  public int stallIndex = 0;
  public AtomicBoolean stalled = new AtomicBoolean(false);

  private void doStallIfNeeded(String msg) {
    if ((stallIndex + 1) > stallPoints.size()) {
      return;
    }

    String[] msgsplit = msg.split("\n");

    String regex = stallPoints.get(stallIndex);
    if (!msgsplit[0].matches(regex)) {
      return;
    }

    stallIndex++;
    stalled.set(true);

    while (stalled.get()) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  synchronized public void addCommand(String command) {
    received.add(new Pair(TYPE_COMMAND, command));
    doStallIfNeeded(command);
  }

  @Override
  synchronized public void addMessage(String message) {
    received.add(new Pair(TYPE_MESSAGE, message));
    doStallIfNeeded(message);
  }

  @Override
  synchronized public void addVerboseMessage(String message) {
    received.add(new Pair(TYPE_VERBOSEMESSAGE, message));
    doStallIfNeeded(message);
  }

  @Override
  synchronized public String commandListToString(List<String> commandList, int prefixSpaces) {
    String s = "";
    for (String cmd : commandList) {
      if (s.length() > 0) {
        s = s + " ";
      }
      s = s + cmd;
    }

    return String.format("%" + (prefixSpaces == 0 ? "" : Integer.valueOf(prefixSpaces).toString()) + "s%s", "", s);
  }

  @Override
  synchronized public void setVerbose(boolean verbose, boolean extraVerbose, boolean quiet) {
    received.add(new Pair(TYPE_VERBOSE, "verbose=" + verbose + "extraVerbose=" + extraVerbose + ", quiet=" + quiet));
  }
}
