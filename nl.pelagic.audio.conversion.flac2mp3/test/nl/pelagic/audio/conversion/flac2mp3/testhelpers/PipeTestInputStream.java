package nl.pelagic.audio.conversion.flac2mp3.testhelpers;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Ignore;

@Ignore
@SuppressWarnings("javadoc")
public class PipeTestInputStream extends InputStream {

  public static final int CMD_STRING = 0x00;
  public static final int CMD_EOF = 0x01;
  public static final int CMD_EXCEPTION = 0x02;
  public static final int CMD_STALL = 0x03;

  public static class Entry {
    public int commandCode;
    public String message;

    public Entry(int commandCode, String msg) {
      super();
      this.commandCode = commandCode;
      this.message = msg;
    }
  }

  public List<Entry> entries = new LinkedList<>();
  private int entryIndex = 0;

  public boolean throwExceptionDuringClose = false;

  private Object unstall = new Object();
  public AtomicBoolean stalled = new AtomicBoolean(false);

  public void unstall() {
    synchronized (unstall) {
      unstall.notifyAll();
    }
  }

  @Override
  public int read(byte[] b) throws IOException {
    return read(b, 0, b.length);

  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    assert (entryIndex < entries.size());
    Entry entry = entries.get(entryIndex);

    entryIndex++;
    switch (entry.commandCode) {
      case CMD_STRING:
        String str = entry.message;
        byte[] strbytes = str.getBytes();
        int strsize = str.length();
        int bsize = len;
        int copysize = Math.min(strsize, bsize);
        System.arraycopy(strbytes, 0, b, off, copysize);
        if (strsize > bsize) {
          entryIndex--;
          entry.message = entry.message.substring(copysize);
        }
        return copysize;

      case CMD_EOF:
        return -1;

      case CMD_EXCEPTION:
        throw new IOException(entry.message);

      case CMD_STALL:
        synchronized (unstall) {
          stalled.set(true);
          try {
            unstall.wait();
          }
          catch (InterruptedException e) {
            /* swallow */
          }
          stalled.set(false);
        }
        return 0;

      default:
        throw new IllegalStateException("Unknown command code " + entry.commandCode); //$NON-NLS-1$
    }
  }

  @Override
  public long skip(long n) throws IOException {
    throw new IOException();
  }

  @Override
  public int available() throws IOException {
    throw new IOException();
  }

  @Override
  public void close() throws IOException {
    if (throwExceptionDuringClose) {
      throw new IOException();
    }
  }

  @Override
  public synchronized void mark(int readlimit) {
    throw new RuntimeException();
  }

  @Override
  public synchronized void reset() throws IOException {
    throw new IOException();
  }

  @Override
  public boolean markSupported() {
    return false;
  }

  @Override
  public int read() throws IOException {
    throw new IOException();
  }
}
