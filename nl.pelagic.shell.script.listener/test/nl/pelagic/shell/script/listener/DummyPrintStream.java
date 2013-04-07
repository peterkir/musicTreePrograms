package nl.pelagic.shell.script.listener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.junit.Ignore;

@Ignore
@SuppressWarnings("javadoc")
public class DummyPrintStream extends PrintStream {

  public DummyPrintStream(File file) throws FileNotFoundException {
    super(file);
  }

  public StringBuilder sb = new StringBuilder();

  @Override
  public void println(String x) {
    sb.append(x);
  }
}
