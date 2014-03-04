package nl.pelagic.audio.conversion.flac2mp3;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import nl.pelagic.audio.conversion.flac2mp3.i18n.Messages;

/**
 * A pipe between two processes
 */
public class Pipe implements Runnable {

  /*
   * Exit codes, OR mask bits
   */

  /** 'no errors' exit value */
  public static final int EXIT_OK = 0x00;

  /** 'read error' exit value bit mask */
  public static final int EXIT_ERROR_READ = 0x01;

  /** 'write error' exit value bit mask */
  public static final int EXIT_ERROR_WRITE = 0x02;

  /** 'interrupted' exit value bit mask */
  public static final int EXIT_ERROR_INTERRUPTED = 0x04;

  /** the output stream of the source process */
  private InputStream srcOutputStream;

  /** the input stream of the destination process */
  private OutputStream dstInputStream;

  /** the pipe buffer size */
  private static final int buffersize = 1024 * 256;

  /** the pipe buffer */
  private byte[] buffer = new byte[buffersize];

  /** the pipe exit value */
  private AtomicInteger exitValue = new AtomicInteger(EXIT_OK);

  /**
   * @return the pipe exit value, bit mask of EXIT_* values
   */
  public int getExitValue() {
    return exitValue.get();
  }

  /**
   * Constructor
   * 
   * @param srcOutputStream the output stream of the source process
   * @param dstInputStream the input stream of the destination process
   */
  public Pipe(InputStream srcOutputStream, OutputStream dstInputStream) {
    super();
    if ((srcOutputStream == null) || (dstInputStream == null)) {
      String s = srcOutputStream == null ? Messages.getString("Pipe.0") : ""; //$NON-NLS-1$//$NON-NLS-2$
      String s2 = dstInputStream == null ? Messages.getString("Pipe.1") : ""; //$NON-NLS-1$//$NON-NLS-2$
      if ((s.length() > 0) && (s2.length() > 0)) {
        s = s + Messages.getString("Pipe.2"); //$NON-NLS-1$
      }
      s = s + s2;
      throw new IllegalArgumentException(s);
    }
    this.srcOutputStream = srcOutputStream;
    this.dstInputStream = dstInputStream;
  }

  /*
   * State
   */

  /** idle state */
  private static final int STATE_IDLE = 0;

  /** running state */
  private static final int STATE_RUNNING = 1;

  /** stopping state */
  private static final int STATE_STOPPING = 2;

  /** state of the pipe */
  private AtomicInteger state = new AtomicInteger(STATE_IDLE);

  /*
   * Stop
   */

  /**
   * Signal the pipe to stop. Returns when the pipe has stopped.
   */
  public void signalStop() {
    int currentState = state.getAndSet(STATE_STOPPING);

    exitValue.set(EXIT_ERROR_INTERRUPTED);

    if (currentState != STATE_RUNNING) {
      /* not running: no need to wait for idle */
      return;
    }

    /* wait for idle of the pipe */
    while (state.get() != STATE_IDLE) {
      try {
        Thread.sleep(1);
      }
      catch (InterruptedException e) {
        /* swallow */
      }
    }
  }

  /*
   * Runnable
   */

  @Override
  public void run() {
    if (!state.compareAndSet(STATE_IDLE, STATE_RUNNING)) {
      /* not idle: can't run */
      return;
    }

    int read = -2;
    try {
      while (state.get() == STATE_RUNNING) {
        read = -2;
        read = srcOutputStream.read(buffer);
        if (read < 0) {
          /* EOF */
          break;
        }
        dstInputStream.write(buffer, 0, read);
      }
    }
    catch (Throwable e) {
      exitValue.compareAndSet(EXIT_OK, (read == -2) ? EXIT_ERROR_READ : EXIT_ERROR_WRITE);
    }
    finally {
      try {
        srcOutputStream.close();
      }
      catch (IOException e) {
        /* swallow */
      }
      try {
        dstInputStream.close();
      }
      catch (IOException e) {
        /* swallow */
      }

      state.set(STATE_IDLE);
    }
  }
}
