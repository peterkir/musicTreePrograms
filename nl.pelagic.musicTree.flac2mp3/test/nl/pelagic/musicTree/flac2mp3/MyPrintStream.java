package nl.pelagic.musicTree.flac2mp3;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;

@Ignore
@SuppressWarnings({
    "nls", "javadoc"
})
public class MyPrintStream extends PrintStream {
  public static final String FLUSH = "FLUSH";
  public List<String> strings = new LinkedList<>();

  public MyPrintStream(String fileName) throws FileNotFoundException {
    super(fileName);
  }

  @Override
  public void flush() {
    strings.add(FLUSH);
  }

  @Override
  public void println() {
    strings.add("");
  }

  @Override
  public void println(String x) {
    strings.add(x);
  }

  @Override
  public PrintStream printf(String format, Object... args) {
    strings.add(String.format(format, args));
    return this;
  }

  /*
   * (non-Javadoc)
   * @see java.io.PrintStream#write(byte[], int, int)
   */
  @Override
  public void write(byte[] buf, int off, int len) {
    strings.add(new String(buf, off, len));
  }
}
