package nl.pelagic.audio.conversion.flac2mp3.testhelpers;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import org.junit.Ignore;

@Ignore
@SuppressWarnings({
    "nls", "javadoc"
})
public class PipeTestOutputStream extends OutputStream {

  public List<String> writtenStrings = new LinkedList<>();

  public boolean throwExceptionDuringWrite = false;
  public boolean throwExceptionDuringClose = false;

  @Override
  public void write(byte[] b) throws IOException {
    write(b, 0, b.length);
  }

  @Override
  public void write(byte[] b, int off, int len) throws IOException {
    if (throwExceptionDuringWrite) {
      throw new IOException();
    }

    String s = new String(b, off, len);
    if (!s.isEmpty()) {
      writtenStrings.add(s);
    }
  }

  @Override
  public void flush() throws IOException {
    /* do nothing */
  }

  @Override
  public void close() throws IOException {
    if (throwExceptionDuringClose) {
      throw new IOException();
    }
  }

  @Override
  public void write(int b) throws IOException {
    throw new IOException("unsupported method");
  }
}
